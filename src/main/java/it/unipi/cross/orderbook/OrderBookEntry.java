package it.unipi.cross.orderbook;

import it.unipi.cross.data.Order;
import it.unipi.cross.data.OrderStatus;
import it.unipi.cross.data.User;

public class OrderBookEntry {
   private final Order order;
   private final String username;
   private OrderStatus status;
   
   public OrderBookEntry(Order order, ) {
      this.order = order;
      this.status = OrderStatus.OPEN;
   }

   public OrderStatus getStatus() {
      return status;
   }

   public void setStatus(OrderStatus status) {
      this.status = status;
   }
}
