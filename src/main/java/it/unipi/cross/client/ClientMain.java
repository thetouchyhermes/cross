package it.unipi.cross.client;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.json.MessageResponse;
import it.unipi.cross.json.OrderResponse;
import it.unipi.cross.json.Request;
import it.unipi.cross.json.Response;
import it.unipi.cross.network.TcpClient;
import it.unipi.cross.network.UdpListener;

public class ClientMain {

   private static TcpClient tcpClient;
   private static UdpListener udpListener;
   private static String username = "";
   private static volatile boolean running = true;

   public static void main(String[] args) {

      // Enable Jansi
      AnsiConsole.systemInstall();

      // Start client app
      Prompt.printStart();
      
      // Handler function for normal termination, exception and anomalous interruption
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      
         if (tcpClient != null && tcpClient.isAlive()) {
            Request logout = new Request();
            logout.setOperation("logout");
            try {
               tcpClient.sendRequest(logout);
            } catch (IOException e) {
               // Server connection lost, ignore
            }

            tcpClient.close();
         }

         if (udpListener != null) {
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

      String udpAddress = config.getString("udp.address");
      int udpPort = config.getInt("udp.port");
      String tcpAddress = config.getString("tcp.address");
      int tcpPort = config.getInt("tcp.port");

      // create and start UDP listener
      udpListener = new UdpListener(udpAddress, udpPort);
      Thread udpThread = new Thread(udpListener);
      udpThread.start();

      // Create and start TCP connection
      tcpClient = new TcpClient(tcpAddress, tcpPort);

      try{
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
               System.out.println("command format not valid");
               continue;
            }
            String command = line.split("\\(")[0].trim();

            if (command == null || command.isEmpty()) {
               Prompt.printError("command is empty");
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
                  System.out.println("command not defined");
                  continue;
            }

            Request request = RequestFactory.create(line);

            if (request != null) {
               String operation = request.getOperation();
               Map<String, Object> values = request.getValues();
               switch (operation) {
                  case "notDefined":
                     System.out.println("command not defined for this number of args");
                     Prompt.printHelp(command);
                     continue;
                  case "invalidArgs":
                     System.out.println("args not valid");
                     Prompt.printHelp(command);
                     continue;
                  case "help":
                     tcpClient.keepServerAlive();
                     if (values == null || values.size() == 0) {
                        Prompt.printHelp();
                        continue;
                     }
                     String com = values.get("command").toString();
                     if (com != null)
                        Prompt.printHelp(com);
                     continue;
                  default:
               }

               System.out.println(request.toString());
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
                              if (username.isEmpty()) {
                                 if (values.get("username") != null) {
                                    username = values.get("username").toString();
                                 }
                              } else {
                                 Prompt.printError("user was already logged before login");
                                 System.exit(1);
                              }
                              break;
                           case "logout":
                              if (!username.isEmpty()) {
                                 username = "";
                                 System.exit(0);
                              } else {
                                 Prompt.printError("user was not logged before logout");
                                 System.exit(1);
                              }
                        }
                     default:
                        System.out.println(response.toString());
                        break;
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
                           System.out.println("user not logged in");
                           break;
                        }
                        System.out.println("order failed or discarded:\n" + response.toString());
                        break;
                     default:
                        System.out.println("order placed correctly:\n" + response.toString());
                        break;
                  }
               }
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
