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
      System.out.println("[UdpNotifier] UDP notifier is active on port " + socket.getLocalPort());
   }

   public synchronized void addClient(String username, int clientPort) {
      if (!clientPorts.containsKey(username)) {
         clientPorts.put(username, clientPort);
         System.out.println("[UdpNotifier] Added client " + username + " at port " + clientPort);
      }
   }

   public synchronized void removeClient(String username) {
      clientPorts.remove(username);
      System.out.println("[UdpNotifier] Removed client " + username);
   }

   // synchronized ???
   public synchronized void notifyClient(String username, String message) throws IOException {
      int port = clientPorts.get(username);
      if (port <= 0) {
         System.err.println("[UdpNotifier] Failed to find client " + username + " at port " + port);
         return;
      }

      byte[] data = message.getBytes();
      DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), port);

      socket.send(packet);
      System.out.println("[UdpNotifier] Sent message to " + username +
            " at port " + port);
   }

   public void close() {
      if (socket != null && !socket.isClosed()) {
         socket.close();
         System.out.println("[UdpNotifier] UDP notifier closed");
      }
   }

   public int getClients() {
      return clientPorts.size();
   }
   
}