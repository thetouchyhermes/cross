package it.unipi.cross.data;

/**
 * Represents a stop order in the trading system.
 * <p>
 * A stop order is an order to buy or sell a security once the price of the
 * security reaches a specified price.
 * </p>
 * <p>
 * This class extends the {@link Order} class and sets the order type to
 * {@code OrderType.stop}.
 * </p>
 * <p>
 * <b>Constructors and Testing:</b>
 * <ul>
 *   <li>
 *     <code>StopOrder(int orderId, String username, Type type, int size, int price, long timestamp)</code>:
 *     Use this constructor when the order ID is already known (e.g., when loading from a database or for integration testing).
 *     All fields are provided, allowing for precise control of the order's state.
 *   </li>
 *   <li>
 *     <code>StopOrder(String username, Type type, int size, int price, long timestamp)</code>:
 *     Use this constructor when creating a new stop order before an order ID is assigned (e.g., in unit tests or for new order creation).
 *     The order ID defaults to -1 until set.
 *   </li>
 * </ul>
 * Both constructors initialize the order with the specified parameters, ensuring consistent and predictable state for testing and order processing.
 * </p>
 *
 * @see Order
 */
public class StopOrder extends Order {
   
   public StopOrder(int orderId, String username, Type type, int size, int price, long timestamp) {
      super(orderId, username, type, OrderType.stop, size, price, timestamp);
   }
   
   public StopOrder(String username, Type type, int size, int price, long timestamp) {
      super(username, type, OrderType.stop, size, price, timestamp);
   }

   public static MarketOrder convertToMarket(StopOrder stop) {
      MarketOrder market = new MarketOrder(
         stop.getOrderId(),
         stop.getUsername(),
         stop.getType(),
         stop.getOriginalSize(),
         stop.getTimestamp()
      );
      // update tempSize
      market.setSize(stop.getSize());
      market.setFromStopOrder();

      return market;
   }
}
