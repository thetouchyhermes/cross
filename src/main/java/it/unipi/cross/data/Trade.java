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

   public Trade() {
      this(null, 0, 0);
   }

   public Trade(Order order, int size, int price) {
      if (order != null) {
         this.orderId = order.getOrderId();
         this.type = order.getType();
         this.orderType = order.getOrderType();
         if (orderType == OrderType.market) {
            MarketOrder market = (MarketOrder) order;
            if (market.isFromStopOrder()) {
               this.orderType = OrderType.stop;
            }
         }
      }
      
      this.size = size;
      this.price = price;
      this.timestamp = Instant.now().getEpochSecond();  
   }

   public int getOrderId() {
      return orderId;
   }

   public void setOrderId(int orderId) {
      this.orderId = orderId;
   }

   public Type getType() {
      return type;
   }

   public void setType(Type type) {
      this.type = type;
   }

   public OrderType getOrderType() {
      return orderType;
   }

   public void setOrderType(OrderType orderType) {
      this.orderType = orderType;
   }

   public int getSize() {
      return size;
   }

   public void setSize(int size) {
      this.size = size;
   }

   public int getPrice() {
      return price;
   }

   public void setPrice(int price) {
      this.price = price;
   }

   public long getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
   }

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            //.registerTypeAdapter(Trade.class, new TradeTypeAdapter())
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }

}
