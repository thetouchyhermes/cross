package it.unipi.cross.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import com.google.gson.JsonSyntaxException;

import it.unipi.cross.client.Prompt;
import it.unipi.cross.data.Trade;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.Notification;
import it.unipi.cross.json.OrderResponse;

public class UdpListener implements Runnable {
   private int udpPort;
   private String username;

   private DatagramSocket socket;
   private volatile boolean running = false;
   private volatile boolean logged = false;

   public UdpListener(int udpPort, String username) {
      this.udpPort = udpPort;
      this.username = username;
   }

   @Override
   public void run() {
      try {
         //// Create DatagramSocket bound to available port for receiving unicast
         //// messages
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

         running = true;

         while (running && !logged) {
            // wait for login to be okay
            Thread.sleep(500);
         }

         if (logged) {
            // debug:
            // System.out.println("[UdpListener] Listening on port " + udpPort);
         }

         while (running) {
            String message = pullMessage(socket);
            if (message != null && !message.isEmpty()) {
               List<Trade> trades = pullTrades(message);

               if (trades == null || trades.isEmpty()) {
                  OrderResponse orderResponse = pullOrderResponse(message);
                  if (orderResponse != null) {
                     System.out.println("\nFailed stop order (id: " + orderResponse.getOrderId() + ")");
                     System.out.println(new OrderResponse(-1).toString() + "\n");
                  }
               } else {
                  if (username.equals(trades.get(0).getUsername())) {
                     System.out.println("\n" + new Notification(trades).toString() + "\n");
                  }
               }

               Prompt.newLine(username);
            }
         }

      } catch (IOException e) {
         if (running) {
            Prompt.printError("[UdpListener] " + e.getClass() + ": " + e.getMessage());
         }
      } catch (InterruptedException e) {
         // shutdown during login wait
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
      try {
         Notification notification = JsonUtil.fromJson(message, Notification.class);
         return notification.getTrades();

      } catch (JsonSyntaxException e) {
         return null;
      }
   }

   private OrderResponse pullOrderResponse(String message) {
      try {
         OrderResponse orderResponse = JsonUtil.fromJson(message, OrderResponse.class);
         return orderResponse;

      } catch (JsonSyntaxException e) {
         return null;
      }
   }

   public void shutdown() {
      running = false;
      if (socket != null && !socket.isClosed()) {
         socket.close();
      }
   }

   public int getPort() {
      // wait for udpListener to be ready
      while (!running) {
         try {
            Thread.sleep(200);
         } catch (InterruptedException e) {
            // client closed
            return -1;
         }
      }
      return udpPort;
   }

   public void isLogged() {
      logged = true;
   }
}