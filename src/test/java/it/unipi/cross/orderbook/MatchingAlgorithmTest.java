package it.unipi.cross.orderbook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.Type;

public class MatchingAlgorithmTest {

   private static OrderBook orderBook;

   @BeforeEach
   public void setUp() {
      orderBook = new OrderBook();
   }

   @Test
   public void testMatchMarketOrder_FullyMatched() {
      // Market buy order, ask book has enough size
      LimitOrder ask1 = new LimitOrder(12, "userA", Type.ask, 5, 100, System.currentTimeMillis());
      LimitOrder ask2 = new LimitOrder(13, "userB", Type.ask, 10, 101, System.currentTimeMillis());
      orderBook.getAskBook().add(ask1);
      orderBook.getAskBook().add(ask2);
      orderBook.getOrderMap().put(ask1.getOrderId(), ask1);
      orderBook.getOrderMap().put(ask2.getOrderId(), ask2);

      MarketOrder market = new MarketOrder(14, "userC", Type.bid, 8, System.currentTimeMillis());
      orderBook.getOrderMap().put(market.getOrderId(), market);

      boolean result = MatchingAlgorithm.matchMarketOrder(orderBook, market);

      assertTrue(result);
      assertEquals(0, market.getSize());
      assertEquals(0, ask1.getSize());
      assertEquals(7, ask2.getSize());
      assertFalse(orderBook.getOrderMap().containsKey(12));
      assertTrue(orderBook.getOrderMap().containsKey(13));
      assertFalse(orderBook.getOrderMap().containsKey(14));
   }

   @Test
   public void testMatchMarketOrder_PartiallyMatched() {
      // Market buy order, ask book does not have enough size
      LimitOrder ask1 = new LimitOrder(16, "userA", Type.ask, 0, 100, System.currentTimeMillis());
      orderBook.getAskBook().add(ask1);
      orderBook.getOrderMap().put(ask1.getOrderId(), ask1);

      MarketOrder market = new MarketOrder(15, "userA", Type.bid, 2, System.currentTimeMillis());
      orderBook.getOrderMap().put(market.getOrderId(), market);

      boolean result = MatchingAlgorithm.matchMarketOrder(orderBook, market);

      assertFalse(result);
      assertEquals(2, market.getSize());
      assertEquals(0, ask1.getSize());
      assertFalse(orderBook.getOrderMap().containsKey(16));
      assertFalse(orderBook.getOrderMap().containsKey(15));
   }

   @Test
   public void testMatchMarketOrder_SkipOwnOrders() {
      // Market buy order, ask book has an order from same user
      LimitOrder ask1 = new LimitOrder(17, "userC", Type.ask, 5, 100, System.currentTimeMillis());
      LimitOrder ask2 = new LimitOrder(18, "userA", Type.ask, 5, 101, System.currentTimeMillis());
      orderBook.getAskBook().add(ask1);
      orderBook.getAskBook().add(ask2);
      orderBook.getOrderMap().put(ask1.getOrderId(), ask1);
      orderBook.getOrderMap().put(ask2.getOrderId(), ask2);

      MarketOrder market = new MarketOrder(19, "userC", Type.bid, 5, System.currentTimeMillis());
      orderBook.getOrderMap().put(market.getOrderId(), market);

      boolean result = MatchingAlgorithm.matchMarketOrder(orderBook, market);

      assertFalse(result);
      assertEquals(5, market.getSize());
      assertEquals(5, ask1.getSize());
      assertEquals(5, ask2.getSize());
      assertTrue(orderBook.getOrderMap().containsKey(17));
      assertTrue(orderBook.getOrderMap().containsKey(18));
      assertFalse(orderBook.getOrderMap().containsKey(19));
   }

   @Test
   public void testMatchMarketOrder_EmptyOppositeBook() {
      // Market buy order, ask book is empty
      MarketOrder market = new MarketOrder(20, "userC", Type.bid, 5, System.currentTimeMillis());
      orderBook.getOrderMap().put(market.getOrderId(), market);

      boolean result = MatchingAlgorithm.matchMarketOrder(orderBook, market);

      assertFalse(result);
      assertEquals(5, market.getSize());
      assertFalse(orderBook.getOrderMap().containsKey(20));
   }

   @Test
   public void testMatchMarketOrder_SellSide() {
      // Market sell order, bid book has enough size
      LimitOrder bid1 = new LimitOrder(21, "userA", Type.bid, 4, 100, System.currentTimeMillis());
      LimitOrder bid2 = new LimitOrder(22, "userB", Type.bid, 4, 99, System.currentTimeMillis());
      orderBook.getBidBook().add(bid1);
      orderBook.getBidBook().add(bid2);
      orderBook.getOrderMap().put(bid1.getOrderId(), bid1);
      orderBook.getOrderMap().put(bid2.getOrderId(), bid2);

      MarketOrder market = new MarketOrder(23, "userC", Type.ask, 23, System.currentTimeMillis());
      orderBook.getOrderMap().put(market.getOrderId(), market);

      boolean result = MatchingAlgorithm.matchMarketOrder(orderBook, market);

      assertTrue(result);
      assertEquals(0, market.getSize());
      assertEquals(0, bid1.getSize());
      assertEquals(1, bid2.getSize());
      assertFalse(orderBook.getOrderMap().containsKey(21));
      assertTrue(orderBook.getOrderMap().containsKey(22));
      assertFalse(orderBook.getOrderMap().containsKey(23));
   }
}