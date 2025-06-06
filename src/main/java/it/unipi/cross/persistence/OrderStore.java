package it.unipi.cross.persistence;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.unipi.cross.data.Order;
import it.unipi.cross.json.JsonUtil;

public class OrderStore {

   public static List<Order> loadOrders(File file) throws IOException {
      OrderList orderList = JsonUtil.readFromFile(file, OrderList.class);
      return orderList.orders;
   }

   public static void saveOrders(List<Order> orders, File file) throws IOException {
      JsonUtil.writeToFile(file, new OrderList(orders));
   }

}
