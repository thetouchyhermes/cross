package it.unipi.cross.data;

public class Trade {
   public static String toString(Order order, int size, int price) {
      OrderType orderType = order.getOrderType();
      if (orderType == OrderType.market) {
         MarketOrder market = (MarketOrder) order;
         if (market.isFromStopOrder()) {
            orderType = OrderType.stop;
         }
      }

      return "{\"orderId\": " + order.getOrderId() +
            ", \"type\": \"" + order.getType() +
            "\", \"orderType\": \"" + orderType +
            "\", \"size\": " + size +
            ", \"price\": " + price +
            ", \"timestamp\": " + System.currentTimeMillis() +
            "}";
   }
}
