package it.unipi.cross.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import it.unipi.cross.server.OrderBook;
import it.unipi.cross.server.UserBook;

public class TcpServer {

   private int tcpPort;
   private int timeout;
   private ExecutorService threadPool;
   private OrderBook orderBook;
   private UserBook userBook;
   private ServerSocket serverSocket;
   private Set<Socket> activeSockets;

   private volatile boolean running = false;

   public TcpServer(OrderBook orderBook, UserBook userBook, int tcpPort, int timeout) {
      this.orderBook = orderBook;
      this.userBook = userBook;
      this.tcpPort = tcpPort;
      this.timeout = timeout;
      this.threadPool = Executors.newCachedThreadPool();
      this.activeSockets = new LinkedHashSet<>();
   }

   public TcpServer(OrderBook orderBook, UserBook userBook, int tcpPort) {
      this(orderBook, userBook, tcpPort, 30000);
   }

   public TcpServer(OrderBook orderBook, UserBook userBook) {
      this(orderBook, userBook, 50000, 30000);
   }

   public void start() throws IOException {
      running = true;
      try {
         serverSocket = new ServerSocket(tcpPort);

         while (running) {
            try {
               Socket socket = serverSocket.accept();
               socket.setSoTimeout(timeout);
               synchronized (activeSockets) {
                  activeSockets.add(socket);
               }

               TcpWorker worker = new TcpWorker(socket, orderBook, userBook);
               threadPool.submit(worker);
            } catch (IOException e) {
               if (running)
                  throw e;
               else
                  break;
            }
         }
      } finally {
         stop();
      }
   }

   public void stop() {
      running = false;
      try {
         if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
         }
         threadPool.shutdownNow();
         synchronized (activeSockets) {
            for (Socket s : activeSockets) {
               System.out.println("closing " + s);
               s.close();
               System.out.println("closed " + s);

            }
            activeSockets.clear();
         }

         try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
               System.err.println("Executor did not terminate in the specified time.");
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      } catch (IOException e) {
         System.err.println("[TcpServer] Error closing server socket");
         // ignore
      }
      shutdown();
   }

   public void shutdown() {
      if (threadPool != null && !threadPool.isShutdown()) {
         threadPool.shutdownNow();
      }
      System.out.println("[TcpServer] Server stopped");
   }
}
