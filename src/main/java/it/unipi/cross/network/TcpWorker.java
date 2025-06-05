package it.unipi.cross.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;
import it.unipi.cross.server.OrderBook;
import it.unipi.cross.server.UserBook;
import it.unipi.cross.util.JsonUtil;
import it.unipi.cross.util.MessageResponse;
import it.unipi.cross.util.OrderResponse;
import it.unipi.cross.util.Request;
import it.unipi.cross.util.Response;

public class TcpWorker implements Runnable {
   private Socket socket;
   private OrderBook orderBook;
   private UserBook userBook;
   private String username;

   public TcpWorker(Socket socket, OrderBook orderBook, UserBook userBook) {
      this.socket = socket;
      this.orderBook = orderBook;
      this.userBook = userBook;
      this.username = "";
   }

   @Override
   public void run() {

      // edit
      System.out.println("[TcpWorker] Connected client " + socket);

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

      Map<String, String> values = JsonUtil.convertObjectToStringMap(request.getValues());

      Response response = null;
      int orderId = -2;

      switch (operation) {
         case "register":
            // user must be not logged in from client
            if (values.get("password").isBlank()) {
               response = new MessageResponse(101, "invalid password");
               break;
            }
            response = userBook.register(
                  values.get("username"),
                  values.get("password"));
            break;
         case "updateCredentials":
            if (!username.isEmpty()) {
               response = new MessageResponse(104, "user currently logged in");
               break;
            }
            response = userBook.updateCredentials(
                  values.get("username"),
                  values.get("old_password"),
                  values.get("new_password"));
            break;
         case "login":
            if (username.isEmpty()) {
               response = new MessageResponse(102, "user already logged in");
               break;
            }
            response = userBook.login(
                  values.get("username"),
                  values.get("password"));
            username = values.get("username");
            break;
         case "logout":
            if (!username.isEmpty()) {
               response = new MessageResponse(101, "user not logged in");
               break;
            }
            response = userBook.logout(username);
            username = "";
            break;
         case "insertLimitOrder":
            // user must be already logged in from client
            LimitOrder limit = new LimitOrder(
                  username,
                  Type.valueOf(values.get("type")),
                  Integer.parseInt(values.get("size")),
                  Integer.parseInt(values.get("price")),
                  System.currentTimeMillis());
            response = new OrderResponse(orderBook.insertOrder(limit));
            break;
         case "insertMarketOrder":
            // user must be already logged in from client
            MarketOrder market = new MarketOrder(
                  username,
                  Type.valueOf(values.get("type")),
                  Integer.parseInt(values.get("size")),
                  System.currentTimeMillis());
            response = new OrderResponse(orderBook.insertOrder(market));
            break;
         case "insertStopOrder":
            // user must be already logged in from client
            StopOrder stop = new StopOrder(
                  username,
                  Type.valueOf(values.get("type")),
                  Integer.parseInt(values.get("size")),
                  Integer.parseInt(values.get("price")),
                  System.currentTimeMillis());
            response = new OrderResponse(orderBook.insertOrder(stop));
            break;
         case "cancelOrder":
            // user must be already logged in from client
            response = orderBook.cancelOrder(
                  Integer.parseInt(values.get("orderId")),
                  username);
            break;
         /**
          * case "getPriceHistory":
          * PriceHistory history = new PriceHistory();
          * history.getPriceHistory(values.get("month"));
          **/
      }

      System.out
            .println("[TcpWorker] Received request: " + request.toString() + "\nSent response: " + response.toString());

      return response;
   }
}
