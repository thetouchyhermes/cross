package it.unipi.cross.network;

import java.time.Instant;
import java.util.Collections;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.Trade;
import it.unipi.cross.data.Type;
import it.unipi.cross.json.Notification;

public class TestUdpMain {

   public static void main(String[] args) throws Exception {
      // Load UDP port from config
      ConfigReader config = new ConfigReader();
      config.loadServer();
      int udpPort = config.getInt("udp.port");

      // Start listener in a separate thread
      UdpListener listener = new UdpListener(udpPort, "testuser");
      Thread listenerThread = new Thread(listener);
      listenerThread.start();

      // Wait for listener to be ready and get its port
      int clientPort = listener.getPort();
      System.out.println("Listener running on port: " + clientPort);

      // Simulate login
      listener.isLogged();

      // Start notifier
      UdpNotifier notifier = new UdpNotifier();
      String username = "testuser";
      notifier.addClient(username, clientPort);

      // Create a dummy trade and notification
      MarketOrder order = new MarketOrder("testuser", Type.ask, 45, Instant.now().getEpochSecond());
      Trade trade = new Trade(order, 10, 1000);

      Notification notification = new Notification(Collections.singletonList(trade));
      String message = notification.toString();

      // Send notification
      notifier.notifyClient(username, message);

      // Wait a bit to ensure message is received
      Thread.sleep(1000);

      // Cleanup
      notifier.removeClient(username);
      notifier.close();
      listener.shutdown();
      listenerThread.join();
      System.out.println("Test finished.");
   }
}