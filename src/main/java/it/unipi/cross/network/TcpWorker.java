package it.unipi.cross.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;
import it.unipi.cross.history.PriceHistory;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.MessageResponse;
import it.unipi.cross.json.OrderResponse;
import it.unipi.cross.json.Request;
import it.unipi.cross.json.Response;
import it.unipi.cross.server.OrderBook;
import it.unipi.cross.server.UserBook;

public class TcpWorker implements Runnable {
   private Socket socket;
   private OrderBook orderBook;
   private UserBook userBook;
   private String username;
   private volatile boolean running = false;

   public TcpWorker(Socket socket, OrderBook orderBook, UserBook userBook) {
      this.socket = socket;
      this.orderBook = orderBook;
      this.userBook = userBook;
      this.username = "";
   }

   @Override
   public void run() {

      running = true;

      System.out.println("[Server] connected client " + socket.getPort());
      String line = null;

      try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
         while (running && !Thread.currentThread().isInterrupted()) {
            line = in.readLine();

            Request request = JsonUtil.fromJson(line, Request.class);
            Response response = processRequest(request);
            if (!running)
               break;
            if (response != null) {
               out.write(JsonUtil.toJson(response));
               out.newLine();
               out.flush();
            }
            if (!username.isEmpty() && request.getOperation().equals("logout")) {
               break;
            }
         }
         if (!username.isEmpty()) {
            userBook.logout(username);
         }
         if (Thread.currentThread().isInterrupted()) {
            Response error = new MessageResponse(500, "internal server error");
            out.write(JsonUtil.toJson(error));
            out.newLine();
            out.flush();
         }
      } catch (SocketTimeoutException e) {
         System.err.println("[Server] timed out client " + socket.getPort());
      } catch (IOException e) {
         System.err.println("[Server] error on socket of client " + socket.getPort());
      } finally {
         try {
            socket.close();
            System.out.println("[Server] disconnected client " + socket.getPort());
         } catch (IOException e) {
            System.err.println("[Server] error while closing socket of client " + socket.getPort());
         }
      }

   }

   private Response processRequest(Request request) throws IOException {
      if (request == null)
         return null;

      String operation = request.getOperation();
      if (operation == null || operation.isEmpty())
         return null;

      Map<String, String> values = JsonUtil.convertObjectToStringMap(request.getValues());

      Response response = null;

      switch (operation) {
         case "register":
            // user must be not logged in from client
            if (!username.isEmpty()) {
               response = new MessageResponse(103, "user currently logged in");
               break;
            }
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
            if (!username.isEmpty()) {
               System.out.println(username);
               response = new MessageResponse(102, "user already logged in");
               break;
            }
            MessageResponse messageResponse = userBook.login(
                  values.get("username"),
                  values.get("password"));

            if (messageResponse.getResponse() == 100) {
               username = values.get("username");
            }
            
            response = messageResponse;
            break;
         case "logout":
            if (username.isEmpty()) {
               response = new MessageResponse(101, "user not logged in");
               break;
            }
            response = userBook.logout(username);
            username = "";
            running = false;
            break;
         case "insertLimitOrder":
            if (username.isEmpty()) {
               response = new OrderResponse(-1);
               break;
            }
            LimitOrder limit = new LimitOrder(
                  username,
                  Type.valueOf(values.get("type")),
                  Integer.parseInt(values.get("size")),
                  Integer.parseInt(values.get("price")),
                  System.currentTimeMillis());
            response = new OrderResponse(orderBook.insertOrder(limit));
            break;
         case "insertMarketOrder":
            if (username.isEmpty()) {
               response = new OrderResponse(-1);
               break;
            }
           MarketOrder market = new MarketOrder(
                  username,
                  Type.valueOf(values.get("type")),
                  Integer.parseInt(values.get("size")),
                  System.currentTimeMillis());
            response = new OrderResponse(orderBook.insertOrder(market));
            break;
         case "insertStopOrder":
            if (username.isEmpty()) {
               response = new OrderResponse(-1);
               break;
            }
            StopOrder stop = new StopOrder(
                  username,
                  Type.valueOf(values.get("type")),
                  Integer.parseInt(values.get("size")),
                  Integer.parseInt(values.get("price")),
                  System.currentTimeMillis());
            response = new OrderResponse(orderBook.insertOrder(stop));
            break;
         case "cancelOrder":
            if (username.isEmpty()) {
               response = new MessageResponse(101, "user not logged in");
            }
            response = orderBook.cancelOrder(
                  Integer.parseInt(values.get("orderId")),
                  username);
            break;
         case "getPriceHistory":
            PriceHistory history = new PriceHistory();
            // history.getPriceHistory(values.get("month"));
            break;
         case "exit":
            if (!username.isEmpty()) {
               userBook.logout(username);
            }
            response = new MessageResponse(0, operation);
            running = false;
            return response;
      }

      // test
      System.out
            .println("[Server] received request: " + request.toString() + "\nsent response: " + response.toString());

      return response;
   }
}
