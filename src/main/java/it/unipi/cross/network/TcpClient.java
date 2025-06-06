package it.unipi.cross.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.MessageResponse;
import it.unipi.cross.json.OrderResponse;
import it.unipi.cross.json.Request;
import it.unipi.cross.json.Response;

public class TcpClient implements Closeable {
   private final String serverAddress;
   private final int serverPort;
   private Socket socket;
   private BufferedReader in;
   private BufferedWriter out;

   private Thread receiverThread;
   private Response receivedResponse;
   private final Object obj = new Object();
   private volatile boolean serverRunning = false;
   private volatile boolean running = false;

   public TcpClient(String serverAddress, int serverPort) {
      this.serverAddress = serverAddress;
      this.serverPort = serverPort;
   }

   /**
    * Establishes a TCP connection to the server using the specified address and
    * port.
    * If already connected, returns immediately.
    * 
    * @throws IOException if an I/O error occurs when creating the socket or
    *                     streams.
    */
   public void connect() throws IOException {
      if (isAlive())
         return;

      socket = new Socket();
      socket.connect(new InetSocketAddress(serverAddress, serverPort));
      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      if (!isAlive()) {
         close();
         throw new IOException("Socket not alive at start");
      }

      serverRunning = true;
      running = true;
      startReceiverThread();

      System.out.println("[Client " + socket.getLocalPort() + "] connected to server");
   }

   private void startReceiverThread() {
      receiverThread = new Thread(() -> {
         try {
            String line;
            while (running && !Thread.currentThread().isInterrupted()) {
               line = in.readLine();

               Response response = stringToResponse(line);
               if (response != null && response instanceof MessageResponse) {
                  MessageResponse msg = (MessageResponse) response;
                  if (msg.getResponse() == 500) {
                     serverRunning = false;
                     System.out.println("[Client " + socket.getLocalPort() + "] server closed the connection");
                     running = false;
                     receivedResponse = null;
                     System.exit(1);
                  }
               }
               if (line == null) {
                  System.out.println("[Client " + socket.getLocalPort() + "] disconnected from server");
                  running = false;
                  receivedResponse = null;
                  System.exit(1);
               }

               synchronized (obj) {
                  receivedResponse = response;
                  obj.notifyAll();
               }
            }
         } catch (IOException e) {
            if (running) {
               System.err.println("[TcpClient] Receiver thread error: " + e.getMessage());
            }
            System.exit(1);
         }
      });

      receiverThread.setDaemon(true);
      receiverThread.start();
   }

   private Response stringToResponse(String line) {

      // try to convert to a MessageResponse
      MessageResponse messageResponse = JsonUtil.fromJson(line, MessageResponse.class);
      if (messageResponse != null && messageResponse.getErrorMessage() != null) {
         return messageResponse;
      }

      // fallback to OrderResponse
      OrderResponse orderResponse = JsonUtil.fromJson(line, OrderResponse.class);
      if (orderResponse != null && orderResponse.getOrderId() != 0) {
         return orderResponse;
      }

      return null;
   }

   /**
    * Sends a request to the server and waits for a response.
    * 
    * @param request the {@link Request} object to send to the server
    * @return a {@link Response} or {@code null} if the client is not alive or the
    *         response cannot be parsed
    * @throws IOException if an I/O error occurs
    */
   public Response sendRequest(Request request) throws IOException {
      if (!isAlive())
         return null;
      if (request == null || request.getOperation() == null)
         return null;

      synchronized (obj) {
         receivedResponse = null;
         out.write(JsonUtil.toJson(request));
         out.newLine();
         out.flush();

         // Wait for response with timeout (optional: adjust as needed)
         while (receivedResponse == null && isAlive()) {
            try {
               obj.wait();
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
               throw new IOException("Interrupted while waiting for response", e);
            }
         }
         return receivedResponse;
      }
   }

   @Override
   public void close() {
      running = false;
      if (receiverThread != null) {
         receiverThread.interrupt();
      }
      try {
         if (socket != null && !socket.isClosed()) {
            socket.close();
         }
      } catch (IOException ignored) {
      }
      try {
         if (in != null) {
            in.close();
         }
      } catch (IOException ignored) {
      }
      try {
         if (out != null) {
            out.close();
         }
      } catch (IOException ignored) {
      }
      in = null;
      out = null;
      socket = null;
   }

   /**
    * Checks if the underlying socket is alive.
    * 
    * @return {@code true} if the socket is alive; {@code false} otherwise.
    */
   public boolean isAlive() {
      return socket != null && socket.isConnected() && !socket.isClosed();
   }

   public boolean isServerAlive() {
      return serverRunning;
   }

   /**
    * Sends an empty request to the server to keep the connection alive and reset
    * the server's timeout.
    * 
    * @throws IOException if an I/O error occurs while sending the request.
    */
   public void keepServerAlive() throws IOException {
      sendRequest(new Request());
   }
}