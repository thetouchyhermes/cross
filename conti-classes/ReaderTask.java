package com;
import java.util.concurrent.*;
import java.io.*;

/**
 * The ReaderTask class implements Runnable and is responsible for reading Account objects from a file
 * and placing them into a BlockingQueue for processing by other threads.
 */
public class ReaderTask implements Runnable {
   private BlockingQueue<Account> q;
   private ConfigFile config = new ConfigReader().getConfigFile();

   /**
    * Constructs a ReaderTask with the specified BlockingQueue.
    * @param q the BlockingQueue to place Account objects into
    */
   public ReaderTask(BlockingQueue<Account> q) {
      this.q = q;
   }

   /**
    * The run method is executed when the thread is started. It reads Account objects from a file
    * and places them into the BlockingQueue. It also places an EndOfList object to signal the end of the list.
    */
   @Override
   public void run() {
      System.out.println("Thread reader started");
      try {
         AccountReader reader;
         try {
            reader = new AccountReader(config.getInputFile());
            while (reader.hasNext()) {
               Account temp = reader.next();
               q.put(temp);
            }
         } catch (IOException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
         }
         // Last element to stop consumers
         q.put(new EndOfList());
      } catch (InterruptedException e) {
         System.err.println(e.getClass() + ": " + e.getMessage());
      }
      System.out.println("Thread reader ended");
   }
}
