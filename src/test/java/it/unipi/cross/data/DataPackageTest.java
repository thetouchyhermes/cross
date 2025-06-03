package it.unipi.cross.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataPackageTest {

   // Minimal concrete subclass for testing
   static class TestOrder extends Order {
      public TestOrder(int orderId, String username, Type type, OrderType orderType, int size, int price, long timestamp) {
         super(orderId, username, type, orderType, size, price, timestamp);
      }
      public TestOrder(String username, Type type, OrderType orderType, int size, int price, long timestamp) {
         super(username, type, orderType, size, price, timestamp);
      }
   }

   private TestOrder order;
   private final int orderId = 42;
   private final String username = "alice";
   private final Type type = Type.bid;
   private final OrderType orderType = OrderType.limit;
   private final int size = 1000;
   private final int price = 50000;
   private final long timestamp = 123456789L;

   @BeforeEach
   void setUp() {
      order = new TestOrder(orderId, username, type, orderType, size, price, timestamp);
   }

   @Test
   void testConstructorWithOrderId() {
      assertEquals(orderId, order.getOrderId());
      assertEquals(username, order.getUsername());
      assertEquals(type, order.getType());
      assertEquals(orderType, order.getOrderType());
      assertEquals(size, order.getSize());
      assertEquals(size, order.getOriginalSize());
      assertEquals(price, order.getPrice());
      assertEquals(timestamp, order.getTimestamp());
   }

   @Test
   void testConstructorWithoutOrderId() {
      TestOrder o = new TestOrder(username, type, orderType, size, price, timestamp);
      assertEquals(-1, o.getOrderId());
      assertEquals(username, o.getUsername());
      assertEquals(type, o.getType());
      assertEquals(orderType, o.getOrderType());
      assertEquals(size, o.getSize());
      assertEquals(size, o.getOriginalSize());
      assertEquals(price, o.getPrice());
      assertEquals(timestamp, o.getTimestamp());
   }

   @Test
   void testSetAndGetOrderId() {
      order.setOrderId(99);
      assertEquals(99, order.getOrderId());
   }

   @Test
   void testGetSizeAndSetSize() {
      assertEquals(size, order.getSize());
      order.setSize(800);
      assertEquals(800, order.getSize());
      // setSize with larger value should not increase tempSize
      order.setSize(900);
      assertEquals(800, order.getSize());
      // setSize with same value
      order.setSize(800);
      assertEquals(800, order.getSize());
   }

   @Test
   void testSetSizeWhenTempSizeNull() {
      TestOrder o = new TestOrder(orderId, username, type, orderType, size, price, timestamp);
      o.tempSize = null;
      o.setSize(500);
      assertEquals(500, o.getSize());
   }

   @Test
   void testGetOriginalSize() {
      order.setSize(500);
      assertEquals(size, order.getOriginalSize());
   }

   @Test
   void testToStringProducesJson() {
      String json = order.toString();
      assertTrue(json.contains("\"orderId\""));
      assertTrue(json.contains("\"username\""));
      assertTrue(json.contains("\"type\""));
      assertTrue(json.contains("\"orderType\""));
      assertTrue(json.contains("\"size\""));
      assertTrue(json.contains("\"price\""));
      assertTrue(json.contains("\"timestamp\""));
   }
}