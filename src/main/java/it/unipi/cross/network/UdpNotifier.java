package it.unipi.cross.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UdpNotifier {
   private final String udpAddress;
   private final int udpPort;

   public UdpNotifier(String udpAddress, int udpPort) {
      this.udpAddress = udpAddress;
      this.udpPort = udpPort;
   }

   public void notify(String message) throws IOException {
      try (MulticastSocket socket = new MulticastSocket()) {
         byte[] data = message.getBytes();
         InetAddress addr = InetAddress.getByName(udpAddress);
         DatagramPacket packet = new DatagramPacket(data, data.length, addr, udpPort);
         socket.send(packet);
      }
   }
}
