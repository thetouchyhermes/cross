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
 *
 * @see Order
 */
public class LimitOrder extends Order {
   
   public LimitOrder(int orderId, String username, Type type, int size, int price, long timestamp) {
      super(orderId, username, type, OrderType.limit, size, price, timestamp);
   }
}
