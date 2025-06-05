package it.unipi.cross.network;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.Socket;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.server.OrderBook;
import it.unipi.cross.server.UserBook;

class TcpServerConnectionTest {

    static Thread serverThread;

    @BeforeAll
    static void startServer() {
        serverThread = new Thread(() -> {
            try {
                OrderBook orderBook = new OrderBook(null);
                UserBook userBook = new UserBook(null);
                ConfigReader config = new ConfigReader();
                config.loadServer();
                // Make sure this sets tcp.port
                TcpServer server = new TcpServer(orderBook, userBook, config);
                // Set thread pool for TcpServer if needed
                java.lang.reflect.Field poolField = TcpServer.class.getDeclaredField("threadPool");
                poolField.setAccessible(true);
                poolField.set(server, Executors.newCachedThreadPool());
                server.start();
            } catch (Exception e) {
                // Ignore, server will be stopped when test ends
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Wait a moment for the server to start
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    @Test
    void testTcpConnection() {
        String host = "localhost";
        int port = 50123;

        try (Socket socket = new Socket(host, port)) {
            assertTrue(socket.isConnected(), "Socket should be connected");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not connect to TCP server: " + e.getClass() + e.getMessage());
        }
    }
}