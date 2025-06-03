package it.unipi.cross.orderbook;

import java.util.Iterator;
import java.util.NavigableSet;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Trade;
import it.unipi.cross.data.Type;

public class MatchingAlgorithm {

   /**
    * Attempts to match a given market order against the opposite side of the order
    * book.
    * The method iterates through the opposite book's limit orders, executing
    * trades until
    * the market order is fully matched or no more suitable limit orders are
    * available.
    *
    * <p>
    * For each match:
    * <ul>
    * <li>Skips orders from the same user as the market order.</li>
    * <li>Executes trades for the minimum available size between the market order
    * and the limit order.</li>
    * <li>Notifies both parties of the trade (notification logic to be
    * implemented).</li>
    * <li>Updates the sizes of both orders and removes fully filled limit orders
    * from the book and order map.</li>
    * </ul>
    * After processing, the market order is removed from the order map.
    *
    * @param orderBook the order book containing current limit orders
    * @param market    the market order to be matched
    * @return {@code true} if the market order was fully matched; {@code false}
    *         otherwise
    */
   public static boolean matchMarketOrder(OrderBook orderBook, MarketOrder market) {

      Type type = market.getType();

      NavigableSet<LimitOrder> oppositeBook = (type == Type.bid) ? orderBook.getAskBook() : orderBook.getBidBook();

      Iterator<LimitOrder> it = oppositeBook.iterator();

      while (market.getSize() > 0 && it.hasNext()) {
         LimitOrder bookOrder = it.next();

         if (market.getUsername().equals(bookOrder.getUsername()))
            continue;

         int tradeSize = Math.min(market.getSize(), bookOrder.getSize());

         int tradePrice = bookOrder.getPrice();

         // notify trade for both orders on the tradeSize and tradePrice
         System.out.println(Trade.toString(market, tradeSize, tradePrice));
         System.out.println(Trade.toString(bookOrder, tradeSize, tradePrice));

         bookOrder.setSize(bookOrder.getSize() - tradeSize);
         market.setSize(market.getSize() - tradeSize);

         if (bookOrder.getSize() == 0) {
            orderBook.getOrderMap().remove(bookOrder.getOrderId());
            it.remove();

            // check new prices after book update
            orderBook.checkBestPrices();
         }
      }

      orderBook.getOrderMap().remove(market.getOrderId());
      if (market.getSize() > 0)
         return false;
      else
         return true;
   }

   /**
    * Attempts to match a given limit order against the opposite side of the order
    * book.
    * <p>
    * For a bid order, matches against the ask book; for an ask order, matches
    * against the bid book.
    * The method iterates through the opposite book and executes trades when price
    * and size conditions are met.
    * Orders from the same user are skipped. Trades are executed at the price of
    * the resting order in the book.
    * Both the incoming limit order and matched book orders are updated or removed
    * as their sizes are reduced to zero.
    * 
    * @param orderBook the order book containing bid and ask books and order
    *                  mappings
    * @param limit     the incoming limit order to be matched
    * @return {@code true} if the limit order was fully matched and completed,
    *         {@code false} otherwise
    */
   public static boolean matchLimitOrder(OrderBook orderBook, LimitOrder limit) {

      Type type = limit.getType();

      boolean completed = false;

      NavigableSet<LimitOrder> oppositeBook = (type == Type.bid) ? orderBook.getAskBook() : orderBook.getBidBook();
      Iterator<LimitOrder> it = oppositeBook.iterator();

      while (limit.getSize() > 0 && it.hasNext()) {
         LimitOrder bookOrder = it.next();

         if (limit.getUsername().equals(bookOrder.getUsername()))
            continue;

         if (type == Type.bid && limit.getPrice() < bookOrder.getPrice()
               || type == Type.ask && limit.getPrice() > bookOrder.getPrice())
            break;

         int tradeSize = Math.min(limit.getSize(), bookOrder.getSize());

         int tradePrice = bookOrder.getPrice();

         // notify trade for both orders on the tradeSize and tradePrice
         System.out.println(Trade.toString(limit, tradeSize, tradePrice));
         System.out.println(Trade.toString(bookOrder, tradeSize, tradePrice));

         bookOrder.setSize(bookOrder.getSize() - tradeSize);
         limit.setSize(limit.getSize() - tradeSize);

         if (bookOrder.getSize() == 0) {
            orderBook.getOrderMap().remove(bookOrder.getOrderId());
            it.remove();
         }
         if (limit.getSize() == 0) {
            orderBook.getOrderMap().remove(limit.getOrderId());
            completed = true;
         }

      }

      return completed;
   }

   /**
    * Attempts to match and execute a stop order against the current order book.
    * <p>
    * If the stop order's trigger price is reached or surpassed by the best available price
    * in the order book (best ask for buy stops, best bid for sell stops), the stop order is
    * converted into a market order and inserted into the order book for execution.
    * </p>
    *
    * @param orderBook the order book containing current orders and prices
    * @param stop the stop order to be matched and potentially executed
    * @return {@code true} if the stop order was converted to market order,
    *         {@code false} otherwise
    */
   public static boolean matchStopOrder(OrderBook orderBook, StopOrder stop) {

      Type type = stop.getType();
      int stopPrice = stop.getPrice();

      boolean execute = false;
      int bestBookPrice = -1;
      if (type == Type.bid) {
         // highest price a buyer can afford
         bestBookPrice = orderBook.getBestAskPrice();
         if (bestBookPrice != -1 && bestBookPrice >= stopPrice)
            execute = true;
      } else if (type == Type.ask) {
         // lowest price a seller can afford
         bestBookPrice = orderBook.getBestBidPrice();
         if (bestBookPrice != -1 && bestBookPrice <= stopPrice)
            execute = true;
      }

      if (execute) {
         // convert to market order
         MarketOrder market = StopOrder.convertToMarket(stop);
         orderBook.getOrderMap().remove(stop.getOrderId());
         int result = orderBook.insertOrder(market);
         if (result == -1) {
            //what to do if its market order fails
         }
      }

      return execute;

   }
}
