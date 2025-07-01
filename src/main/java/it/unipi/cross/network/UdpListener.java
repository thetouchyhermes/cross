package it.unipi.cross.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import it.unipi.cross.client.Prompt;
import it.unipi.cross.data.Trade;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.Notification;

public class UdpListener implements Runnable {
   private int udpPort;
   private final String username;

   private DatagramSocket socket;
   private volatile boolean running = false;

   public UdpListener(int udpPort, String username) {
      this.udpPort = udpPort;
      this.username = username;
   }

   @Override
   public void run() {
      try {
         //// Create DatagramSocket bound to available port for receiving unicast messages
         for (int port = udpPort; port < udpPort + 50; port++) {
            try {
               socket = new DatagramSocket(port, InetAddress.getLocalHost());
               udpPort = port;
               break;
            } catch (SocketException e) {
               // Debug: 
               // System.out.println("Port " + port + " already in use");
            }
         }

         if (socket == null || !socket.isBound()) {
            Prompt.printError("[UdpListener] Could not find an available port");
            return;
         }
         // socket.setSoTimeout(5000); // 5 second timeout

         // debug:
         // System.out.println("[UdpListener] Listening on port " + udpPort + " for user " + username);

         running = true;
         while (running) {
            String message = pullMessage(socket);
            if (message != null && !message.isEmpty()) {
               List<Trade> trades = pullTrades(message);
               if (!trades.isEmpty()) {
                  System.out.println("\n" + new Notification(trades).toString() + "\n");
               }
            }
         }

      } catch (IOException e) {
         if (running) {
            Prompt.printError("[UdpListener] " + e.getClass() + ": " + e.getMessage());
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

   public int getPort() {
      // wait for udpListener to be ready
      while(!running) {
         try {
            Thread.sleep(200);
         } catch (InterruptedException e) {
            // client closed
            return -1;
         }
      }
      return udpPort;
   }
}