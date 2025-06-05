package cross.sockets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import cross.Prompt;
import cross.requests.Request;
import cross.requests.Response;
import cross.requests.ResponseFactory;
import cross.values.CancelOrderValues;
import cross.values.CredentialsValues;
import cross.values.HistoryValues;
import cross.values.OrderValues;
import cross.values.PriceOrderValues;
import cross.values.UpdateCredentialsValues;
import cross.values.Values;

public class SocketTask implements Runnable {
   private Socket socket;
   private TCPServer tcpServer;

   public SocketTask(Socket socket, TCPServer tcpServer) {
      this.socket = socket;
      this.tcpServer = tcpServer;
   }

   @Override
   public void run() {
      //to edit
      System.out.println("SocketTask: Connected client " + socket);

      try (
      JsonReader reader = new JsonReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

         Gson gson = new Gson();
         while (socket.isConnected() && !socket.isClosed()) {
            
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String operation = jsonObject.get("operation").getAsString();
            JsonElement elem = jsonObject.get("values");
            Values values = null;

            switch (operation) {
               case "register":
               case "login":
                  values = gson.fromJson(elem, CredentialsValues.class);
                  break;
               case "updateCredentials":
                  values = gson.fromJson(elem, UpdateCredentialsValues.class);
                  break;
               case "insertLimitOrder":
               case "insertStopOrder":
                  values = gson.fromJson(elem, PriceOrderValues.class);
                  break;
               case "insertMarketOrder":
                  values = gson.fromJson(elem, OrderValues.class);
                  break;
               case "cancelOrder":
                  values = gson.fromJson(elem, CancelOrderValues.class);
                  break;
               case "getPriceHistory":
                  values = gson.fromJson(elem, HistoryValues.class);
                  break;
               case "logout":
               default:
                  values = gson.fromJson(elem, Values.class);
            }

            Request request = new Request();
            request.setOperation(operation);
            request.setValues(values);
            
            //generate Response
            Response response = ResponseFactory.create(request);
            writer.println(response.toString());
            }

      } catch (IOException e) {
         Prompt.printError("SocketTask: Error while working with client socket");
      } finally {
         tcpServer.removeSocket(socket);
         if (socket.isConnected() && !socket.isClosed()) {
            try {
               socket.close();
            } catch (IOException ex) {
               Prompt.printError("SocketTask: Error closing socket");
            }
         }
      }
   }
}