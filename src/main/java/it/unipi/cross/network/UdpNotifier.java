package it.unipi.cross.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

public class UdpNotifier {
   private DatagramSocket socket;
   private Map<String, Integer> clientPorts;

   public UdpNotifier() throws IOException {
      this.socket = new DatagramSocket();
      this.clientPorts = new LinkedHashMap<>();
      System.out.println("[UdpNotifier] active");
   }

   public synchronized void addClient(String username, int clientPort) {
      if (!clientPorts.containsKey(username)) {
         clientPorts.put(username, clientPort);
         // Debug:
         // System.out.println("[UdpNotifier] Added " + username + " at port " +
         // clientPort);
      }
   }

   public synchronized void removeClient(String username) {
      clientPorts.remove(username);
      // Debug:
      // System.out.println("[UdpNotifier] Removed client " + username);
   }

   public void notifyClient(String username, String message) {
      if (!clientPorts.containsKey(username) || clientPorts.get(username) == null) {
         // client not connected
         return;
      }

      Integer port = clientPorts.get(username);
      byte[] data = message.getBytes();

      // send message through UDP with a short wait to make the TCP response arrive
      // first
      Thread notifyThread = new Thread(() -> {
         try {
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), port);
            Thread.sleep(500);

            socket.send(packet);
         } catch (IOException e) {
            System.err.println("[UdpNotifier] Error during notification");
         } catch (Exception e) {
            // server was closed
         }
      });
      notifyThread.setDaemon(true);
      notifyThread.start();

      System.out.println("[UdpNotifier] sent message to " + username +
            " at port " + port);
   }

   public void close() {
      if (socket != null && !socket.isClosed()) {
         socket.close();
         System.out.println("[UdpNotifier] closed");
      }
   }

}