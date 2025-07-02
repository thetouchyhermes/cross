package it.unipi.cross.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.gson.JsonSyntaxException;

import it.unipi.cross.client.Prompt;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.MessageResponse;
import it.unipi.cross.json.OrderResponse;
import it.unipi.cross.json.PriceHistory;
import it.unipi.cross.json.Request;
import it.unipi.cross.json.Response;

/** Client class containing all TCP management on client-side **/
public class TcpClient implements Closeable {
   private final String serverAddress;
   private final int serverPort;
   private Socket socket;
   private BufferedReader in;
   private BufferedWriter out;

   // receives asynchronous responses from server
   private Thread receiverThread;

   // stores last response received
   private Response receivedResponse;
   // to signal if receivedResponse is not empty
   private volatile boolean readyResponse = false;

   // socket status
   private volatile boolean running = false;

   // to keep count of pending stop orders
   private volatile int stopPinCount = 0;

   public TcpClient(String serverAddress, int serverPort) {
      this.serverAddress = serverAddress;
      this.serverPort = serverPort;
   }

   /** Establishes tcp connection with server using the given address and port **/
   public void connect() throws IOException {

      // if already connected, returns immediately
      if (isAlive())
         return;

      // creates socket connection and opens I/O channels
      socket = new Socket();
      socket.connect(new InetSocketAddress(serverAddress, serverPort));
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      if (!isAlive()) {
         // should not be reached
         close();
         throw new IOException("Socket not alive at start");
      }

      // tcp is connected and working
      running = true;

      // starts asynchronous response reception
      startReceiverThread();

      System.out.println("[Client " + socket.getLocalPort() + "] connected to server");
   }

   /** Starts asynchronous non-daemon thread to receive responses from the server **/
   private void startReceiverThread() {
      receiverThread = new Thread(() -> {
         try {
            String line;
            // works while tcp is connected
            while (running && !Thread.currentThread().isInterrupted()) {
               line = in.readLine();

               if (line == null) {
                  // most important part of the thread:
                  // if server is interrupted (Ctrl+C) or timed out client
                  // then thread starts client shutdown
                  Prompt.printError("[Client " + socket.getLocalPort() + "] disconnected from server");
                  System.exit(1);
               }

               // parsing read line into a response and signals
               // does not use a delegated class because of simplicity
               receivedResponse = stringToResponse(line);
               readyResponse = true;
            }
         } catch (IOException e) {
            if (running) {
               // server process was killed
               Prompt.printError("[Client] disconnected from server");
            } else {
               // should not be reached
               e.printStackTrace();
            }
            System.exit(1);
         }
      });
      receiverThread.start();
   }

   /** Parses input line into a Response object or returns null*/
   private Response stringToResponse(String line) {

      // try to convert to a MessageResponse
      try {
         MessageResponse messageResponse = JsonUtil.fromJson(line, MessageResponse.class);
         if (messageResponse != null && messageResponse.getErrorMessage() != null) {
            return messageResponse;
         }
      } catch (JsonSyntaxException e) {
         // not of format MessageResponse
      }

      // fallback to OrderResponse
      try {
         OrderResponse orderResponse = JsonUtil.fromJson(line, OrderResponse.class);
         if (orderResponse != null && orderResponse.getOrderId() != 0) {
            return orderResponse;
         }
      } catch (JsonSyntaxException e) {
         // not of format OrderResponse
      }

      // fallback to PriceHistory
      try {
         PriceHistory priceHistory = JsonUtil.fromJson(line, PriceHistory.class);
         if (priceHistory != null && priceHistory.getDailyStats() != null) {
            return priceHistory;
         }
      } catch (JsonSyntaxException e) {
         // not of format PriceHistory
      }

      return null;
   }

   /** Sends formatted Request to server through output buffer */
   public void sendRequest(Request request) throws IOException {

      while (readyResponse && isAlive()) {
         // wait for the previous Response to be processed
      }

      if (!isAlive()) {
         // socket closed
         throw new IOException("Socket is not alive");
      }
      if (request == null) {
         // should not be reached
         throw new IllegalArgumentException("Request is null");
      }

      // deletes previous response
      receivedResponse = null;

      // writes Request to buffer
      out.write(JsonUtil.toJson(request));
      out.newLine();
      out.flush();
   }

   /** Reads for a new Response sent from server or null if tcp was stopped **/
   public Response receiveResponse() throws IOException {

      while (!readyResponse) {
         // wait for a response to be received from server
      }

      Response response = null;

      // if tcp is still connected, save received response
      if (running) {
         response = receivedResponse;
      }
      // response was saved, variable can now be reused
      readyResponse = false;

      return response;
   }

   /** Close tcp connection and shutdown response variables **/
   @Override
   public void close() {
      running = false;
      readyResponse = true;
      receivedResponse = null;

      // interrupts waiting thread
      if (receiverThread != null && !receiverThread.isAlive()) {
         receiverThread.interrupt();
      }

      // try and close resources ignoring exceptions
      try {
         if (socket != null && !socket.isClosed()) {
            socket.close();
         }
      } catch (IOException ignored) {}

      try {
         if (in != null) {
            in.close();
         }
      } catch (IOException ignored) {}

      try {
         if (out != null) {
            out.close();
         }
      } catch (IOException ignored) {}
      
      in = null;
      out = null;
      socket = null;
   }

   /** Check if socket is connected and not closed **/
   public boolean isAlive() {
      return socket != null && socket.isConnected() && !socket.isClosed();
   }

   /** Sends an empty request to server to reset client's timeout **/
   public void keepServerAlive() throws IOException {
      sendRequest(new Request());
   }

   /** Increment pending stop orders pin count, start keepAlive thread if it was not started before **/
   public synchronized void setPinCount() {
      stopPinCount++;
      // Debug:
      // System.out.println("stopPinCount = " + stopPinCount);

      // start asynchronous thread if the pin count was 0
      if (stopPinCount == 1) {
         Thread keepAliveThread = new Thread(() -> {
            // if socket is alive and there are pending stop orders
            while (isAlive() && stopPinCount > 0) {
               try {
                  keepServerAlive();
                  // wait 5 seconds (server timeout must be greater)
                  Thread.sleep(5000);
               } catch (Exception e) {
                  break;
               }
            }
         });

         // don't wait for thread at termination
         keepAliveThread.setDaemon(true);
         keepAliveThread.start();
      }
   }

   /** Decrements pending stop orders pin count after a stop order finalizes */
   public synchronized void unsetPinCount() {
      if (stopPinCount > 0) {
         stopPinCount--;
      }

      // Debug:
      // System.out.println("stopPinCount = " + stopPinCount);
   }

}