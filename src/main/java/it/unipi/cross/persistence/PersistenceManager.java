package it.unipi.cross.persistence;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.User;

/**
 * The {@code PersistenceManager} class is responsible for managing the
 * persistence
 * of {@link User} and {@link Order} objects. It loads and saves user and order
 * data
 * to files specified in a configuration properties file.
 * <p>
 * The file paths for user and order data are read from a configuration file
 * (default: {@code src/main/resources/server_config.properties}) using the
 * {@link ConfigReader}.
 * </p>
 *
 * <ul>
 * <li>{@code loadAll()} loads users and orders from their respective files into
 * the provided lists.</li>
 * <li>{@code saveAll()} saves the current users and orders to their respective
 * files.</li>
 * </ul>
 *
 * <p>
 * If the configuration file cannot be read, the application will print an error
 * and terminate.
 * </p>
 */
public class PersistenceManager {

   private final String CONFIG_FILE = "src/main/resources/server_config.properties";

   private final List<User> users;
   private final List<Order> orders;
   private final File userFile;
   private final File orderFile;

   public PersistenceManager(List<User> users, List<Order> orders) {
      this.users = users;
      this.orders = orders;
      orders.removeIf(order -> order.getOrderType() != OrderType.limit);
      
      try {
         ConfigReader config = new ConfigReader(CONFIG_FILE)
         userFile = new File(config.getString("persistence.user_file"));
         orderFile = new File(config.getString("persistence.order_file"));
      } catch (ConfigException e) {
         System.err.println(e.getMessage());
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
