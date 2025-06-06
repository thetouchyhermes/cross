package it.unipi.cross.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents an abstract order in a trading system.
 * <p>
 * This class encapsulates the common properties of an order, such as its ID, username, type,
 * order type, size, price, and timestamp. It is intended to be extended by specific
 * order implementations.
 * </p>
 * <p>
 * <b>Constructors:</b>
 * <ul>
 *   <li>
 *     <code>Order(int orderId, String username, Type type, OrderType orderType, int size, int price, long timestamp)</code>:
 *     Used when the order ID is already known (e.g., when loading from a database). All fields must be provided.
 *   </li>
 *   <li>
 *     <code>Order(String username, Type type, OrderType orderType, int size, int price, long timestamp)</code>:
 *     Used when creating a new order before an order ID is assigned (e.g., for testing or new order creation).
 *     The order ID will remain at its default value (-1) until set.
 *   </li>
 * </ul>
 * Both constructors initialize the <code>size</code> and <code>tempSize</code> fields to the provided size value,
 * ensuring consistent state for testing and order processing.
 * </p>
 */
public abstract class Order {

   protected int orderId;
   protected final String username;
   protected final Type type;
   protected final OrderType orderType;
   protected int size;  // millesimi di BTC
   protected final int price; // millesimi di USD
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
