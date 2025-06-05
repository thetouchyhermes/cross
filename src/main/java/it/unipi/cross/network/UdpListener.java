package it.unipi.cross.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

public class UdpListener implements Runnable {
   private final String udpAddress;
   private final int udpPort;

   private MulticastSocket socket;
   private volatile boolean running = true;

   public UdpListener(String udpAddress, int udpPort) {
      this.udpAddress = udpAddress;
      this.udpPort = udpPort;
   }

   @Override
   public void run() {
      try (MulticastSocket socket = new MulticastSocket(udpPort)) {
         this.socket = socket;
         InetAddress addr = InetAddress.getByName(udpAddress);
         SocketAddress sockAddr = new InetSocketAddress(addr, udpPort);
         NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

         // MulticastSocket.joinGroup(InetAddress) is deprecated since Java 9
         socket.joinGroup(sockAddr, netIf);

         byte[] data = new byte[1024];
         while(running) {
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("[NOTIFICATION] " + message);
         }

         socket.leaveGroup(sockAddr, netIf);
      } catch (Exception e) {
         if (running) {
            System.err.println("[UdpListener] " + e.getClass() + e.getMessage());
         }
      }
   }

   public void shutdown() {
      running = false;
      if (socket != null && !socket.isClosed()) {
         socket.close();
      }
   }
}
