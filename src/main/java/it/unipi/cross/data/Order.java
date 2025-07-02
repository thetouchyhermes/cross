package it.unipi.cross.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents an abstract order in a trading system.
 */
public class Order {

   protected int orderId;
   protected final String username;
   protected final Type type;
   protected final OrderType orderType;
   protected int size; // mills BTC
   protected final int price; // mills USD
   protected final long timestamp;
   protected transient final Integer originalSize;

   public Order(int orderId, String username, Type type, OrderType orderType, int size, int price, long timestamp) {
      this.orderId = orderId;
      this.username = username;
      this.type = type;
      this.orderType = orderType;
      this.size = this.originalSize = size;
      this.price = price;
      this.timestamp = timestamp;
   }

   public Order(String username, Type type, OrderType orderType, int size, int price, long timestamp) {
      this(-1, username, type, orderType, size, price, timestamp);
   }

   public int getOrderId() {
      return orderId;
   }

   public void setOrderId(int orderId) {
      this.orderId = orderId;
   }

   public String getUsername() {
      return username;
   }

   public Type getType() {
      return type;
   }

   public OrderType getOrderType() {
      return orderType;
   }

   public int getSize() {
      return size;
   }

   public void setSize(int size) {
      if (size <= this.size)
         this.size = size;
   }

   public int getOriginalSize() {
      return originalSize;
   }

   public int getPrice() {
      return price;
   }

   public long getTimestamp() {
      return timestamp;
   }

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }

}
