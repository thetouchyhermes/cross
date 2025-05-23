package cross.sockets;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import cross.Prompt;
import cross.config.ConfigReader;

public class TCPServer extends TCP{

   private ConfigReader config;
   private int serverPort;
   private ServerSocket server;
   private Set<Socket> activeSockets;
   
   public TCPServer() {
      this.config = new ConfigReader("src/main/resources/client_config.properties");
      this.serverPort = Integer.parseInt(config.getProperty("server.port"));
      this.activeSockets = new HashSet<Socket>();

      try{
         server = new ServerSocket(serverPort);
      } catch (BindException e)  {
         Prompt.printError("TCPServer: Port " + serverPort + " busy");
      } catch (IOException e) {
         Prompt.printError("TCPServer: Error opening socket");
      } catch (IllegalArgumentException e) {
         Prompt.printError("TCPServer: " + serverPort + " is not an accepted port");
      }
   }

   public Socket listen() throws NoSuchElementException {
      Socket socket = null;
      try {
            socket = server.accept();
            synchronized (activeSockets) {
               activeSockets.add(socket);
            }
      } catch (IOException e) {
         Prompt.printError("TCPServer: Error accepting client and creating its socket");
      }
      return socket;
   }

   @Override
   public void stop() {
      try {
         
         server.close();
         synchronized (activeSockets) {
            for (Socket s : activeSockets)
               s.close();
            activeSockets.clear();
         }
      } catch (IOException e) {
         Prompt.printError("TCPServer: Error closing server socket or client sockets");
         Prompt.exit(1);
      }
   }

   @Override
   public boolean isAlive() {
      return server != null && !server.isClosed();
   }

   public void removeSocket(Socket socket) {
      synchronized (activeSockets) {
         activeSockets.remove(socket);
      }
   }

}
