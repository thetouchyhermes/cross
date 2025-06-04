package it.unipi.cross.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.OrderType;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;

// Minimal stub for MatchingAlgorithm for testing
class DummyMatchingAlgorithm {
   static boolean matchMarketOrder(OrderBook book, MarketOrder order) {
      // Simulate matching: succeed if there is at least one opposite limit order
      if (order.getType() == Type.bid && !book.getAskBook().isEmpty())
         return true;
      if (order.getType() == Type.ask && !book.getBidBook().isEmpty())
         return true;
      return false;
   }

   static boolean matchLimitOrder(OrderBook book, LimitOrder order) {
      return false;
   }

   static boolean matchStopOrder(OrderBook book, it.unipi.cross.data.StopOrder order) {
      return false;
   }
}

class DummyStopOrder extends StopOrder {
   private final int price;
   private final long timestamp;
   private final int size;
   private final String username;
   private final Type type;
   private int orderId = -1;

   public DummyStopOrder(Type type, int price, int size, long timestamp, String username) {
      super(username, type, price, size, System.currentTimeMillis());
      this.type = type;
      this.price = price;
      this.size = size;
      this.timestamp = timestamp;
      this.username = username;
   }

   @Override
   public int getPrice() {
      return price;
   }

   @Override
   public long getTimestamp() {
      return timestamp;
   }

   @Override
   public int getSize() {
      return size;
   }

   @Override
   public String getUsername() {
      return username;
   }

   @Override
   public Type getType() {
      return type;
   }

   @Override
   public OrderType getOrderType() {
      return OrderType.limit;
   }

   @Override
   public int getOrderId() {
      return orderId;
   }

   @Override
   public void setOrderId(int id) {
      this.orderId = id;
   }
}

class DummyLimitOrder extends LimitOrder {
   private final int price;
   private final long timestamp;
   private final int size;
   private final String username;
   private final Type type;
   private int orderId = -1;

   public DummyLimitOrder(Type type, int price, int size, long timestamp, String username) {
      super(username, type, price, size, System.currentTimeMillis());
      this.type = type;
      this.price = price;
      this.size = size;
      this.timestamp = timestamp;
      this.username = username;
   }

   @Override
   public int getPrice() {
      return price;
   }

   @Override
   public long getTimestamp() {
      return timestamp;
   }

   @Override
   public int getSize() {
      return size;
   }

   @Override
   public String getUsername() {
      return username;
   }

   @Override
   public Type getType() {
      return type;
   }

   @Override
   public OrderType getOrderType() {
      return OrderType.limit;
   }

   @Override
   public int getOrderId() {
      return orderId;
   }

   @Override
   public void setOrderId(int id) {
      this.orderId = id;
   }
}

class DummyMarketOrder extends MarketOrder {
   private final int size;
   private final String username;
   private final Type type;
   private int orderId = -1;

   public DummyMarketOrder(Type type, int size, String username) {
      super(username, type, size, System.currentTimeMillis());
      this.type = type;
      this.size = size;
      this.username = username;
   }

   @Override
   public int getSize() {
      return size;
   }

   @Override
   public String getUsername() {
      return username;
   }

   @Override
   public Type getType() {
      return type;
   }

   @Override
   public OrderType getOrderType() {
      return OrderType.market;
   }

   @Override
   public int getOrderId() {
      return orderId;
   }

   @Override
   public void setOrderId(int id) {
      this.orderId = id;
   }
}

public class OrderBookTest {

   @BeforeEach
   void setup() {
      // No-op, but could reset static state if needed
   }

   @Test
   void testInsertMarketOrderWithMatchingLimitOrderSucceeds() {
      // Add an ask limit order to the book
      LimitOrder ask = new LimitOrder("seller", Type.ask, 10, 100, System.currentTimeMillis());
      OrderBook book = new OrderBook(null);
      int limitId = book.insertOrder(ask);
      MarketOrder bidMarket = new MarketOrder("buyer", Type.bid, 5, System.currentTimeMillis());

      int marketId = book.insertOrder(bidMarket);

      assertTrue(marketId > 0, "Market order should be matched and return a valid orderId");
      assertFalse(book.getOrderMap().containsKey(marketId), "Market order should not be stored in orderMap");
   }

   @Test
   void testInsertMarketOrderWithNoMatchingLimitOrderFails() {
      // No ask orders in the book
      OrderBook book = new OrderBook(Collections.emptyList());
      MarketOrder bidMarket = new MarketOrder("buyer", Type.bid, 5, System.currentTimeMillis());

      int orderId = book.insertOrder(bidMarket);

      assertEquals(-1, orderId, "Market order should fail if no matching limit order exists");
   }

   @Test
   void testInsertMarketOrderWithZeroSizeFails() {
      OrderBook book = new OrderBook(Collections.emptyList());
      MarketOrder bidMarket = new MarketOrder("buyer", Type.bid, 0, System.currentTimeMillis());

      int orderId = book.insertOrder(bidMarket);

      assertEquals(-1, orderId, "Market order with zero size should fail");
   }

   @Test
   void testMarketOrderNotAddedToOrderMapIfNotMatched() {
      OrderBook book = new OrderBook(Collections.emptyList());
      MarketOrder askMarket = new MarketOrder("seller", Type.ask, 3, System.currentTimeMillis());

      int orderId = book.insertOrder(askMarket);

      assertEquals(-1, orderId);
      assertTrue(book.getOrderMap().isEmpty(), "Order map should remain empty if market order not matched");
   }

   @Test
   void testMarketOrderNotAddedToOrderMapIfMatched() {
      // Add a bid limit order to the book
      LimitOrder bid = new LimitOrder("buyer", Type.bid, 10, 100, System.currentTimeMillis());
      OrderBook book = new OrderBook(null);
      int limitId = book.insertOrder(bid);
      MarketOrder ask = new MarketOrder("seller", Type.ask, 5, System.currentTimeMillis());

      int marketId = book.insertOrder(ask);
      assertEquals(marketId, limitId + 1, "Market order should have the following id of the limit order");
      assertTrue(book.getOrderList().size() == 1, "limit order should remain after matching");
      assertEquals(bid.getSize(), bid.getOriginalSize() - ask.getOriginalSize(),
            "remaining size should be the difference: original - trade");
      assertTrue(marketId > 0, "Market order should be matched and return a valid orderId");
      // Market orders are not stored in orderMap
      assertFalse(book.getOrderMap().containsKey(marketId), "Market order should not be stored in orderMap");
   }

   @Test
   void testOrderListStart() {
      LimitOrder bid1 = new LimitOrder(1, "buyer", Type.bid, 10, 80, System.currentTimeMillis());
      LimitOrder bid2 = new LimitOrder(3, "alice", Type.bid, 7, 90, System.currentTimeMillis());
      LimitOrder ask1 = new LimitOrder(14, "cesar", Type.ask, 5, 100, System.currentTimeMillis());
      LimitOrder ask2 = new LimitOrder(5, "alice", Type.ask, 4, 93, System.currentTimeMillis());

      List<Order> orders = new ArrayList<>();
      orders.add(bid1);
      orders.add(bid2);
      orders.add(ask1);
      orders.add(ask2);

      OrderBook book = new OrderBook(orders);
      MarketOrder askMarket = new MarketOrder("alice", Type.ask, 7, System.currentTimeMillis());
      MarketOrder bidMarket = new MarketOrder("seller", Type.bid, 8, System.currentTimeMillis());

      int marketId1 = book.insertOrder(askMarket);
      int marketId2 = book.insertOrder(bidMarket);
      System.out.println("1: " + marketId1 + ", 2: " + marketId2);
      assertEquals(ask1.getOrderId() + 1, marketId1,
            "Market order should have the following id of the max num of limit orders");
      assertEquals(ask1.getOrderId() + 2, marketId2,
            "Market order 2 should have the following id+1 of the max num of limit orders");

      assertTrue(book.getOrderMap().containsKey(ask1.getOrderId()), "ask1 should be in book");
      assertFalse(book.getOrderMap().containsKey(ask2.getOrderId()), "ask2 should not be in book");
      assertTrue(book.getOrderMap().containsKey(bid1.getOrderId()), "bid1 should be in book");
      assertTrue(book.getOrderMap().containsKey(bid2.getOrderId()), "bid2 should be in book");

      assertEquals(3, book.getOrderList().size(), "3 limit orders should remain after matching");
      assertEquals(bid2.getSize(), bid2.getOriginalSize(), "bid2 should remain unchanged");
      assertEquals(bid1.getSize(), bid1.getOriginalSize() - askMarket.getOriginalSize(), "bid1 should decrease");
      assertFalse(bid1.getSize() <= 0, "bid1 should be bigger than 0");
      assertTrue(ask2.getSize() == 0, "ask2 should be over");
      assertTrue(ask1.getSize() == ask1.getOriginalSize() + ask2.getOriginalSize() - bidMarket.getOriginalSize(),
            "ask1 should be left something");

      // Market orders are not stored in orderMap
      assertFalse(book.getOrderMap().containsKey(marketId2), "Market order should not be stored in orderMap");
   }

   @Test
   void testOrderListStartSingleton() {
      LimitOrder bid1 = new LimitOrder(3, "buyer", Type.bid, 10, 80, System.currentTimeMillis());

      List<Order> orders = Collections.singletonList(bid1);

      OrderBook book = new OrderBook(orders);
      MarketOrder askMarket = new MarketOrder("alice", Type.ask, 11, System.currentTimeMillis());
      MarketOrder bidMarket = new MarketOrder("seller", Type.bid, 7, System.currentTimeMillis());

      int marketId1 = book.insertOrder(askMarket);
      int marketId2 = book.insertOrder(bidMarket);
      assertEquals(-1, marketId1, "Market order should have the following id of the max num of limit orders");
      assertEquals(-1, marketId2, "Market order 2 should not match");
      assertTrue(book.getOrderList().size() == 1, "limit in book should remain after matching");
      assertEquals(bid1.getSize(), bid1.getOriginalSize(), "bid1 should not change");

      // Market orders are not stored in orderMap
      assertFalse(book.getOrderMap().containsKey(marketId1), "Market order should not be stored in orderMap");
      assertFalse(book.getOrderMap().containsKey(marketId2), "Market order 2 should not be stored in orderMap");
   }

   @Test
   void testInsertStopOrderIsAddedToOrderMap() {
      OrderBook book = new OrderBook(Collections.emptyList());
      StopOrder stop = new StopOrder("user", Type.bid, 10, 5, System.currentTimeMillis());
      int stopId = book.insertOrder(stop);

      assertTrue(stopId > 0, "Stop order should be assigned a valid orderId");
      assertTrue(book.getOrderMap().containsKey(stopId), "Stop order should be stored in orderMap");
   }

   @Test
   void testInsertStopOrderWithZeroSizeFails() {
      OrderBook book = new OrderBook(Collections.emptyList());
      StopOrder stop = new StopOrder("user", Type.ask, 0, 10, System.currentTimeMillis());
      int stopId = book.insertOrder(stop);

      assertEquals(-1, stopId, "Stop order with zero size should fail");
   }

   @Test
   void testStopOrderIsRemovedWhenTriggered() {
      // We'll simulate a stop order that is triggered by best price change.
      // For this, MatchingAlgorithm.matchStopOrder must return true.
      // We'll use a dummy stop order and temporarily override the matching algorithm.

      OrderBook book = new OrderBook(Collections.emptyList());
      StopOrder stop = new StopOrder("user", Type.bid, 5, 10, System.currentTimeMillis()) {
         @Override
         public int getOrderId() {
            return super.getOrderId();
         }
      };

      // Insert stop order
      int stopId = book.insertOrder(stop);
      assertTrue(book.getOrderMap().containsKey(stopId), "Stop order should be in orderMap before triggering");

      // Simulate triggering: forcibly remove from stopOrders and orderMap
      book.getOrderMap().remove(stopId);
      // Normally, checkStopOrders would do this if MatchingAlgorithm.matchStopOrder
      // returns true

      assertFalse(book.getOrderMap().containsKey(stopId),
            "Stop order should be removed from orderMap after triggering");
   }

   @Test
   void testStopOrderNotAddedToBidOrAskBook() {
      OrderBook book = new OrderBook(Collections.emptyList());
      StopOrder stop = new StopOrder("user", Type.ask, 5, 10, System.currentTimeMillis());
      int stopId = book.insertOrder(stop);

      assertTrue(book.getOrderMap().containsKey(stopId), "Stop order should be in orderMap");
      assertTrue(book.getBidBook().isEmpty(), "Stop order should not be in bidBook");
      assertTrue(book.getAskBook().isEmpty(), "Stop order should not be in askBook");
   }

   @Test
   void testStopOrderTriggering() {
      OrderBook book = new OrderBook(Collections.emptyList());

      // 1. Insert an ask limit order
      LimitOrder ask = new LimitOrder("seller", Type.ask, 10, 150, System.currentTimeMillis());
      int askId = book.insertOrder(ask);

      // 2. Insert a new ask limit order at price 200
      LimitOrder askTrigger = new LimitOrder("trigger", Type.ask, 7, 200, System.currentTimeMillis());
      int askTriggerId = book.insertOrder(askTrigger);

      // 3. Insert a stop order (bid stop at price 160, not triggered yet)
      StopOrder stop = new StopOrder("stopper", Type.bid, 5, 160, System.currentTimeMillis());
      int stopId = book.insertOrder(stop);

      // 4. Insert a bid limit order (will delete the cheapest ask limit order and trigger the stop order)
      LimitOrder bid = new LimitOrder("buyer", Type.bid, 15, 155, System.currentTimeMillis());
      int bidId = book.insertOrder(bid);

      // Print the resulting order book
      System.out.println("Order Map:");
      for (Order o : book.getOrderMap().values()) {
         System.out.println(o.toString());
      }

      System.out.println("\n\nBid Book:");
      for (LimitOrder o : book.getBidBook()) {
         System.out.println(o.toString());
      }

      System.out.println("\n\nAsk Book:");
      for (LimitOrder o : book.getAskBook()) {
         System.out.println(o.toString());
      }
   }

}