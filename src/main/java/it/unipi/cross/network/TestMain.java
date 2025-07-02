package it.unipi.cross.network;

import java.time.Instant;

import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.Trade;
import it.unipi.cross.data.Type;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.Notification;

public class TestMain {
   public static void main(String[] args) throws Exception {
      // Start listener first
      UdpListener listener = new UdpListener(12345, "testuser");
      new Thread(listener).start();

      UdpListener listener2 = new UdpListener(36853, "alice");
      new Thread(listener2).start();

      // Wait for listener to be ready
      Thread.sleep(2000);

      // Now send messages
      UdpNotifier notifier = new UdpNotifier();

      notifier.addClient("testuser", 12345);
      // Send a few test messages
      for (int i = 1; i <= 5; i++) {
         Trade t = new Trade(new MarketOrder(i, "testuser", Type.ask, 3+i, Instant.now().getEpochSecond()), 3, 400+i*2);
         Notification notif = new Notification(t);
         notifier.notifyClient("testuser", JsonUtil.toJson(notif));
         Thread.sleep(1000);
      }
      
      notifier.addClient("alice", 36853);
      
      Trade t = new Trade(new MarketOrder(6, "alice", Type.ask, 5, Instant.now().getEpochSecond()), 3, 520);
      Notification notif = new Notification(t);
      notifier.notifyClient("alice", JsonUtil.toJson(notif));
   }
}
