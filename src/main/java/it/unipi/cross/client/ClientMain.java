package it.unipi.cross.client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.json.MessageResponse;
import it.unipi.cross.json.OrderResponse;
import it.unipi.cross.json.PriceHistory;
import it.unipi.cross.json.Request;
import it.unipi.cross.json.Response;
import it.unipi.cross.network.TcpClient;
import it.unipi.cross.network.UdpListener;

public class ClientMain {

   private static TcpClient tcpClient;
   private static UdpListener udpListener;
   private static boolean udpStarted = false;
   private static String username = "";
   private static volatile boolean running = true;

   public static void main(String[] args) {

      // Enable Jansi
      AnsiConsole.systemInstall();

      // Start client app
      Prompt.printStart();

      // Handler function for normal termination, exception and anomalous interruption
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {

         running = false;

         if (tcpClient != null && tcpClient.isAlive()) {
            Request logout = new Request();
            logout.setOperation("logout");
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("stopped", 1);
            logout.setValues(values);
            try {
               tcpClient.sendRequest(logout);
            } catch (IOException e) {
               // server killed
               Prompt.printError("[ClientMain] server didn't close this socket");
            }

            tcpClient.close();
         }

         if (udpStarted && udpListener != null) {
            udpListener.shutdown();
         }

         Prompt.printEnd();

         // Disable Jansi
         AnsiConsole.systemUninstall();
      }));

      // load configuration
      ConfigReader config = new ConfigReader();
      try {
         config.loadClient();
      } catch (IOException e) {
         Prompt.printError("[ClientMain] " + e.getMessage());
         System.exit(1);
      }

      // String udpAddress = config.getString("udp.address");
      int udpPort = config.getInt("udp.port");
      String tcpAddress = config.getString("tcp.address");
      int tcpPort = config.getInt("tcp.port");

      // Create and start TCP connection
      tcpClient = new TcpClient(tcpAddress, tcpPort);

      try {
         tcpClient.connect();
      } catch (IOException e) {
         Prompt.printError("[Client] server not available");
         System.exit(1);
      }

      try (Scanner scanner = new Scanner(System.in)) {

         while (running) {

            Prompt.newLine(username);
            String line = "";
            line = scanner.nextLine();
            if (!running)
               break;
            if (line.isEmpty() || !line.contains("(") || !line.contains(")")) {
               System.out.println("Command format not valid");
               continue;
            }
            String command = line.split("\\(")[0].trim();

            if (command == null || command.isEmpty()) {
               Prompt.printError("Command is empty");
               continue;
            }
            switch (command) {
               case "register":
               case "insertLimitOrder":
               case "insertMarketOrder":
               case "insertStopOrder":
               case "cancelOrder":
               case "updateCredentials":
               case "login":
               case "logout":
               case "getPriceHistory":
               case "help":
               case "exit":
                  break;
               default:
                  System.out.println("Command not defined");
                  continue;
            }

            Request request = RequestFactory.create(line);

            if (request != null) {
               String operation = request.getOperation();
               Map<String, Object> values = request.getValues();
               switch (operation) {
                  case "notDefined":
                     System.out.println("Command not defined for this number of args");
                     break;
                  case "invalidArgs":
                     System.out.println("Args not valid");
                     break;
                  case "help":
                     tcpClient.keepServerAlive();
                     if (values == null || values.size() == 0) {
                        Prompt.printHelp();
                        continue;
                     }
                     String com = values.get("command").toString();
                     if (com != null)
                        command = com;
               }

               switch (operation) {
                  case "notDefined":
                  case "invalidArgs":
                  case "help":
                     Prompt.printHelp(command);
                     continue;
                  case "login":
                     if (!udpStarted) {
                        // create and start UDP listener
                        // udpListener = new UdpListener(udpAddress, udpPort, username);
                        udpListener = new UdpListener(udpPort);
                        Thread udpThread = new Thread(udpListener);
                        udpThread.start();
                        udpStarted = true;

                        // send udpPort
                        udpPort = udpListener.getPort();
                        values.put("udpPort", udpPort);
                     }
                     request.setValues(values);
               }

               // Debug:
               // System.out.println(request.toString());
               tcpClient.sendRequest(request);
               Response response = tcpClient.receiveResponse();

               if (response == null || operation.equals("exit"))
                  System.exit(0);

               if (response instanceof MessageResponse) {
                  MessageResponse messageResponse = (MessageResponse) response;

                  int code = messageResponse.getResponse();
                  String message = messageResponse.getErrorMessage();
                  switch (code) {
                     case 0:
                        Prompt.printError("[ClientMain] error on response received");
                        System.exit(1);
                     case 110:
                        System.out.println(message);
                        continue;
                     case 100:
                        switch (operation) {
                           case "login":
                              if (values.get("username") != null) {
                                 username = values.get("username").toString();
                              }
                              break;
                           case "logout":
                              username = "";
                              System.exit(0);
                        }
                  }
               } else if (response instanceof OrderResponse) {
                  OrderResponse orderResponse = (OrderResponse) response;

                  int orderId = orderResponse.getOrderId();
                  switch (orderId) {
                     case 0:
                        Prompt.printError("[ClientMain] error on response received");
                        System.exit(1);
                     case -1:
                        if (username.isEmpty()) {
                           System.out.println("User not logged in");
                           continue;
                        }
                        System.out.println("Order failed or discarded:");
                        break;
                     default:
                        System.out.println("Order placed correctly:");
                  }
               } else if (response instanceof PriceHistory) {
                  PriceHistory priceHistory = (PriceHistory) response;
                  if (priceHistory.getDailyStats() == null || priceHistory.getDailyStats().isEmpty()) {
                     System.out.println("No entries of this month are available");
                  } else {
                     priceHistory.printDailyStats();
                  }
                  continue;
               }
               System.out.println(response.toString());
               System.out.flush();
            }
         }
      } catch (IOException e) {
         Prompt.printError(e.getClass() + ": " + e.getMessage());
         System.exit(1);
      } catch (NoSuchElementException e) {
         Prompt.printError("^C");
         System.exit(2);
      }

      Prompt.printEnd();
   }

}
