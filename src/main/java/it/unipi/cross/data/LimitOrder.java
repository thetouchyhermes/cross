package it.unipi.cross.data;

/**
 * Represents a limit order in the trading system.
 * <p>
 * A limit order is an order to buy or sell a security at a specified price or
 * better.
 * </p>
 * <p>
 * This class extends the {@link Order} class and sets the order type to
 * {@code OrderType.limit}.
 * </p>
 * <p>
 * <b>Constructors and Testing:</b>
 * <ul>
 *   <li>
 *     <code>LimitOrder(int orderId, String username, Type type, int size, int price, long timestamp)</code>:
 *     Use this constructor when the order ID is already known (e.g., when loading from a database or for integration testing).
 *     All fields are provided, allowing for precise control of the order's state.
 *   </li>
 *   <li>
 *     <code>LimitOrder(String username, Type type, int size, int price, long timestamp)</code>:
 *     Use this constructor when creating a new limit order before an order ID is assigned (e.g., in unit tests or for new order creation).
 *     The order ID defaults to -1 until set.
 *   </li>
 * </ul>
 * Both constructors initialize the order with the specified parameters, ensuring consistent and predictable state for testing and order processing.
 * </p>
 *
 * @see Order
 */
public class LimitOrder extends Order {
   
   public LimitOrder(int orderId, String username, Type type, int size, int price, long timestamp) {
      super(orderId, username, type, OrderType.limit, size, price, timestamp);
   }

   public LimitOrder(String username, Type type, int size, int price, long timestamp) {
      super(username, type, OrderType.limit, size, price, timestamp);
   }
}
