package com;
import java.util.concurrent.*;
import java.util.*;

/**
 * The WorkerTask class implements Runnable and is responsible for processing Account objects
 * from a BlockingQueue and updating the ReasonCounter with the transaction reasons.
 */
public class WorkerTask implements Runnable {
   private static int id_count = 0;
   private final int id;
   private BlockingQueue<Account> q;
   private ReasonCounter counter;

   /**
    * Constructs a WorkerTask with the specified BlockingQueue and ReasonCounter.
    * @param q the BlockingQueue to take Account objects from
    * @param counter the ReasonCounter to update with transaction reasons
    */
   public WorkerTask(BlockingQueue<Account> q, ReasonCounter counter) {
      this.q = q;
      this.counter = counter;
      this.id = id_count;
      id_count++;
   }

   /**
    * Processes an Account object by updating the ReasonCounter with the transaction reasons.
    * @param account the Account object to process
    */
   private void handleAccount(Account account) {
      //System.out.println("Thread " + id + " consumed account: "+ account.getOwner());
      List<Record> records = account.getRecords();
      for (int i = 0; i < records.size(); i++) {
         Reason reason = records.get(i).getReason();
         counter.increase(reason);
      }
   }

   /**
    * The run method is executed when the thread is started. It processes Account objects
    * from the BlockingQueue until an EndOfList object is encountered.
    */
   @Override
   public void run() {
      System.out.println("Thread " + id + " started");
      try {
         while (true) {
            Account account = q.take();
            if (account instanceof EndOfList) {
               q.put(account);
               //System.out.println("Thread " + id + ": Reached end of list");
               break;
            } else {
               handleAccount(account);
            }
         }
      } catch (InterruptedException e) {
         System.err.println(e.getClass() + ": " + e.getMessage());
         Thread.currentThread().interrupt();
      }
      System.out.println("Thread " + id + " ended");
   }

}
