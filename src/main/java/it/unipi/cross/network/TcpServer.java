package it.unipi.cross.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.server.OrderBook;
import it.unipi.cross.server.UserBook;

public class TcpServer {
   private String tcpAddress;
   private int tcpPort;
   private ExecutorService threadPool;
   private OrderBook orderBook;
   private UserBook userBook;

   public TcpServer(OrderBook orderBook, UserBook userBook, ConfigReader config) {
      this.orderBook = orderBook;
      this.userBook = userBook;
      /**
       * this.tcpAddress = config.getString("tcp.address");
       * if (this.tcpAddress.isEmpty()) {
       * this.tcpAddress = "localhost";
       * }
       */
      this.tcpPort = config.getInt("tcp.port");
      if (this.tcpPort == -1) {
         this.tcpPort = 50000;
      }

   }

   public void start() throws Exception {
      try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
         System.out.println("[TcpServer] Server started on port " + tcpPort);

         while (true) {
            Socket socket = serverSocket.accept();
            TcpWorker worker = new TcpWorker(socket, orderBook, userBook);
            threadPool.submit(worker);
         }
      }
   }
}
