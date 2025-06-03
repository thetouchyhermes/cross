package it.unipi.cross.orderbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import it.unipi.cross.data.Type;

public class OrderBook {

   // unique order IDs
   private final AtomicInteger idGenerator = new AtomicInteger(1);

   // maps orderId to each Order
   private final Map<Integer, Order> orderMap = new ConcurrentHashMap<>();

   // Limit orders book sides
   private final NavigableSet<LimitOrder> bidBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).reversed().thenComparingLong(LimitOrder::getTimestamp));
   private final NavigableSet<LimitOrder> askBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).thenComparingLong(LimitOrder::getTimestamp));

   // To handle stop orders (used only in synchronized methods)
   private final List<StopOrder> stopOrders = new ArrayList<>();

   // Listeners for completion notification
   // private final OrderListener listener = new OrderListener();

   private int bestBidPrice = -1;
   private int bestAskPrice = -1;

   public OrderBook(List<Order> orders) {
      if (orders != null && !orders.isEmpty()) {
         for (Order order : orders) {
            orderMap.put(order.getOrderId(), order);
         }

         int lastId = Collections.max(orderMap.keySet());
         idGenerator.set(lastId + 1);

         for (Order order : orderMap.values()) {
            if (order.getType() == Type.ask) {
               askBook.add((LimitOrder) order);
            } else if (order.getType() == Type.bid) {
               bidBook.add((LimitOrder) order);
            }
         }
      }
   }

   /**
    * Inserts an order into the order book.
    * <p>
    * This method handles the insertion of different types of orders (limit, stop,
    * market)
    * into the order book. It assigns a unique order ID if necessary, checks for
    * duplicate
    * orders, and updates the relevant order book structures. For limit and stop
    * orders,
    * it attempts to match them using the matching algorithm. For market orders, it
    * tries
    * to execute the order immediately.
    * </p>
    *
    * @param order the {@link Order} to be inserted
    * @return the assigned order ID if the order is successfully inserted and
    *         processed;
    *         -1 if the order is invalid, already exists, or could not be matched
    *         (for market orders)
    */
   public synchronized int insertOrder(Order order) {

      if (order.getSize() <= 0)
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
            MatchingAlgorithm.matchStopOrder(this, stop);
            break;

         case market:
            boolean success = MatchingAlgorithm.matchMarketOrder(this, (MarketOrder) order);
            return (success) ? orderId : -1;
      }

      return orderId;

   }

   /**
    * Deletes an order from the order book by its unique order ID.
    * <p>
    * This method removes the order from the internal order map and, depending on
    * the order type,
    * also removes it from the corresponding order book (bidBook, askBook, or
    * stopOrders).
    * If the order does not exist, the method returns {@code false}.
    * </p>
    *
    * @param orderId the unique identifier of the order to be deleted
    * @return {@code true} if the order was found and deleted; {@code false}
    *         otherwise
    */
   public synchronized boolean deleteOrder(int orderId) {
      if (!orderMap.containsKey(orderId))
         return false;

      Order order = orderMap.remove(orderId);

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
      // no actions are needed to delete market orders

      return true;
   }

   // matching algorithm access

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
    * Checks if the best bid or ask prices in the order book have changed.
    * <p>
    * Compares the current best bid and ask prices with the previously stored
    * values.
    * If either price has changed, updates the stored values, triggers a check for
    * stop orders,
    * and returns {@code true}. Otherwise, returns {@code false}.
    *
    * @return {@code true} if the best bid or ask price has changed; {@code false}
    *         otherwise.
    */
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
    * Checks all stop orders in the order book and removes those that are
    * triggered.
    * <p>
    * Iterates through the list of stop orders and, for each stop order, determines
    * if it should be executed using the
    * {@link MatchingAlgorithm#matchStopOrder(OrderBook, StopOrder)} method.
    * If a stop order is triggered, it is removed from the list.
    * </p>
    */
   public void checkStopOrders() {

      Iterator<StopOrder> it = stopOrders.iterator();
      while (it.hasNext()) {
         StopOrder stop = it.next();
         if (MatchingAlgorithm.matchStopOrder(this, stop)) {
            it.remove();

            // convert to market order
            MarketOrder market = StopOrder.convertToMarket(stop);
            orderMap.remove(stop.getOrderId());
            int fail = insertOrder(market);
            if (fail == -1) {
               // manage market insertion fail after conversion
               System.err.println("Failed insertion of market order after conversion from stop order. orderId: " + stop.getOrderId());
            }               
         }
      }

   }

}
