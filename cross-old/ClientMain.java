package cross;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.fusesource.jansi.AnsiConsole;

import cross.requests.Request;
import cross.requests.RequestFactory;
import cross.requests.Response;
import cross.requests.ResponseManager;
import cross.sockets.TCPClient;
import cross.values.CredentialsValues;

public class ClientMain {

   private static volatile boolean running = true;

   public static void main(String[] args) {
      
      // Enable Jansi
      AnsiConsole.systemInstall();

      // Start client app
      Prompt.printStart();

      String user = "";
      boolean isRegistered = false;
      boolean error = false;

      // Create and start TCP connection
      TCPClient tcpClient = new TCPClient();
      
      Prompt.printHelp(false, false);

      // Handler function for normal termination, exception and anomalous interruption
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         
         running = false;

         switch (Prompt.getExitStatus()) {
            case 0:
               break;
            case 1:
               break;
            case 2:
               Prompt.printError("^C");
               break;
            default:
         }

         // Close server connection
         if (tcpClient != null && tcpClient.isAlive()) {
            tcpClient.stop();
         }

         
         // Aggiungi qui il codice che vuoi eseguire quando il programma viene interrotto
         // Ad esempio, chiudere risorse, salvare lo stato, ecc.

         Prompt.printEnd();
         
         // Disable Jansi
         AnsiConsole.systemUninstall();
      }));

      try (Scanner scanner = new Scanner(System.in)) {

         while (running) {

            Prompt.newLine(user);
            String command = scanner.nextLine();
            String[] cmd = command.split(" ");

            error = false;
            Request request = null;

            if (command == null || command.equals("")) {
               Prompt.printError("ClientMain: Command is not defined for this input");
            } else {
               switch (cmd[0].toLowerCase()) {
                  case "help":
                     keepServerAlive();
                     Prompt.printHelp(isRegistered, !user.equals(""));
                     break;
                  case "exit":
                     Prompt.exit(0);
                     break;
                  default:
                     if (!isRegistered) {
                        Prompt.printError("ClientMain: Command not accepted, missing connection");
                        error = true;
                     } else {
                        request = RequestFactory.create(command, user);
                     }
               }

               Response response = null;
               if (request != null && !error) {

                  // manage register

                  response = tcpClient.send(request);
                  boolean success = ResponseManager.handle(request, response);
                  if (success) {
                     switch (request.getOperation()) {
                        case "register":
                           if (!isRegistered)
                              isRegistered = true;
                           break;
                        case "login":
                           if (user.equals("")) {
                              CredentialsValues v = (CredentialsValues) request.getValues();
                              user = v.getUsername();
                           } else {
                              Prompt.printError("ClientMain: User " + user + " was already logged before login");
                              Prompt.exit(1);
                           }
                           break;
                        case "logout":
                           if (!user.equals(""))
                              user = "";
                           else {
                              Prompt.printError("ClientMain: User was not logged before logout");
                              Prompt.exit(1);
                           }
                           break;
                        // default:
                     }
                  } else {
                     // to delete
                     Prompt.printError("ClientMain: Request not successful, error message expected before this");
                  }
               } else {
                  if (isRegistered)
                     keepServerAlive();
                  System.out.println(command);
               }
            }
         }
      } catch (NoSuchElementException e) {
         Prompt.printError(e.getClass() + ": " + e.getMessage());
         if (!running) {
            Prompt.printError("exception but not running");
            Prompt.exit(2);
         } else {
            Prompt.printError("exception but running");
            Prompt.exit(1);
         }
      }

      // CLIManager.selectOperation();
      // System.out.println("Client Host: " + config.getProperty("client.host"));
      // System.out.println("Client Port: " + config.getProperty("client.port"));

      // Get current timestamp:
      // Instant.now().getEpochSecond();

      Prompt.printEnd();
   }

   public static void keepServerAlive() {
      
   }
}
