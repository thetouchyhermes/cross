package it.unipi.cross.persistence;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.unipi.cross.data.Order;
import it.unipi.cross.data.User;


/**
 * The {@code PersistenceManager} class provides methods to manage the persistence
 * of {@link User} and {@link Order} objects to and from files.
 * <p>
 * It encapsulates file paths for user and order data, and delegates the actual
 * loading and saving operations to {@link UserStore} and {@link OrderStore}.
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>
 *     PersistenceManager pm = new PersistenceManager("users.dat", "orders.dat");
 *     List&lt;User&gt; users = new ArrayList&lt;&gt;();
 *     List&lt;Order&gt; orders = new ArrayList&lt;&gt;();
 *     pm.loadAll(users, orders);
 *     // ... modify users and orders ...
 *     pm.saveAll(users, orders);
 * </pre>
 * </p>
 */
public class PersistenceManager {

   private File userFile;
   private File orderFile;

   public PersistenceManager(String userFilePath, String orderFilePath) {
      this.userFile = new File(userFilePath);
      this.orderFile = new File(orderFilePath);
   }

   public void loadAll(List<User> users, List<Order> orders) throws IOException {
      users.clear();
      orders.clear();

      if (userFile.exists() && userFile.length() > 0) {
         users.addAll(UserStore.loadUsers(userFile));
      }
      if (orderFile.exists() && orderFile.length() > 0) {
         orders.addAll(OrderStore.loadOrders(orderFile));
      }
      
   }

   public void saveAll(List<User> users, List<Order> orders) throws IOException {
      if (userFile.getParentFile() != null) {
         userFile.getParentFile().mkdirs();
      }
      if (orderFile.getParentFile() != null) {
         orderFile.getParentFile().mkdirs();
      }
      
      UserStore.saveUsers(users, userFile);
      OrderStore.saveOrders(orders, orderFile);
   }

}
