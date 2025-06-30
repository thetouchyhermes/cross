package it.unipi.cross.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UdpNotifierm {
   private final String udpAddress;
   private final int udpPort;

   public UdpNotifier(String udpAddress, int udpPort) {
      this.udpAddress = udpAddress;
      this.udpPort = udpPort;
      try {
         notify("udpNotifier is active\n");
      } catch (IOException e) {
         System.err.println("[UdpNotifier] Couldn't send first message");
      }
   }

   public void notify(String message) throws IOException {
      try (MulticastSocket socket = new MulticastSocket()) {

         socket.setTimeToLive(0);
         socket.setLoopbackMode(false); 

         InetAddress addr = InetAddress.getByName(udpAddress);
         socket.joinGroup(addr);

         byte[] data = message.getBytes();
         DatagramPacket packet = new DatagramPacket(data, data.length, addr, udpPort);
         System.out.println("Sent " + message + " to " + udpAddress + ":" + udpPort);
         socket.send(packet);

         socket.leaveGroup(addr);
      }
   }

   public void startPeriodicNotification(long intervalMs) {
      new Thread(() -> {
         int counter = 0;
         while (true) {
            try {
               notify("Periodic message " + (++counter));
               Thread.sleep(intervalMs);
            } catch (Exception e) {
               System.err.println("Error in periodic notification: " + e.getMessage());
               break;
            }
         }
      }).start();
   }
}
