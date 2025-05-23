package it.unipi.cross.data;

/**
 * Represents the type of an order in a trading system.
 * <ul>
 *   <li>{@code market} - An order to buy or sell immediately at the best available current price.</li>
 *   <li>{@code limit} - An order to buy or sell at a specific price or better.</li>
 *   <li>{@code stop} - An order to buy or sell once the price reaches a specified stop price.</li>
 * </ul>
 */
public enum OrderType {
   market,
   limit,
   stop
}
