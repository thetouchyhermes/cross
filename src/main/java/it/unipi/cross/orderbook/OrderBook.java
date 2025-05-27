package it.unipi.cross.orderbook;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.Order;

public class OrderBook {

   private final BlockingQueue<Order> newOrders = new LinkedBlockingQueue<>();
   private final Map<Integer, Order> currentOrders = new ConcurrentHashMap<>();

   private final NavigableSet<LimitOrder> bidBook = new TreeSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).reversed().thenComparingLong(LimitOrder::getTimestamp));
   private final NavigableSet<LimitOrder> askBook = new TreeSet<>(
         Comparator.comparingInt(LimitOrder::getPrice).thenComparingLong(LimitOrder::getTimestamp));

   public OrderBook() {
      
   }

   public int insertOrder(Order order) {

   }

   public boolean deleteOrder(int idOrder) {

   }

   public NavigableSet<LimitOrder> getBidBook() {
      return bidBook;
   }

   public NavigableSet<LimitOrder> getAskBook() {
      return askBook;
   }

}
