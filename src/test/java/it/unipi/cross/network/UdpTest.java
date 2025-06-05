package it.unipi.cross.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class UdpTest {
    private static final String MULTICAST_ADDRESS = "230.0.0.1";
    private static final int PORT = 4450;
    private Thread listenerThread;
    private UdpListener listener;

    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    void startListener() throws Exception {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        listener = new UdpListener(MULTICAST_ADDRESS, PORT);
        listenerThread = new Thread(listener);
        listenerThread.start();
        Thread.sleep(1000); // Let listener start
    }

    @AfterEach
    void tearDown() {
        if (listener != null) listener.shutdown();
        if (listenerThread != null && listenerThread.isAlive()) listenerThread.interrupt();
        if (originalOut != null) System.setOut(originalOut);
    }

    @Test
    void testNotifierThrowsOnInvalidAddress() {
        UdpNotifier notifier = new UdpNotifier("invalid-address", PORT);
        assertThrows(Exception.class, () -> notifier.notify("test"));
    }

    @Test
    void testListenerReceivesSingleMessage() throws Exception {
        startListener();
        UdpNotifier notifier = new UdpNotifier(MULTICAST_ADDRESS, PORT);
        String msg = "SingleMessage";
        notifier.notify(msg);
        Thread.sleep(1000);
        listener.shutdown();

        String output = outContent.toString();
        assertTrue(output.contains(msg), "Listener should receive the single message");
    }

    @Test
    void testListenerReceivesMultipleMessages() throws Exception {
        startListener();
        UdpNotifier notifier = new UdpNotifier(MULTICAST_ADDRESS, PORT);
        String msg1 = "First";
        String msg2 = "Second";
        notifier.notify(msg1);
        notifier.notify(msg2);
        Thread.sleep(1000);
        listener.shutdown();

        String output = outContent.toString();
        assertTrue(output.contains(msg1), "Listener should receive the first message");
        assertTrue(output.contains(msg2), "Listener should receive the second message");
    }

    @Test
    void testListenerShutdownStopsReceiving() throws Exception {
        startListener();
        UdpNotifier notifier = new UdpNotifier(MULTICAST_ADDRESS, PORT);
        String msg = "BeforeShutdown";
        notifier.notify(msg);
        Thread.sleep(1000);
        listener.shutdown();
        Thread.sleep(1000);

        String msg2 = "AfterShutdown";
        notifier.notify(msg2);
        Thread.sleep(1000);

        String output = outContent.toString();
        assertTrue(output.contains(msg), "Listener should receive message before shutdown");
        assertFalse(output.contains(msg2), "Listener should not receive message after shutdown");
    }

    @Test
    void testListenerHandlesSocketExceptionGracefully() throws Exception {
        // Simulate by using an invalid port (negative)
        listener = new UdpListener(MULTICAST_ADDRESS, -1);
        listenerThread = new Thread(listener);
        listenerThread.start();
        Thread.sleep(1000);
        // Should not throw, but print error
        listener.shutdown();
    }
}