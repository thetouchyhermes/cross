package it.unipi.cross.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.OrderType;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Trade;
import it.unipi.cross.data.Type;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.MessageResponse;
import it.unipi.cross.json.Notification;
import it.unipi.cross.json.OrderResponse;
import it.unipi.cross.json.Response;
import it.unipi.cross.network.UdpNotifier;
import it.unipi.cross.persistence.PersistenceManager;

/**
 * Principal structure of order handling of the app. Contains all order
 * management methods, including insertion, deletion, matching, stop orders
 * triggering and persistence starting data structures
 **/
public class OrderBook {

   // unique order IDs
   private final AtomicInteger idGenerator = new AtomicInteger(1);

   // maps orderId to each Order
   private final Map<Integer, Order> orderMap = new ConcurrentHashMap<>();

   // maps orderId to each Trade (completed order)
   private final Map<Integer, Trade> tradeMap = new ConcurrentHashMap<>();

   // Limit orders book sides
   private final NavigableSet<LimitOrder> bidBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).reversed().thenComparingLong(LimitOrder::getTimestamp));
   private final NavigableSet<LimitOrder> askBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).thenComparingLong(LimitOrder::getTimestamp));

   // To handle stop orders (used only in synchronized methods)
   private final List<StopOrder> stopOrders = new LinkedList<>();

   // udp connection to notify order completion
   private final UdpNotifier udpNotifier;

   // persistence manager price pre-calculation backup
   private final PersistenceManager persistenceManager;

   private int bestBidPrice = -1;
   private int bestAskPrice = -1;

   public OrderBook(List<Order> orders, int lastOrderId, UdpNotifier udpNotifier,
         PersistenceManager persistenceManager) {

      this.udpNotifier = udpNotifier;
      this.persistenceManager = persistenceManager;

      if (orders != null && !orders.isEmpty()) {
         for (Order order : orders) {
            orderMap.put(order.getOrderId(), order);
         }

         for (Order order : orderMap.values()) {
            if (order.getType() == Type.ask) {
               askBook.add((LimitOrder) order);
            } else if (order.getType() == Type.bid) {
               bidBook.add((LimitOrder) order);
            }
         }

         checkBestPrices();
      }

      int lastId = lastOrderId;
      if (!orderMap.isEmpty()) {
         lastId = Math.max(Collections.max(orderMap.keySet()), lastOrderId);
      }

      this.idGenerator.set(lastId + 1);

   }

   public synchronized int insertOrder(Order order) {

      if (order.getOriginalSize() <= 0)
         return -1;

      int orderId = order.getOrderId();
      if (orderId == -1) {
         orderId = idGenerator.getAndIncrement();
         order.setOrderId(orderId);
      }

      if (orderMap.containsKey(orderId))
         return -1;

      Type type = order.getType();

      switch (order.getOrderType()) {
         case limit:
            orderMap.put(orderId, order);
            LimitOrder limit = (LimitOrder) order;
            boolean completed = MatchingAlgorithm.matchLimitOrder(this, limit);
            if (!completed) {
               if (type == Type.bid)
                  bidBook.add(limit);
               else if (type == Type.ask)
                  askBook.add(limit);
               // check new prices after book update
               checkBestPrices();
            }
            break;

         case stop:
            orderMap.put(orderId, order);
            StopOrder stop = (StopOrder) order;
            stopOrders.add(stop);
            if (MatchingAlgorithm.matchStopOrder(this, stop)) {
               checkStopOrders();
            }
            break;

         case market:
            boolean success = MatchingAlgorithm.matchMarketOrder(this, (MarketOrder) order);
            return (success) ? orderId : -1;
      }

      return orderId;

   }

   public synchronized Response cancelOrder(int orderId, String username) {
      Order order = orderMap.get(orderId);

      if (order == null)
         return new MessageResponse(101, "order does not exist or has already been finalized");
      if (order.getUsername() != null && !order.getUsername().equals(username))
         return new MessageResponse(101, "order belongs to a different user");

      orderMap.remove(orderId);
      if (order.getOrderType() == OrderType.limit) {
         LimitOrder limit = (LimitOrder) order;
         Type type = limit.getType();
         if (type == Type.bid)
            bidBook.remove(limit);
         else if (type == Type.ask)
            askBook.remove(limit);

         // check new prices after book update
         checkBestPrices();
      } else if (order.getOrderType() == OrderType.stop) {
         stopOrders.remove((StopOrder) order);
      }
      // market orders cannot be canceled anyway

      return new MessageResponse(100, "OK");
   }

   // matchingAlgorithm access

   public NavigableSet<LimitOrder> getBidBook() {
      return bidBook;
   }

   public NavigableSet<LimitOrder> getAskBook() {
      return askBook;
   }

   public Map<Integer, Order> getOrderMap() {
      return orderMap;
   }

   public int getBestBidPrice() {
      return bestBidPrice;
   }

   public int getBestAskPrice() {
      return bestAskPrice;
   }

   /**
    * Checks if the best prices in the order book have changed. Then tries to
    * trigger stop orders list
    **/
   public boolean checkBestPrices() {

      int newBestBid = bidBook.isEmpty() ? -1 : bidBook.first().getPrice();
      int newBestAsk = askBook.isEmpty() ? -1 : askBook.first().getPrice();
      boolean changed = false;

      if (newBestBid != bestBidPrice) {
         bestBidPrice = newBestBid;
         changed = true;
      }
      if (newBestAsk != bestAskPrice) {
         bestAskPrice = newBestAsk;
         changed = true;
      }
      if (changed) {
         checkStopOrders();
      }

      return changed;
   }

   /**
    * Checks all stop orders.
    * For each, checks if triggered with
    * {@link MatchingAlgorithm#matchStopOrder(OrderBook, StopOrder)}
    *
    * If a stop order is triggered, removes it from the order book, converts it to
    * a market order, notifies the owner if it fails after conversion
    **/
   public void checkStopOrders() {

      Iterator<StopOrder> it = stopOrders.iterator();
      while (it.hasNext()) {
         StopOrder stop = it.next();
         if (MatchingAlgorithm.matchStopOrder(this, stop)) {
            it.remove();

            // convert to market order
            MarketOrder market = StopOrder.convertToMarket(stop);
            orderMap.remove(stop.getOrderId());
            System.out.println("all good?");
            int fail = insertOrder(market);
            if (fail == -1) {
               // manage market insertion fail after conversion
               udpNotifier.notifyClient(stop.getUsername(), JsonUtil.toJson(new OrderResponse(stop.getOrderId())));
            }
         }
      }
   }

   // add completed order to book and notify it to orders owners
   public void insertTrade(Order firstOrder, Order secondOrder, int tradeSize, int tradePrice) {
      Trade firstTrade = new Trade(firstOrder, tradeSize, tradePrice);
      Trade secondTrade = new Trade(secondOrder, tradeSize, tradePrice);

      tradeMap.put(firstTrade.getOrderId(), firstTrade);
      tradeMap.put(secondTrade.getOrderId(), secondTrade);
      notify(firstTrade);
      notify(secondTrade);
   }

   /** Sends UDP notifications of order completion to the respective owners **/
   public void notify(Trade trade) {
      if (trade == null || trade.getOrderId() <= 0) {
         return;
      }
      Notification notification = new Notification(trade);
      udpNotifier.notifyClient(trade.getUsername(), JsonUtil.toJson(notification));
   }

   /** Retrieves a list of all limit orders from the order book **/
   public synchronized List<Order> getOrderList() {
      List<Order> orders = new ArrayList<>(orderMap.values());
      orders.removeIf(order -> order.getOrderType() != OrderType.limit);
      return orders;
   }

   /** Retrieves a copy of all trades from the order book **/
   public List<Trade> getTradeList() {
      List<Trade> trades = new ArrayList<>(tradeMap.values());
      return trades;
   }

   /** Cuts a list of all trades from the order book **/
   public synchronized List<Trade> extractTradeList() {
      List<Trade> trades = new ArrayList<>(tradeMap.values());
      tradeMap.clear();

      return trades;
   }

   // extracts immediately all saved trades in the order book to their file
   public void backupTrades() throws IOException {
      persistenceManager.saveAll(null, null, extractTradeList());
   }

}
