package it.unipi.cross.data;

/**
 * Represents a market order in the trading system.
 * <p>
 * A market order is an order to buy or sell a security immediately at the best
 * available current price. Market orders do not specify a price.
 * </p>
 * <p>
 * This class extends the {@link Order} class and sets the order type to
 * {@code OrderType.market}.
 * </p>
 * <p>
 * <b>Constructors and Testing:</b>
 * <ul>
 *   <li>
 *     <code>MarketOrder(int orderId, String username, Type type, int size, long timestamp)</code>:
 *     Use this constructor when the order ID is already known (e.g., when loading from a database or for integration testing).
 *     All fields are provided, allowing for precise control of the order's state.
 *   </li>
 *   <li>
 *     <code>MarketOrder(String username, Type type, int size, long timestamp)</code>:
 *     Use this constructor when creating a new market order before an order ID is assigned (e.g., in unit tests or for new order creation).
 *     The order ID defaults to -1 until set.
 *   </li>
 * </ul>
 * Both constructors initialize the order with the specified parameters, ensuring consistent and predictable state for testing and order processing.
 * </p>
 *
 * @see Order
 */
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

   public boolean isFromStopOrder() {
      return fromStopOrder;
   }

   public void setFromStopOrder() {
      fromStopOrder = true;
   }
}
