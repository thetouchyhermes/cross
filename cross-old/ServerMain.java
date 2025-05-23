package cross;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fusesource.jansi.AnsiConsole;

import cross.sockets.SocketTask;
import cross.sockets.TCPServer;

public class ServerMain {

   private static TCPServer tcpServer = new TCPServer();

   public static void main(String[] args) {

      // Enable Jansi
      AnsiConsole.systemInstall();

      // Handler function for normal termination, exception and anomalous interruption
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         
         // Disable Jansi
            AnsiConsole.systemUninstall();

         // Close server connection
         if (tcpServer.isAlive()) {
            tcpServer.stop();
         }

         // Aggiungi qui il codice che vuoi eseguire quando il programma viene interrotto
         // Ad esempio, chiudere risorse, salvare lo stato, ecc.
         
      }));

      int NUM_THREADS = Runtime.getRuntime().availableProcessors();
      // fixed?
      ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);

      System.out.println("ServerMain: Server is running...");

      try {
         while (tcpServer.isAlive()) {
            pool.execute(new SocketTask(tcpServer.listen(), tcpServer));
         }
         //close server and threads
      } catch (NoSuchElementException e) {
         Prompt.printError(Prompt.RED + "^C" + Prompt.RESET);
         Prompt.exit(1);
      }
      
   }
   
}
