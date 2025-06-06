package it.unipi.cross.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unipi.cross.json.Request;

public class ClientMainTest {

   private final PrintStream originalOut = System.out;
   private ByteArrayOutputStream outContent;

   @BeforeEach
   void setUpStreams() {
      outContent = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outContent));
   }

   @AfterEach
   void restoreStreams() {
      System.setOut(originalOut);
   }

   @Test
   void testPromptPrintStartAndEnd() {
      Prompt.printStart();
      String startOutput = outContent.toString();
      assertTrue(startOutput.toLowerCase().contains("cross"), "Should print start banner");

      outContent.reset();
      Prompt.printEnd();
      String endOutput = outContent.toString();
      assertTrue(endOutput.toLowerCase().contains("thank you") || endOutput.toLowerCase().contains("end"),
            "Should print end message");
   }

   @Test
   void testPromptPrintHelp() {
      outContent.reset();
      Prompt.printHelp();
      String helpOutput = outContent.toString();
      assertTrue(helpOutput.toLowerCase().contains("help"), "Should print help message");
   }

   @Test
   void testRequestFactoryCreatesRequest() {
      Request req = RequestFactory.create("login(user,pass)");
      assertNotNull(req, "Request should not be null for valid command");
      assertEquals("login", req.getOperation());
      assertEquals("user", req.getValues().get("username"));
      assertEquals("pass", req.getValues().get("password"));
   }

   @Test
   void testRequestFactoryInvalidCommand() {
      Request req = RequestFactory.create("invalidcommand()");
      assertNotNull(req, "Should return a notDefined request for unknown command");
      assertEquals("notDefined", req.getOperation());
   }

   @Test
   void testRequestFactoryEmptyArgs() {
      Request req = RequestFactory.create("login()");
      assertNotNull(req, "Should return notDefined for empty args");
      assertEquals("notDefined", req.getOperation());
   }

   @Test
   void testRequestFactoryInvalidArgs() {
      Request req = RequestFactory.create("insertLimitOrder(a,b,c)");
      assertNotNull(req, "Should return invalidArgs for wrong args");
      assertEquals("invalidArgs", req.getOperation());
   }


   void testClientMainHandlesInvalidCommandFormat() {
      String input = "badcommand\nexit()\n";
      System.setIn(new ByteArrayInputStream(input.getBytes()));
      // Run main in a separate thread to avoid System.exit
      Thread t = new Thread(() -> {
         try {
            ClientMain.main(new String[0]);
         } catch (Exception ignored) {
         }
      });
      t.start();
      try {
         t.join(1000);
      } catch (InterruptedException ignored) {
      }
      String output = outContent.toString();
      assertTrue(output.contains("Command format not valid") || output.contains("command not defined"));
   }
}