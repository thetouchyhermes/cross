package it.unipi.cross.persistence;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.unipi.cross.data.Order;
import it.unipi.cross.data.Trade;
import it.unipi.cross.data.User;
import it.unipi.cross.json.JsonUtil;

public class PersistenceManager {

   private File userFile;
   private File orderFile;
   private File tradeFile;

   public PersistenceManager(String userFilePath, String orderFilePath, String tradeFilePath) {
      this.userFile = new File(userFilePath);
      this.orderFile = new File(orderFilePath);
      this.tradeFile = new File(tradeFilePath);
   }

   public void loadAll(List<User> users, List<Order> orders) throws IOException {
      users.clear();
      orders.clear();

      if (userFile.exists() && userFile.length() > 0) {
         users.addAll(JsonUtil.readListFromFile(userFile, User.class));
      }
      if (orderFile.exists() && orderFile.length() > 0) {
         orders.addAll(JsonUtil.readListFromFile(orderFile, Order.class));
      }
      
   }

   public <T> void save(File objectFile, List<T> objects, boolean streaming) throws IOException {
      if (objects != null && !objects.isEmpty()) {
         if (objectFile.getParentFile() != null) {
            objectFile.getParentFile().mkdirs();
         }

         if (streaming) {
            String objectsName = objects.get(0).getClass().getSimpleName().toLowerCase().concat("s");
            StreamingUtil.appendObjectToFile(objectFile, objects, objectsName);
         } else {
            JsonUtil.writeListToFile(objectFile, objects);
         }
      }
   }

   public void saveAll(List<User> users, List<Order> orders, List<Trade> trades) throws IOException {
      save(userFile, users, false);
      save(orderFile, orders, false);
      save(tradeFile, trades, true);
   }

}
