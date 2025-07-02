package it.unipi.cross.data;

/**
 * Represents a limit order in the trading system, an order of OrderType.limit
 **/
public class LimitOrder extends Order {

   public LimitOrder(int orderId, String username, Type type, int size, int price, long timestamp) {
      super(orderId, username, type, OrderType.limit, size, price, timestamp);
   }

   public LimitOrder(String username, Type type, int size, int price, long timestamp) {
      super(username, type, OrderType.limit, size, price, timestamp);
   }
}
