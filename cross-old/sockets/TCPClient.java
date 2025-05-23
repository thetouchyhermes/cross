package cross.sockets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import cross.Prompt;
import cross.config.ConfigReader;
import cross.requests.ErrorRequest;
import cross.requests.MessageResponse;
import cross.requests.OrderResponse;
import cross.requests.Request;
import cross.requests.Response;

public class TCPClient extends TCP {

   private ConfigReader config;
   private String serverAddress;
   private int serverPort;

   private Socket socket;
   private JsonReader reader;
   private PrintWriter writer;

   public TCPClient() {
      this.config = new ConfigReader("src/main/resources/client_config.properties");
      serverAddress = config.getProperty("server.address");
      serverPort = Integer.parseInt(config.getProperty("server.port"));

      try {
         InetAddress ipServerAddress = InetAddress.getByName(serverAddress);
         socket = new Socket(ipServerAddress, serverPort);

         reader = new JsonReader(new InputStreamReader(socket.getInputStream()));
         writer = new PrintWriter(socket.getOutputStream(), true);

         if (!isAlive()) {
            Prompt.printError("TCPClient: Socket not alive at start");
            Prompt.exit(1);
         }
         System.out.println(Prompt.GREEN + "Connection established" + Prompt.RESET);
      } catch (UnknownHostException e) {
         Prompt.printError("TCPClient: IP address for " + serverAddress + " not found");
         Prompt.exit(1);
      } catch (IOException e) {
         Prompt.printError("TCPClient: Error during socket creation");
         Prompt.exit(1);
      } catch (Exception e) {
         Prompt.printError(e.getClass() + ": " + e.getMessage());
      }
   }

   @Override
   public void stop() {
      try {
         if (isAlive()) {
            socket.close();
            System.out.println("Connection closed");
         } else {
            Prompt.printError("TCPClient: Socket not alive at closing");
            Prompt.exit(1);
         }
      } catch (IOException e) {
         Prompt.printError("TCPClient: Error closing socket");
         Prompt.exit(1);
      }
   }

   @Override
   public boolean isAlive() {
      return socket != null && socket.isConnected() && !socket.isClosed();
   }

   public Response send(Request request) {
      if (!isAlive()) {
         Prompt.printError("TCPClient: Socket not alive on request sending");
         Prompt.exit(1);
      }
      writer.print(request.toString());
      System.out.println("Sent request to server");

      // receiving response
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
      //to delete
      System.out.println("Response json: " + jsonObject.toString());
      String errorMessage = jsonObject.get("errorMessage").getAsString();
      //to delete
      System.out.println("errormessage: " + errorMessage);
      if (errorMessage != null) {
         int response = jsonObject.get("response").getAsInt();
         
         MessageResponse messageResponse = new MessageResponse();
         messageResponse.setResponse(response);
         messageResponse.setErrorMessage(errorMessage);
         return messageResponse;
      }
      JsonElement idElement = jsonObject.get("orderId");
      if (idElement != null) {
         int orderId = idElement.getAsInt();
         OrderResponse orderResponse = new OrderResponse();
         orderResponse.setOrderId(orderId);
         return orderResponse;
      }
      Response response = gson.fromJson(jsonObject, Response.class);
      return response;
   }

   public void keepServerAlive(){
      // send an empty message to server to make timeout reset
      send(new ErrorRequest());
   }
}
