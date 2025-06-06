package it.unipi.cross.data;

import java.time.Instant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Trade {
   private int orderId;
   private Type type;
   private OrderType orderType;
   private int size;
   private int price;
   private long timestamp;

   public Trade(Order order, int size, int price) {
      this.orderId = order.getOrderId();
      this.type = order.getType();

      this.orderType = order.getOrderType();
      if (orderType == OrderType.market) {
         MarketOrder market = (MarketOrder) order;
         if (market.isFromStopOrder()) {
            this.orderType = OrderType.stop;
         }
      }

      this.size = size;
      this.price = price;
      this.timestamp = Instant.now().getEpochSecond();
   }

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
}
