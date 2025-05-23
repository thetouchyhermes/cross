package it.unipi.cross.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// File: src/test/java/it/unipi/cross/data/DataPackageTest.java
class DataPackageTest {

   // ----- LimitOrder -----
   @Test
   void testLimitOrderConstructorAndGetters() {
      LimitOrder order = new LimitOrder(Type.bid, 1000, 50000, 123456789L);
      assertEquals(Type.bid, order.getType());
      assertEquals(OrderType.limit, order.getOrderType());
      assertEquals(1000, order.getSize());
      assertEquals(50000, order.getPrice());
      assertEquals(123456789L, order.getTimestamp());
   }

   @Test
   void testLimitOrderToStringIsJson() {
      LimitOrder order = new LimitOrder(Type.ask, 2000, 60000, 987654321L);
      String json = order.toString();
      assertTrue(json.contains("\"type\""));
      assertTrue(json.contains("\"orderType\""));
      assertTrue(json.contains("\"size\""));
      assertTrue(json.contains("\"price\""));
      assertTrue(json.contains("\"timestamp\""));
   }

   // ----- MarketOrder -----
   @Test
   void testMarketOrderConstructorAndGetters() {
      MarketOrder order = new MarketOrder(Type.ask, 1500, 111111111L);
      assertEquals(Type.ask, order.getType());
      assertEquals(OrderType.market, order.getOrderType());
      assertEquals(1500, order.getSize());
      assertEquals(0, order.getPrice());
      assertEquals(111111111L, order.getTimestamp());
   }

   @Test
   void testMarketOrderToStringIsJson() {
      MarketOrder order = new MarketOrder(Type.bid, 500, 222222222L);
      String json = order.toString();
      assertTrue(json.contains("\"orderType\""));
      assertTrue(json.contains("\"market\"") || json.contains("\"orderType\":\"market\""));
   }

   // ----- StopOrder -----
   @Test
   void testStopOrderConstructorAndGetters() {
      StopOrder order = new StopOrder(Type.bid, 3000, 45000, 333333333L);
      assertEquals(Type.bid, order.getType());
      assertEquals(OrderType.stop, order.getOrderType());
      assertEquals(3000, order.getSize());
      assertEquals(45000, order.getPrice());
      assertEquals(333333333L, order.getTimestamp());
   }

   @Test
   void testStopOrderToStringIsJson() {
      StopOrder order = new StopOrder(Type.ask, 4000, 47000, 444444444L);
      String json = order.toString();
      assertTrue(json.contains("\"orderType\""));
      assertTrue(json.contains("\"stop\"") || json.contains("\"orderType\":\"stop\""));
   }

   // ----- User -----
   @Test
   void testUserConstructorAndGettersSetters() {
      User user = new User("alice", "password123");
      assertEquals("alice", user.getUsername());
      assertEquals("password123", user.getPassword());
      assertFalse(user.isLogged());
      user.setLogged(true);
      assertTrue(user.isLogged());
   }

   @Test
   void testUserToStringIsJson() {
      User user = new User("bob", "secret");
      String json = user.toString();
      assertTrue(json.contains("\"username\""));
      assertTrue(json.contains("\"password\""));
      assertTrue(json.contains("\"logged\""));
   }

   // ----- OrderType enum -----
   @Test
   void testOrderTypeEnumValues() {
      assertEquals(OrderType.market, OrderType.valueOf("market"));
      assertEquals(OrderType.limit, OrderType.valueOf("limit"));
      assertEquals(OrderType.stop, OrderType.valueOf("stop"));
      assertArrayEquals(new OrderType[] { OrderType.market, OrderType.limit, OrderType.stop }, OrderType.values());
   }

   // ----- Type enum -----
   @Test
   void testTypeEnumValues() {
      assertEquals(Type.ask, Type.valueOf("ask"));
      assertEquals(Type.bid, Type.valueOf("bid"));
      assertArrayEquals(new Type[] { Type.ask, Type.bid }, Type.values());
   }
}