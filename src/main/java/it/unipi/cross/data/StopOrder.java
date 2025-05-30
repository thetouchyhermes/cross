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
 *
 * @see Order
 */
public class StopOrder extends Order {
   
   public StopOrder(int orderId, String username, Type type, int size, int price, long timestamp) {
      super(orderId, username, type, OrderType.stop, size, price, timestamp);
   }
}
