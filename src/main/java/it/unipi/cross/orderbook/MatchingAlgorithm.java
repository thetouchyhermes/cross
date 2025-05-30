package it.unipi.cross.orderbook;

import java.util.Iterator;
import java.util.NavigableSet;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;

public class MatchingAlgorithm {

   public void sortMatch(OrderBook orderBook, Order newOrder) {
      switch (newOrder.getOrderType()) {
         case market:
            matchMarketOrder(orderBook, (MarketOrder) newOrder);
         case limit:
            matchLimitOrder(orderBook, (LimitOrder) newOrder);
         case stop:
            matchStopOrder(orderBook, (StopOrder) newOrder);
      }
   }

   private void matchMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
      Type type = marketOrder.getType();
      int marketSize = marketOrder.getSize();

      NavigableSet<LimitOrder> book = (type == Type.bid) ? orderBook.getBidBook() : orderBook.getAskBook();

      Iterator<LimitOrder> iter = book.iterator();
      while(marketSize > 0 && iter.hasNext()) {
         LimitOrder bookOrder = iter.next();

         int limitSize = bookOrder.getSize();
         int tradedSize = Math.min(marketSize, limitSize);

         bookOrder.setSize(limitSize - tradedSize);
         marketSize -= tradedSize;

         if (bookOrder.getSize() == 0) {
            orderBook.complete(bookOrder);
         }
         if (marketSize == 0) {
            marketOrder.setSize(marketSize);
            orderBook.complete(marketOrder);
            return;
         }
      }

      if (marketSize > 0) {
         orderBook.incomplete(marketOrder);
      }

   }

   private void matchLimitOrder(OrderBook orderBook, LimitOrder limitOrder) {

   }

   private void matchStopOrder(OrderBook orderBook, StopOrder stopOrder) {
      // check if it exists
      //if (???) {...}

      Type type = stopOrder.getType();
      int stopPrice = stopOrder.getPrice();

      boolean execute = false;
      int bestBookPrice = -1;
      if (type == Type.bid) {
         //prezzo massimo, compratore
         bestBookPrice = orderBook.getBestAskPrice();
         if (bestBookPrice != -1 && bestBookPrice >= stopPrice)
            execute = true;
      } else if (type == Type.ask) {
         //prezzo minimo, venditore
         bestBookPrice = orderBook.getBestBidPrice();
         if (bestBookPrice != -1 && bestBookPrice <= stopPrice)
            execute = true;
      }

      if (execute) {
         //convert to market order
         Order order = (Order) stopOrder;
         MarketOrder marketOrder = (MarketOrder) order;
         
      }
   }
}
