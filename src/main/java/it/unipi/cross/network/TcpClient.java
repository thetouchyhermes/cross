package it.unipi.cross.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.MessageResponse;
import it.unipi.cross.json.OrderResponse;
import it.unipi.cross.json.Request;
import it.unipi.cross.json.Response;

public class TcpClient implements Closeable {
   private final String serverAddress;
   private final int serverPort;
   private Socket socket;

   private final int timeout;

   private BufferedReader in;
   private BufferedWriter out;

   public TcpClient(String serverAddress, int serverPort, int timeout) {
      this.serverAddress = serverAddress;
      this.serverPort = serverPort;
      this.timeout = timeout;
   }

   public TcpClient(String serverAddress, int serverPort) {
      this(serverAddress, serverPort, 5000);
   }

   /**
    * Establishes a TCP connection to the server using the specified address and
    * port.
    * <p>
    * If the client is already connected (socket is alive), this method returns
    * immediately.
    * Otherwise, it connects to the server.
    * </p>
    * <p>
    * If the socket is not alive after attempting the application exits.
    * </p>
    *
    * @throws IOException if an I/O error occurs when creating the socket or
    *                     streams,
    *                     or if the connection attempt fails.
    */
   public void connect() throws IOException {
      if (isAlive()) {
         return;
      }

      socket = new Socket();
      socket.connect(new InetSocketAddress(serverAddress, serverPort));
      socket.setSoTimeout(timeout);

      out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      if (!isAlive()) {
         System.err.println("[TcpClient] Socket not alive at start");
         System.exit(1);
      }
      System.out.println("[TcpClient] Connected to server with socket " + socket);
   }

   /**
    * Sends a request to the server and waits for a response
    *
    * @param request the {@link Request} object to send to the server
    * @return a {@link Response} or {@code null} if the client is not alive or the
    *         response cannot be parsed
    * @throws IOException if an I/O error occurs or if the server response times
    *                     out
    */
   public Response sendRequest(Request request) throws IOException {
      if (!isAlive())
         return null;

      out.write(JsonUtil.toJson(request));
      out.newLine();
      out.flush();

      if (request.getOperation() == null) {
         return null;
      }
      
      // wait for a single non-null line response
      String line;
      try {
         while (true) {
            line = in.readLine();
            // !!!!!! always null with logout
            if (line != null)
               break;
            else if (!isAlive())
               return null;
         }  
      } catch (SocketTimeoutException e) {
         throw new IOException("[TcpClient] Timed out waiting for server response: " + e.getMessage(), e);
      }

      // try to convert to a MessageResponse
      MessageResponse messageResponse = JsonUtil.fromJson(line, MessageResponse.class);
      if (messageResponse != null && messageResponse.getErrorMessage() != null) {
         return messageResponse;
      }

      // fallback to OrderResponse
      OrderResponse orderResponse = JsonUtil.fromJson(line, OrderResponse.class);
      if (orderResponse != null && orderResponse.getOrderId() == 0) {
         return orderResponse;
      }

      return null;
   }

   @Override
   public void close() {
      try {
         if (in != null)
            in.close();
      } catch (IOException ignored) {
      }
      try {
         if (out != null)
            out.close();
      } catch (IOException ignored) {
      }
      try {
         if (socket != null)
            socket.close();
      } catch (IOException ignored) {
      }
      in = null;
      out = null;
      socket = null;
   }

   /**
    * Checks if the underlying socket is alive.
    * <p>
    * This method returns {@code true} if the socket is not {@code null},
    * is connected, and has not been closed. Otherwise, it returns {@code false}.
    *
    * @return {@code true} if the socket is alive; {@code false} otherwise.
    */
   public boolean isAlive() {
      return socket != null && socket.isConnected() && !socket.isClosed();
   }

   /**
    * Sends an empty request to the server to keep the connection alive and reset
    * the server's timeout.
    *
    * @throws IOException if an I/O error occurs while sending the request.
    */
   public void keepServerAlive() throws IOException {
      // send an empty message to server to make timeout reset
      sendRequest(new Request());
   }
}