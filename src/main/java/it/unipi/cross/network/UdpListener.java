package it.unipi.cross.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.List;

import it.unipi.cross.data.Trade;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.Notification;

public class UdpListener implements Runnable {
   private final String udpAddress;
   private final int udpPort;
   private final String username;

   private MulticastSocket socket;
   private volatile boolean running = true;

   public UdpListener(String udpAddress, int udpPort, String username) {
      this.udpAddress = udpAddress;
      this.udpPort = udpPort;
      this.username = username;
   }

   @Override
   public void run() {
      try (MulticastSocket socket = new MulticastSocket(udpPort)) {
         this.socket = socket;
         InetAddress addr = InetAddress.getByName(udpAddress);
         // SocketAddress sockAddr = new InetSocketAddress(addr, udpPort);
         // NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

         // MulticastSocket.joinGroup(InetAddress) is deprecated since Java 9
         socket.joinGroup(addr);
         
         while(running) {
            
            String message = pullMessage(socket);

            if (message != null && !message.isEmpty()) {
               List<Trade> trades = pullTrades(message);
               if (!trades.isEmpty()) {
                  System.out.println(new Notification(trades).toString());
               }
            }
         }

         socket.leaveGroup(addr);
      } catch (Exception e) {
         if (running) {
            System.err.println("[UdpListener] " + e.getClass() + e.getMessage());
         }
      }
   }

   private String pullMessage(MulticastSocket socket) throws IOException {
      byte[] data = new byte[1024];
      DatagramPacket packet = new DatagramPacket(data, data.length);
      socket.receive(packet);
      String message = new String(packet.getData(), 0, packet.getLength());

      return message;
   }

   private List<Trade> pullTrades(String message) {
      Notification notification = JsonUtil.fromJson(message, Notification.class);

      List<Trade> trades = new LinkedList<>();
      if (notification != null) {
         for (Trade trade : notification.getTrades()) {
            if (trade.getUsername().equals(username)) {
               trade.setUsername("");
               trades.add(trade);
            }
         }
      }
      return trades;
   }

   public void shutdown() {
      running = false;
      if (socket != null && !socket.isClosed()) {
         socket.close();
      }
   }
}
