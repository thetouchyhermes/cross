package it.unipi.cross.data;

/**
 * Represents a market order in the trading system, an order of OrderType.market
 **/
public class MarketOrder extends Order {

   private transient boolean fromStopOrder = false;

   public MarketOrder(int orderId, String username, Type type, int size, long timestamp) {
      // Market orders do not have a price
      super(orderId, username, type, OrderType.market, size, 0, timestamp);
   }

   public MarketOrder(String username, Type type, int size, long timestamp) {
      // Market orders do not have a price
      super(username, type, OrderType.market, size, 0, timestamp);
   }

   // checks if a market order was a stop order before conversion
   public boolean isFromStopOrder() {
      return fromStopOrder;
   }

   // sets a market order as a converted stop order
   public void setFromStopOrder() {
      fromStopOrder = true;
   }
}
