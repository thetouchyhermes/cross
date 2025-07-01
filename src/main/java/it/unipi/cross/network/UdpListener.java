package it.unipi.cross.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import it.unipi.cross.data.Trade;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.Notification;

public class UdpListener implements Runnable {
   private final int udpPort;
   private final String username;

   private DatagramSocket socket;
   private volatile boolean running = true;

   public UdpListener(int udpPort, String username) {
      this.udpPort = udpPort;
      this.username = username;
   }

   @Override
   public void run() {
      try {
         // Create DatagramSocket bound to specific port for receiving unicast messages
         socket = new DatagramSocket(udpPort, InetAddress.getLocalHost());
         // socket.setSoTimeout(5000); // 5 second timeout

         System.out.println("[UdpListener] Listening on port " + udpPort + " for user " + username);

         while (running) {
            String message = pullMessage(socket);
            if (message != null && !message.isEmpty()) {
               List<Trade> trades = pullTrades(message);
               if (!trades.isEmpty()) {
                  System.out.println(new Notification(trades).toString());
               }
            }
         }

      } catch (IOException e) {
         if (running) {
            System.err.println("[UdpListener] " + e.getClass() + ": " + e.getMessage());
         }
      } finally {
         if (socket != null && !socket.isClosed()) {
            socket.close();
         }
      }
   }

   private String pullMessage(DatagramSocket socket) throws IOException {
      byte[] buf = new byte[1024];
         DatagramPacket packet = new DatagramPacket(buf, buf.length);

         socket.receive(packet);

         String message = new String(packet.getData(), 0, packet.getLength());
         return message;
   }

   private List<Trade> pullTrades(String message) {
      Notification notification = JsonUtil.fromJson(message, Notification.class);

      if (notification == null) {
         return new LinkedList<>();
      }

      List<Trade> trades = new LinkedList<>(notification.getTrades());
      return trades;
   }

   public void shutdown() {
      running = false;
      if (socket != null && !socket.isClosed()) {
         socket.close();
      }
   }

   public int getLocalPort() {
      return socket != null ? socket.getLocalPort() : -1;
   }
}