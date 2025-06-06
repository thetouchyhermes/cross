package it.unipi.cross.server;

import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.data.Order;
import it.unipi.cross.data.User;
import it.unipi.cross.network.TcpServer;
import it.unipi.cross.network.UdpNotifier;
import it.unipi.cross.persistence.PersistenceManager;

public class ServerMain {

   public static void main(String[] args) {

      try {
         // load configuration
         ConfigReader config = new ConfigReader();
         try {
            config.loadServer();
         } catch (IOException e) {
            System.err.println("[ServerMain] " + e.getMessage());
            System.exit(1);
         }

         int tcpPort = config.getInt("tcp.port");
         int tcpTimeout = config.getInt("tcp.timeout");
         int udpPort = config.getInt("udp.port");
         String udpAddress = config.getString("udp.address");
         String userFilePath = config.getString("persistence.user_file");
         String orderFilePath = config.getString("persistence.order_file");
         int persistInterval = config.getInt("persistence.secs");

         // initialize data structures
         PersistenceManager persistenceManager = new PersistenceManager(userFilePath, orderFilePath);

         List<User> users = new ArrayList<>();
         List<Order> orders = new ArrayList<>();
         try {
            persistenceManager.loadAll(users, orders);
         } catch (IOException e) {
            System.err.println("[Server] error while loading persistence files: " + e.getMessage());
         }
         
         // set up UDP notifier
         UdpNotifier udpNotifier = new UdpNotifier(udpAddress, udpPort);

         UserBook userBook = new UserBook(users);
         OrderBook orderBook = new OrderBook(orders, udpNotifier);

         // schedule periodic persistence
         ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
         scheduler.scheduleAtFixedRate((() -> {
            try {
               persistenceManager.saveAll(userBook.getUserList(), orderBook.getOrderList());
            } catch (IOException e) {
               System.err.println("[Server] error persisting data: " + e.getMessage());
            }
         }), persistInterval, persistInterval, TimeUnit.SECONDS);

         // set up TCP server
         TcpServer tcpServer = new TcpServer(orderBook, userBook, tcpPort, tcpTimeout);

         System.out.println("[Server] started on TCP port " + tcpPort + ", UDP port " + udpPort);

         // Handler function for normal termination, exception and anomalous interruption
         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            
            try {
               if (tcpServer != null)
                  tcpServer.stop();
               if (scheduler != null)
                  scheduler.shutdownNow();
               
               persistenceManager.saveAll(userBook.getUserList(), orderBook.getOrderList());
            } catch (Exception e) {
               System.err.println("[ServerMain] error during shutdown: " + e.getMessage());
            }
            System.out.println("[Server] stopped");
         }));

         // start TCP server
         tcpServer.start();
      } catch (BindException e) {
         System.err.println("[Server] port is already in use");
         System.exit(1);
      } catch (Exception e) {
         System.err.println("[ServerMain] something failed: " + e.getMessage());
         e.printStackTrace();
      }
   }
}