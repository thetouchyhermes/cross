package it.unipi.cross.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.OrderType;
import it.unipi.cross.data.User;

/**
 * The {@code PersistenceManager} class is responsible for managing the persistence
 * of {@link User} and {@link Order} objects. It loads and saves user and order data
 * from and to files specified in the configuration.
 * <p>
 * The class uses {@link UserStore} and {@link OrderStore} for serialization and
 * deserialization of users and orders, respectively. Only orders of type
 * {@link OrderType#limit} are persisted when saving.
 * </p>
 *
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with lists of users and orders (can be null).</li>
 *   <li>Call {@link #loadAll()} to load data from files.</li>
 *   <li>Call {@link #saveAll()} to persist current users and limit orders to files.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Configuration file keys required:
 * <ul>
 *   <li>{@code persistence.user_file} - Path to the user data file.</li>
 *   <li>{@code persistence.order_file} - Path to the order data file.</li>
 * </ul>
 * </p>
 */
public class PersistenceManager {

   private List<User> users;
   private List<Order> orders;
   private File userFile;
   private File orderFile;

   public PersistenceManager(List<User> users, List<Order> orders) {
      this.users = (users != null) ? users : new ArrayList<>();
      this.orders = (orders != null) ? orders : new ArrayList<>();
      
      try {
         ConfigReader config = new ConfigReader();
         config.loadServer();
         userFile = new File(config.getString("persistence.user_file"));
         orderFile = new File(config.getString("persistence.order_file"));
      } catch (IOException e) {
         System.err.println("[PersistenceManager] " + e.getMessage());
         System.exit(1);
      }      
   }

   public void loadAll() throws IOException {
      users.clear();
      users.addAll(UserStore.loadUsers(userFile));
      orders.clear();
      orders.addAll(OrderStore.loadOrders(orderFile));
   }

   public void saveAll() throws IOException {
      UserStore.saveUsers(users, userFile);
      OrderStore.saveOrders(orders, orderFile);
   }

}
