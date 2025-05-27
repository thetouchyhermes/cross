package it.unipi.cross.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents an abstract order in a trading system.
 * <p>
 * This class encapsulates the common properties of an order, such as its ID, type,
 * order type, size, price, and timestamp. It is intended to be extended by specific
 * order implementations.
 * </p>
 */
public abstract class Order {

   protected final int orderId;
   protected final Type type;
   protected final OrderType orderType;
   protected final int size;  // millesimi di BTC
   protected transient Integer tempSize;
   protected final int price; // millesimi di USD
   protected final long timestamp;

   public Order(int orderId, Type type, OrderType orderType, int size, int price, long timestamp) {
      this.orderId = orderId;
      this.type = type;
      this.orderType = orderType;
      this.size = this.tempSize = size;
      this.price = price;
      this.timestamp = timestamp;
   }

   public int getOrderId() {
      return orderId;
   }

   public Type getType() {
      return type;
   }

   public OrderType getOrderType() {
      return orderType;
   }

   public int getSize() {
      if (tempSize == null)
         tempSize = size;

      return tempSize;
   }

   public void setSize(int size) {
      if (tempSize == null)
         tempSize = this.size;

      if (size <= this.tempSize)
         this.tempSize = size;
   }

   public int getOriginalSize() {
      return size;
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
