package cross;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.stream.JsonReader;

import cross.config.ConfigReader;

public class Stub_Server {

   public static void main(String[] args) {
      ConfigReader config = new ConfigReader("src/main/resources/client_config.properties");
      String serverAddress = config.getProperty("server.address");
      int serverPort = Integer.parseInt(config.getProperty("server.port"));

      try {
         InetAddress ipServerAddress = InetAddress.getByName(serverAddress);
         Socket socket = new Socket(ipServerAddress, serverPort);

         JsonReader reader = new JsonReader(new InputStreamReader(socket.getInputStream()));
         PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

         if (socket == null || !socket.isConnected() || socket.isClosed()) {
            Prompt.printError("TCPClient: Socket not alive at start");
            Prompt.exit(1);
         }
         System.out.println("Connection established");
         Thread.sleep(7000);
         reader.close();
         writer.close();
         socket.close();
         System.out.println("Socket closed");

      } catch (UnknownHostException e) {
         Prompt.printError("TCPClient: IP address for " + serverAddress + " not found");
         Prompt.exit(1);
      } catch (IOException e) {
         Prompt.printError("TCPClient: Error during socket use");
         Prompt.exit(1);
      } catch (InterruptedException e) {
         Prompt.printError("TCPClient: Interrupted sleep");
         Prompt.exit(1);
      }
   }

}
