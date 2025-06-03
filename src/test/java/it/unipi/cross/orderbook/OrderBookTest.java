package it.unipi.cross.orderbook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;

class OrderBookTest {

   private OrderBook orderBook;

   @BeforeEach
   void setUp() {
      orderBook = new OrderBook();
   }

   @Test
   void testInsertLimitOrder() {
      LimitOrder bid = new LimitOrder(-1, "alice", Type.bid, 100, 10, System.nanoTime());
      int id = orderBook.insertOrder(bid);
      assertTrue(id > 0);
      assertEquals(bid, orderBook.getOrderMap().get(id));
      assertTrue(orderBook.getBidBook().contains(bid));
      assertEquals(10, orderBook.getBestBidPrice());
   }

   @Test
   void testInsertAskLimitOrder() {
      LimitOrder ask = new LimitOrder(-1, "bene", Type.ask, 105, 5, System.nanoTime());
      int id = orderBook.insertOrder(ask);
      assertTrue(id > 0);
      assertTrue(orderBook.getAskBook().contains(ask));
      assertEquals(5, orderBook.getBestAskPrice());
   }

   @Test
   void testDeleteLimitOrder() {
      LimitOrder bid = new LimitOrder(-1, "ciao", Type.bid, 100, 10, System.nanoTime());
      int id = orderBook.insertOrder(bid);
      assertTrue(orderBook.deleteOrder(id));
      assertFalse(orderBook.getBidBook().contains(bid));
      assertNull(orderBook.getOrderMap().get(id));
      assertEquals(-1, orderBook.getBestBidPrice());
   }

   @Test
   void testInsertMarketOrder() {
      // Insert an ask so the market order can match
      LimitOrder ask = new LimitOrder(-1, "dario", Type.ask, 105, 5, System.nanoTime());
      orderBook.insertOrder(ask);

      MarketOrder marketBid = new MarketOrder(-1, "erne", Type.bid, 5, System.nanoTime());
      int id = orderBook.insertOrder(marketBid);
      assertTrue(id > 0);
      // Market orders are not stored
      assertNull(orderBook.getOrderMap().get(id));
   }

   @Test
   void testInsertStopOrder() {
      StopOrder stop = new StopOrder(-1, "forse", Type.bid, 110, 10, System.nanoTime());
      int id = orderBook.insertOrder(stop);
      assertTrue(id > 0);
      assertEquals(stop, orderBook.getOrderMap().get(id));
      // Stop orders are not in bid/ask books
      assertFalse(orderBook.getBidBook().contains(stop));
      assertFalse(orderBook.getAskBook().contains(stop));
   }

   @Test
   void testDeleteStopOrder() {
      StopOrder stop = new StopOrder(-1, "", Type.ask, 90, 10, System.nanoTime());
      int id = orderBook.insertOrder(stop);
      assertTrue(orderBook.deleteOrder(id));
      assertNull(orderBook.getOrderMap().get(id));
   }

   @Test
   void testDeleteNonexistentOrder() {
      assertFalse(orderBook.deleteOrder(9999));
   }

   @Test
   void testBestPricesUpdate() {
      LimitOrder bid1 = new LimitOrder(-1, "giacomo", Type.bid, 100, 75, System.nanoTime());
      LimitOrder bid2 = new LimitOrder(-1, "hector", Type.bid, 105, 80, System.nanoTime());
      orderBook.insertOrder(bid1);
      orderBook.insertOrder(bid2);
      assertEquals(80, orderBook.getBestBidPrice());
      orderBook.deleteOrder(bid2.getOrderId());
      assertEquals(75, orderBook.getBestBidPrice());
   }

   @Test
   void testInsertOrderWithNegativeSize() {
      LimitOrder badOrder = new LimitOrder(-1, "bad", Type.bid, -5, 100, System.nanoTime());
      int id = orderBook.insertOrder(badOrder);
      assertEquals(-1, id);
   }

   @Test
   void testInsertOrderWithDuplicateId() {
      LimitOrder order1 = new LimitOrder(123, "dup", Type.bid, 10, 100, System.nanoTime());
      LimitOrder order2 = new LimitOrder(123, "dup2", Type.ask, 10, 105, System.nanoTime());
      int id1 = orderBook.insertOrder(order1);
      int id2 = orderBook.insertOrder(order2);
      assertTrue(id1 > 0);
      assertEquals(-1, id2);
   }

   @Test
   void testInsertLimitOrderNotMatchedGoesToBook() {
      LimitOrder ask = new LimitOrder(-1, "asker", Type.ask, 10, 110, System.nanoTime());
      int id = orderBook.insertOrder(ask);
      assertTrue(orderBook.getAskBook().contains(ask));
      assertEquals(ask, orderBook.getOrderMap().get(id));
   }

   @Test
   void testInsertLimitOrderMatchedNotInBook() {
      // Insert ask, then bid that matches
      LimitOrder ask = new LimitOrder(-1, "asker", Type.ask, 3, 100, System.nanoTime());
      orderBook.insertOrder(ask);
      LimitOrder bid = new LimitOrder(-1, "bidder", Type.bid, 5, 100, System.nanoTime());
      int bidId = orderBook.insertOrder(bid);
      int askId = orderBook.insertOrder(ask);
      // If matched, should not be in book
      assertFalse(orderBook.getAskBook().contains(ask));
      // Market order is removed from map after match, but limit order remains until deleted
      // Here, both should be in map until deleted
      assertEquals(ask, orderBook.getOrderMap().get(askId));
   }

   @Test
   void testInsertMarketOrderNoMatchReturnsMinusOne() {
      MarketOrder marketBid = new MarketOrder(-1, "noMatch", Type.bid, 5, System.nanoTime());
      int id = orderBook.insertOrder(marketBid);
      assertEquals(-1, id);
   }

   @Test
   void testInsertStopOrderIsAddedAndMatchCalled() {
      StopOrder stop = new StopOrder(-1, "stopper", Type.ask, 10, 120, System.nanoTime());
      int id = orderBook.insertOrder(stop);
      assertTrue(id > 0);
      assertEquals(stop, orderBook.getOrderMap().get(id));
   }
}