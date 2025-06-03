package it.unipi.cross.persistence;

import java.util.List;

import it.unipi.cross.data.Order;

public class OrderList {
   public List<Order> orders;

   public OrderList() {
   }

   public OrderList(List<Order> orders) {
      this.orders = orders;
   }
}
