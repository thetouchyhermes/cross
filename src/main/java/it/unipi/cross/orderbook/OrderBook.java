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

public class OrderBook {

   private final BlockingQueue<Command> newCommands = new LinkedBlockingQueue<>();
   private final Map<Integer, Order> currentOrders = new ConcurrentHashMap<>();

   private final NavigableSet<LimitOrder> bidBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).reversed().thenComparingLong(LimitOrder::getTimestamp));
   private final NavigableSet<LimitOrder> askBook = new ConcurrentSkipListSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).thenComparingLong(LimitOrder::getTimestamp));

   public OrderBook() {
      
   }

   public int insertOrder(Order order) {

   }

   public void deleteOrder(int idOrder) {

   }

   public NavigableSet<LimitOrder> getBidBook() {
      return bidBook;
   }

   public NavigableSet<LimitOrder> getAskBook() {
      return askBook;
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
      // if stop respond with -1 to user
      // archive trade
      // send notification of trade completion
   }

}
