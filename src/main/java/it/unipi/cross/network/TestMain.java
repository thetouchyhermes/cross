package it.unipi.cross.network;

public class TestMain {
   public static void main(String[] args) throws Exception {
      // Start listener first
      UdpListener listener = new UdpListener("230.0.0.1", 12345, "testuser");
      new Thread(listener).start();

      // Wait for listener to be ready
      Thread.sleep(2000);

      // Now send messages
      UdpNotifier notifier = new UdpNotifier("230.0.0.1", 12345);

      // Send a few test messages
      for (int i = 1; i <= 5; i++) {
         notifier.notify("Test message " + i);
         Thread.sleep(1000);
      }
   }
}
