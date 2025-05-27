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

   private void matchMarketOrder(OrderBook orderBook, MarketOrder order) {
      Type type = order.getType();
      int marketSize = order.getSize();

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
            order.setSize(marketSize);
            orderBook.complete(order);
            return;
         }
      }

      if (marketSize > 0) {
         orderBook.incomplete(order);
      }

   }

   private void matchLimitOrder(OrderBook orderBook, LimitOrder order) {

   }

   private void matchStopOrder(OrderBook orderBook, StopOrder order) {

   }
}
