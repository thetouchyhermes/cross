package it.unipi.cross.data;

/**
 * Represents a market order in the trading system.
 * <p>
 * A market order is an order to buy or sell a security immediately at the best
 * available current price. Market orders do not specify a price.
 * </p>
 * This class extends the {@link Order} class and sets the order type to
 * {@code OrderType.market}.
 * </p>
 *
 * @see Order
 */
public class MarketOrder extends Order {

   public MarketOrder(int orderId, Type type, int size, long timestamp) {
      // Market orders do not have a price
      super(orderId, type, OrderType.market, size, 0, timestamp);
   }
}
