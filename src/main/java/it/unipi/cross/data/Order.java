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

   protected int orderId;
   protected Type type;
   protected OrderType orderType;
   protected int size;  // millesimi di BTC
   protected int price; // millesimi di USD
   protected long timestamp;

   public Order(int orderId, Type type, OrderType orderType, int size, int price, long timestamp) {
      this.orderId = orderId;
      this.type = type;
      this.orderType = orderType;
      this.size = size;
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
