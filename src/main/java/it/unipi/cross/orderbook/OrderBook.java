package it.unipi.cross.orderbook;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.StopOrder;

public class OrderBook {

   private final BlockingQueue<Command> newCommands = new LinkedBlockingQueue<>();
   private final Map<Integer, StopOrder> stopOrders = new ConcurrentHashMap<>();

   private final NavigableSet<LimitOrder> bidBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).reversed().thenComparingLong(LimitOrder::getTimestamp));
   private final NavigableSet<LimitOrder> askBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).thenComparingLong(LimitOrder::getTimestamp));

   private int bestBidPrice = -1;
   private int bestAskPrice = -1;

   public OrderBook() {
      
   }

   public int insertOrder(Order order) {

   }

   public void deleteOrder(int orderId) {

   }

   public StopOrder getStopOrderById(int orderId) {
      return stopOrders.get(orderId);
   }

   public NavigableSet<LimitOrder> getBidBook() {
      return bidBook;
   }

   public NavigableSet<LimitOrder> getAskBook() {
      return askBook;
   }

   public int getBestBidPrice() {
      return bestBidPrice;
   }

   public int getBestAskPrice() {
      return bestAskPrice;
   }

   public void complete(Order order) {
      // remove from orderbook
      // if market respond with orderId to user
      // archive trade
      // send notification of trade completion
   }

   public void incomplete(Order order) {
      // remove from orderbook
      // if market respond with -1 to user
   }

}
