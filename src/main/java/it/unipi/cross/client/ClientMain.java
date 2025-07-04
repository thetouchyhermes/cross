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

/** Main class for the client-side of CROSS **/
public class ClientMain {

   private static TcpClient tcpClient;
   private static UdpListener udpListener;
   private static boolean udpStarted = false;

   // to keep track of login state
   private static String username = "";

   // to keep track of client status
   private static volatile boolean running = false;

   public static void main(String[] args) {

      // enable Jansi
      AnsiConsole.systemInstall();

      // start client app
      Prompt.printStart();

      // handler function for client termination
      // called by System.exit(), exceptions or anomalous events
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {

         running = false;

         if (tcpClient != null && tcpClient.isAlive()) {

            // signal to server client disconnection as a special logout message
            Request logout = new Request();
            logout.setOperation("logout");
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("stopped", 1);
            logout.setValues(values);
            try {
               tcpClient.sendRequest(logout);
            } catch (IOException e) {
               // server killed
               // should not be reached
               Prompt.printError("[ClientMain] server didn't close this socket");
            }

            tcpClient.close();
         }

         if (udpStarted && udpListener != null) {
            udpListener.shutdown();
         }

         // print ending message
         Prompt.printEnd();

         // disable Jansi
         AnsiConsole.systemUninstall();
      }));

      // load configuration file
      ConfigReader config = new ConfigReader();
      try {
         config.loadClient();
      } catch (IOException e) {
         Prompt.printError("[ClientMain] " + e.getMessage());
         System.exit(1);
      }

      // load parameters from config file
      int udpPort = config.getInt("udp.port");
      String tcpAddress = config.getString("tcp.address");
      int tcpPort = config.getInt("tcp.port");

      // create and start TCP connection
      tcpClient = new TcpClient(tcpAddress, tcpPort);
      try {
         tcpClient.connect();
      } catch (IOException e) {
         Prompt.printError("[Client] server not available");
         System.exit(1);
      }

      try (Scanner scanner = new Scanner(System.in)) {

         running = true;
         while (running) {

            tcpClient.keepServerAlive();

            // print prompt line header
            Prompt.newLine(username);

            String line = scanner.nextLine();

            if (!running) {
               // client disconnected
               break;
            }

            if (line.isEmpty() || !line.contains("(") || !line.contains(")")) {
               System.out.println("Command format not valid");
               continue;
            }

            // get command string without params
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

            // delegate request creation
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
                        // print standard full help page
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
                     // print help page for specific command
                     Prompt.printHelp(command);
                     continue;

                  case "login":
                     if (username.isEmpty() && !udpStarted) {
                        // create and start UDP listener only at login request
                        udpListener = new UdpListener(udpPort, request.getAsString("username"), tcpClient);
                        Thread udpThread = new Thread(udpListener);
                        udpThread.start();
                        udpStarted = true;

                        // udp needed before response reception to be able to send correct port to
                        // server
                        udpPort = udpListener.getPort();
                        values.put("udpPort", udpPort);
                     }
                     request.setValues(values);
               }

               // Debug:
               // System.out.println(request.toString());

               // send request to server
               tcpClient.sendRequest(request);
               // receive response from server
               Response response = tcpClient.receiveResponse();

               // client stopped if no response or 'exit' request
               if (response == null || operation.equals("exit"))
                  System.exit(0);

               if (response instanceof MessageResponse) {
                  // tries parsing to MessageResponse

                  MessageResponse messageResponse = (MessageResponse) response;

                  int code = messageResponse.getResponse();
                  switch (code) {
                     case 100:
                        switch (operation) {
                           case "login":
                              if (values.get("username") != null) {
                                 // update username variable after successful login
                                 username = values.get("username").toString();
                              }
                              if (udpStarted) {
                                 // signal to udp that login was successful
                                 udpListener.isLogged();
                              }
                              break;
                           case "logout":
                              // update username variable after successful logout
                              username = "";
                              // ends program
                              System.exit(0);
                        }
                        break;
                     default:
                        switch (operation) {
                           case "login":
                              // signals to udp that login was not successful
                              udpListener.shutdown();
                              udpStarted = false;
                              break;
                           case "cancelOrder":
                              // only logged users can cancel orders
                              if (username.isEmpty()) {
                                 System.out.println("User not logged in");
                                 continue;
                              }
                        }
                  }
               } else if (response instanceof OrderResponse) {
                  // tries parsing to OrderResponse
                  OrderResponse orderResponse = (OrderResponse) response;

                  int orderId = orderResponse.getOrderId();
                  switch (orderId) {
                     case 0:
                        // should not be reached
                        Prompt.printError("[ClientMain] error on response received");
                        System.exit(1);
                     case -1:
                        // ignore if not logged in
                        if (username.isEmpty()) {
                           System.out.println("User not logged in");
                           continue;
                        }

                        // user logged in, failed order
                        System.out.println("Order failed or discarded:");
                        break;
                     default:
                        // order was successful
                        System.out.println("Order placed correctly:");

                        if (operation.equals("insertStopOrder")) {
                           // increment counter of pending stop orders
                           tcpClient.setPinCount();

                        }
                  }
               } else if (response instanceof PriceHistory) {
                  // tries parsing to PriceHistory
                  PriceHistory priceHistory = (PriceHistory) response;

                  if (priceHistory.getDailyStats() == null || priceHistory.getDailyStats().isEmpty()) {
                     // no trades were made in the given month
                     System.out.println("No entries of this month are available");
                  } else {
                     // prints table of stats
                     priceHistory.printDailyStats();
                  }
                  continue;
               }

               // prints response received directly to CLI
               System.out.println(response.toString());
               System.out.flush();
            }
         }
      } catch (IOException e) {
         Prompt.printError(e.getClass() + ": " + e.getMessage());
         System.exit(1);
      } catch (NoSuchElementException e) {
         // user probably interrupted on CLI using Ctrl+C
         Prompt.printError("^C");
         System.exit(1);
      }

      // print end message
      Prompt.printEnd();
   }

}
