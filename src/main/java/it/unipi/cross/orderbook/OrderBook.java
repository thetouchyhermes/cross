package it.unipi.cross.orderbook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;

public class OrderBook {

   // unique order IDs
   private final AtomicInteger idGenerator = new AtomicInteger(1);

   // maps orderId to each Order (sync???)
   private final ConcurrentMap<Integer, Order> orderMap = new ConcurrentHashMap<>();

   // Limit orders book sides
   private final NavigableSet<LimitOrder> bidBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).reversed().thenComparingLong(LimitOrder::getTimestamp));
   private final NavigableSet<LimitOrder> askBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).thenComparingLong(LimitOrder::getTimestamp));

   // To handle stop orders (sync???)
   private final List<StopOrder> stopOrders = new ArrayList<>();

   // Listeners for completion notification
   private final OrderListener listener = new OrderListener();

   private int bestBidPrice = -1;
   private int bestAskPrice = -1;

   public OrderBook() {
   }

   /**
    * Inserts a new order into the order book and attempts to match it according to
    * its type.
    * <p>
    * Supported order types are:
    * <ul>
    * <li>MarketOrder: Immediately attempts to match with the best available
    * orders.</li>
    * <li>LimitOrder: Adds to the bid or ask book and attempts to match if
    * possible.</li>
    * <li>StopOrder: Adds to the stop orders list and attempts to match if trigger
    * conditions are met.</li>
    * </ul>
    * </p>
    * 
    * @param order the {@link Order} to be inserted and matched
    * @return the generated order ID if the order was successfully matched or
    *         added;
    *         -1 if the order could not be processed
    */
   public synchronized int insertOrder(Order order) {
      if (order.getSize() <= 0)
         return -1;

      int orderId = idGenerator.getAndIncrement();
      order.setOrderId(orderId);

      orderMap.put(orderId, order);

      Type type = order.getType();

      boolean success = false;

      switch (order.getOrderType()) {
         case market:
            success = MatchingAlgorithm.matchMarketOrder(this, (MarketOrder) order);
            break;
         case limit:
            LimitOrder limitOrder = (LimitOrder) order;
            if (type == Type.bid)
               bidBook.add(limitOrder);
            else if (type == Type.ask)
               askBook.add(limitOrder);
            success = MatchingAlgorithm.matchLimitOrder(this, limitOrder);
            break;
         case stop:
            StopOrder stopOrder = (StopOrder) order;
            stopOrders.add(stopOrder);
            success = MatchingAlgorithm.matchStopOrder(this, stopOrder);
            break;
      }
      if (success)
         return orderId;
      else
         return -1;
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
      Order order = orderMap.remove(orderId);
      if (order == null)
         return false;

      switch (order.getOrderType()) {
         case limit:
            LimitOrder limitOrder = (LimitOrder) order;
            Type type = limitOrder.getType();
            if (type == Type.bid)
               bidBook.remove(limitOrder);
            else if (type == Type.ask)
               askBook.remove(limitOrder);
            break;
         case stop:
            stopOrders.remove(order);
            break;
         case market:
            break;
      }

      return true;
   }

   // matching algorithm access

   public NavigableSet<LimitOrder> getBidBook() {
      return bidBook;
   }

   public NavigableSet<LimitOrder> getAskBook() {
      return askBook;
   }

   public List<StopOrder> getStopOrders() {
      return stopOrders;
   }

   public ConcurrentMap<Integer, Order> getOrderMap() {
      return orderMap;
   }

   /**
    * public int getBestBidPrice() {
    * return bestBidPrice;
    * }
    * 
    * public int getBestAskPrice() {
    * return bestAskPrice;
    * }
    * 
    * public void complete(Order order) {
    * // remove from orderbook
    * // if market respond with orderId to user
    * // archive trade
    * // send notification of trade completion
    * }
    * 
    * public void incomplete(Order order) {
    * // remove from orderbook
    * // if market respond with -1 to user
    * }
    * 
    * public StopOrder getStopOrderById(int orderId) {
    * return stopOrders.get(orderId);
    * }
    */

}
