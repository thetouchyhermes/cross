package it.unipi.cross.orderbook;

import java.util.Iterator;
import java.util.NavigableSet;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;

public class MatchingAlgorithm {

   public static boolean matchMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {

      Type type = marketOrder.getType();

      NavigableSet<LimitOrder> oppositeBook = (type == Type.bid) ? orderBook.getAskBook() : orderBook.getBidBook();

      Iterator<LimitOrder> it = oppositeBook.iterator();

      while (marketOrder.getSize() > 0 && it.hasNext()) {
         LimitOrder bookOrder = it.next();

         if (marketOrder.getUsername() == bookOrder.getUsername())
            continue;

         int tradeSize = Math.min(marketOrder.getSize(), bookOrder.getSize());

         int tradePrice = bookOrder.getPrice();

         // notify trade for both orders on the tradeSize and tradePrice
         // ...

         bookOrder.setSize(bookOrder.getSize() - tradeSize);
         marketOrder.setSize(marketOrder.getSize() - tradeSize);

         if (bookOrder.getSize() == 0) {
            it.remove();
            orderBook.getOrderMap().remove(bookOrder.getOrderId());
         }
      }

      orderBook.getOrderMap().remove(marketOrder.getOrderId());
      if (marketOrder.getSize() > 0)
         return false;
      else
         return true;
   }

   public static boolean matchLimitOrder(OrderBook orderBook, LimitOrder limitOrder) {

      Type type = limitOrder.getType();

      NavigableSet<LimitOrder> oppositeBook = (type == Type.bid) ? orderBook.getAskBook() : orderBook.getBidBook();
      Iterator<LimitOrder> it = oppositeBook.iterator();

      while (limitOrder.getSize() > 0 && it.hasNext()) {
         LimitOrder bookOrder = it.next();

         if (limitOrder.getUsername() == bookOrder.getUsername())
            continue;

         if (type == Type.bid && limitOrder.getPrice() < bookOrder.getPrice()
               || type == Type.ask && limitOrder.getPrice() > bookOrder.getPrice())
            break;

         int tradeSize = Math.min(limitOrder.getSize(), bookOrder.getSize());

         int tradePrice = bookOrder.getPrice();

         // notify trade for both orders on the tradeSize and tradePrice
         // ...

         bookOrder.setSize(bookOrder.getSize() - tradeSize);
         limitOrder.setSize(limitOrder.getSize() - tradeSize);

         if (bookOrder.getSize() == 0) {
            it.remove();
            orderBook.getOrderMap().remove(bookOrder.getOrderId());
         }
         if (limitOrder.getSize() == 0) {
            orderBook.getOrderMap().remove(limitOrder.getOrderId());
            if (type == Type.bid)
               orderBook.getAskBook().remove(limitOrder);
            else if (type == Type.ask)
               orderBook.getBidBook().remove(limitOrder);
         }
      }

      return true;

      // matching after insertion...

   }

   // CHECK COPILOT FOR CHANGES ABOUT BEST PRICE TRIGGERING
   public static boolean matchStopOrder(OrderBook orderBook, StopOrder stopOrder) {

      // check for immediate trigger after insertion...

      Type type = stopOrder.getType();
      int stopPrice = stopOrder.getPrice();

      boolean execute = false;
      int bestBookPrice = -1;
      if (type == Type.bid) {
         // prezzo massimo, compratore
         bestBookPrice = orderBook.getBestAskPrice();
         if (bestBookPrice != -1 && bestBookPrice >= stopPrice)
            execute = true;
      } else if (type == Type.ask) {
         // prezzo minimo, venditore
         bestBookPrice = orderBook.getBestBidPrice();
         if (bestBookPrice != -1 && bestBookPrice <= stopPrice)
            execute = true;
      }

      if (execute) {
         // convert to market order
         MarketOrder marketOrder = StopOrder.convertToMarket(stopOrder);
         orderBook.deleteOrder(stopOrder.getOrderId());
         orderBook.insertOrder(marketOrder);
      }
   }
}
