package it.unipi.cross.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import it.unipi.cross.server.OrderBook;
import it.unipi.cross.server.UserBook;
import it.unipi.cross.util.JsonUtil;
import it.unipi.cross.util.Request;
import it.unipi.cross.util.Response;

public class TcpWorker implements Runnable {
   private Socket socket;
   private OrderBook orderBook;
   private UserBook userBook;

   public TcpWorker(Socket socket, OrderBook orderBook, UserBook userBook) {
      this.socket = socket;
      this.orderBook = orderBook;
      this.userBook = userBook;
   }

   @Override
   public void run() {

      // edit
      System.out.println("SocketTask: Connected client " + socket);

      try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
         String line;
         while ((line = in.readLine()) != null) {
            Request request = JsonUtil.fromJson(line, Request.class);
            Response response = processRequest(request);
            out.write(JsonUtil.toJson(response));
            out.newLine();
            out.flush();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private Response processRequest(Request request) {
      // Dispatch to OrderBook, UserBook, etc.
      // Return Response object as per util.Response
      // ...

      String operation = request.getOperation();
      if (operation.isEmpty())
         return null;
      
      switch(operation) {
         case "register":
         case "login":
         case "updateCredentials":
         case "cancelOrder":
         case "insertLimitOrder":
         case "insertStopOrder":
         case "insertMarketOrder":
         case "getPriceHistory":
         
      }
      return new Response(/* ... */);
   }
}
