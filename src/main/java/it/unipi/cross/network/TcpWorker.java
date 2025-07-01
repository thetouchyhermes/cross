package it.unipi.cross.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Instant;

import it.unipi.cross.data.LimitOrder;
import it.unipi.cross.data.MarketOrder;
import it.unipi.cross.data.StopOrder;
import it.unipi.cross.data.Type;
import it.unipi.cross.history.PriceHistoryCalculator;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.MessageResponse;
import it.unipi.cross.json.OrderResponse;
import it.unipi.cross.json.PriceHistory;
import it.unipi.cross.json.Request;
import it.unipi.cross.json.Response;
import it.unipi.cross.server.OrderBook;
import it.unipi.cross.server.UserBook;

public class TcpWorker implements Runnable {
   private Socket socket;
   private OrderBook orderBook;
   private UserBook userBook;
   private UdpNotifier udpNotifier;
   private String username;
   private volatile boolean running = false;

   public TcpWorker(Socket socket, OrderBook orderBook, UserBook userBook, UdpNotifier udpNotifier) {
      this.socket = socket;
      this.orderBook = orderBook;
      this.userBook = userBook;
      this.udpNotifier = udpNotifier;
      this.username = "";
   }

   @Override
   public void run() {

      running = true;

      System.out.println("[Server] connected client " + socket.getPort());
      String line = null;

      Request request = null;
      try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
         while (running && !Thread.currentThread().isInterrupted()) {
            line = in.readLine();
            if (line == null || line.isBlank()) {
               break;
            }
            request = JsonUtil.fromJson(line, Request.class);
            Response response = processRequest(request);
            if (!running)
               break;
            if (response != null) {
               out.write(JsonUtil.toJson(response));
               out.newLine();
               out.flush();
            }
            if (!username.isEmpty() && request.getOperation().equals("logout")) {
               running = false;
            }
         }
      } catch (SocketTimeoutException e) {
         System.err.println("[Server] timed out client " + socket.getPort());
      } catch (IOException e) {
         if (request != null && request.getOperation().equals("logout")) {
            System.out.println("[Server] disconnected client " + socket.getPort());
         } else {
            System.out.println("[Server] IOException on " + socket.getPort());
         }
      } catch (Exception e) {
         System.err.println("[Server] " + e.getClass() + ": " + e.getMessage());
         // e.printStackTrace();
      } finally {
         if (!username.isEmpty()) {
            userBook.logout(username);
            // Remove client from UDP notifier
            if (udpNotifier != null) {
               udpNotifier.removeClient(username);
            }
            username = "";
         }
         try {
            if (socket != null && !socket.isClosed()) {
               socket.close();
               System.out.println("[Server] disconnected client " + socket.getPort());
            }
         } catch (IOException e) {
            System.err.println("[Server] error while closing socket of client " + socket.getPort());
         }
      }

   }

   private Response processRequest(Request request) throws IOException, NullPointerException {
      if (request == null)
         return null;

      String operation = request.getOperation();
      if (operation == null || operation.isEmpty())
         return null;

      Response response = null;

      switch (operation) {
         case "register":
            if (!username.isEmpty()) {
               response = new MessageResponse(103, "user currently logged in");
               break;
            }
            if (request.getAsString("password").isBlank()) {
               response = new MessageResponse(101, "invalid password");
               break;
            }
            response = userBook.register(
                  request.getAsString("username"),
                  request.getAsString("password"));
            break;
         case "updateCredentials":
            if (!username.isEmpty()) {
               response = new MessageResponse(104, "user currently logged in");
               break;
            }
            response = userBook.updateCredentials(
                  request.getAsString("username"),
                  request.getAsString("old_password"),
                  request.getAsString("new_password"));
            break;
         case "login":
            if (!username.isEmpty()) {
               response = new MessageResponse(102, "user already logged in");
               break;
            }
            MessageResponse messageResponse = userBook.login(
                  request.getAsString("username"),
                  request.getAsString("password"));

            if (messageResponse.getResponse() == 100) {
               username = request.getAsString("username");
               // Automatically register client for UDP notifications if udpPort is provided
               try {
                  Integer udpPort = request.getAsInteger("udpPort");
                  if (udpPort != null && udpNotifier != null) {
                        udpNotifier.addClient(username, udpPort);
                        System.out.println("[Server] Registered UDP notifications for " + username + " at port " + udpPort);
                  } else  {
                     System.out.println("[Server] No port found at login");
                  }
               } catch (Exception e) {
                  System.err.println("[Server] Warning: Could not register UDP for " + username + 
                                   ": " + e.getMessage());
                  // Don't fail login if UDP registration fails
               }
            }

            response = messageResponse;
            break;
         case "logout":
            if (username.isEmpty()) {
               response = new MessageResponse(101, "user not logged in");
               break;
            }
            response = userBook.logout(username);
            // Remove client from UDP notifier
            if (udpNotifier != null) {
               udpNotifier.removeClient(username);
            }
            username = "";
            running = false;
            break;
         case "insertLimitOrder":
            if (username.isEmpty()) {
               response = new OrderResponse(-1);
               break;
            }
            try {
               LimitOrder limit = new LimitOrder(
                     username,
                     Type.valueOf(request.getAsString("type")),
                     request.getAsInteger("size"),
                     request.getAsInteger("price"),
                     Instant.now().getEpochSecond());
               response = new OrderResponse(orderBook.insertOrder(limit));
            } catch (Exception e) {
               System.err.println(e.getClass() + ": " + e.getMessage());
               response = new OrderResponse(-1);
            }
            break;
         case "insertMarketOrder":
            if (username.isEmpty()) {
               response = new OrderResponse(-1);
               break;
            }
            try {
               MarketOrder market = new MarketOrder(
                     username,
                     Type.valueOf(request.getAsString("type")),
                     request.getAsInteger(
                           "size"),
                     Instant.now().getEpochSecond());
               response = new OrderResponse(orderBook.insertOrder(market));
            } catch (Exception e) {
               System.err.println(e.getClass() + ": " + e.getMessage());
               response = new OrderResponse(-1);
            }
            break;
         case "insertStopOrder":
            if (username.isEmpty()) {
               response = new OrderResponse(-1);
               break;
            }
            try {
               StopOrder stop = new StopOrder(
                     username,
                     Type.valueOf(request.getAsString("type")),
                     request.getAsInteger(
                           "size"),
                     request.getAsInteger(
                           "price"),
                     Instant.now().getEpochSecond());
               response = new OrderResponse(orderBook.insertOrder(stop));
            } catch (Exception e) {
               System.err.println(e.getClass() + ": " + e.getMessage());
               response = new OrderResponse(-1);
            }
            break;
         case "cancelOrder":
            if (username.isEmpty()) {
               response = new MessageResponse(101, "user not logged in");
            }
            try {
               response = orderBook.cancelOrder(
                     request.getAsInteger("orderId"),
                     username);
            } catch (Exception e) {
               System.err.println(e.getClass() + ": " + e.getMessage());
               response = new OrderResponse(-1);
            }
            break;
         case "getPriceHistory":
            PriceHistoryCalculator history = new PriceHistoryCalculator();
            PriceHistory priceHistory = history.getPriceHistory(request.getAsString("month"));
            response = priceHistory;
            break;
         case "exit":
            if (!username.isEmpty()) {
               userBook.logout(username);
               // Remove client from UDP notifier
               if (udpNotifier != null) {
                  udpNotifier.removeClient(username);
               }
            }
            response = new MessageResponse(0, operation);
            running = false;
            return response;
      }

      // test
      System.out.println("[Server] request from " + socket.getPort() + ":\n" + request.toString());
      System.out.println("[Server] response to " + socket.getPort() + ":\n" + response.toString());

      return response;
   }
}