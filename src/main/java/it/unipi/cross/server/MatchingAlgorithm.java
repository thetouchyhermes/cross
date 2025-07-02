package it.unipi.cross.server;

import java.util.Iterator;
import java.util.NavigableSet;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;

/**
 * Static algorithm that attempts at matching a given order against limit orders
 * from
 * the opposite side of the order book (from other users)
 **/
public class MatchingAlgorithm {

   public static boolean matchMarketOrder(OrderBook orderBook, MarketOrder market) {

      Type type = market.getType();

      NavigableSet<LimitOrder> oppositeBook = (type == Type.bid) ? orderBook.getAskBook() : orderBook.getBidBook();

      // simulate matching to see if the market order will fail
      Iterator<LimitOrder> simIter = oppositeBook.iterator();
      int marketSize = market.getSize();

      while (marketSize > 0 && simIter.hasNext()) {
         LimitOrder simBookOrder = simIter.next();

         if (market.getUsername().equals(simBookOrder.getUsername()))
            continue;

         int simTradeSize = Math.min(marketSize, simBookOrder.getSize());
         marketSize -= simTradeSize;
      }

      if (marketSize == 0) {

         // create the real trades because market order didn't fail
         Iterator<LimitOrder> rmIter = oppositeBook.iterator();
         boolean changedBest = false;

         while (market.getSize() > 0 && rmIter.hasNext()) {
            LimitOrder bookOrder = rmIter.next();

            if (market.getUsername().equals(bookOrder.getUsername()))
               continue;

            int tradeSize = Math.min(market.getSize(), bookOrder.getSize());
            int tradePrice = bookOrder.getPrice();

            // signal order completion to the order book for insertion of trade and
            // notification
            orderBook.insertTrade(market, bookOrder, tradeSize, tradePrice);

            market.setSize(market.getSize() - tradeSize);
            bookOrder.setSize(bookOrder.getSize() - tradeSize);

            if (bookOrder.getSize() == 0) {
               orderBook.getOrderMap().remove(bookOrder.getOrderId());
               rmIter.remove();
               changedBest = true;
            }
         }

         if (changedBest) {
            // check new prices after book update
            orderBook.checkBestPrices();
         }

         return true;
      } else {
         return false;
      }
   }

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

         // signal order completion to the order book for insertion of trade and
         // notification
         orderBook.insertTrade(limit, bookOrder, tradeSize, tradePrice);

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

      return execute;

   }
}
