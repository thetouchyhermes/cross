package it.unipi.cross.data;

/**
 * Represents a stop order in the trading system, an order of OrderType.stop
 **/
public class StopOrder extends Order {

   public StopOrder(int orderId, String username, Type type, int size, int price, long timestamp) {
      super(orderId, username, type, OrderType.stop, size, price, timestamp);
   }

   public StopOrder(String username, Type type, int size, int price, long timestamp) {
      super(username, type, OrderType.stop, size, price, timestamp);
   }

   public static MarketOrder convertToMarket(StopOrder stop) {
      MarketOrder market = new MarketOrder(
            stop.getOrderId(),
            stop.getUsername(),
            stop.getType(),
            stop.getOriginalSize(),
            stop.getTimestamp());
      // update tempSize
      market.setSize(stop.getSize());
      market.setFromStopOrder();

      return market;
   }
}
