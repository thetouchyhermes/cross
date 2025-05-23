package com;

import java.util.concurrent.*;
import com.google.gson.*;

/**
 * The ReasonCounter class keeps track of the count of transactions for each reason (objects of Reason type, not strings)
 */
public class ReasonCounter {

   private final ConcurrentHashMap<Reason, Integer> map = new ConcurrentHashMap<>();

   /**
    * Default constructor that initializes the count for each reason to zero.
    */
   public ReasonCounter() {
      map.put(Reason.valueOf("BONIFICO"), 0);
      map.put(Reason.valueOf("ACCREDITO"), 0);
      map.put(Reason.valueOf("BOLLETTINO"), 0);
      map.put(Reason.valueOf("F24"), 0);
      map.put(Reason.valueOf("PAGOBANCOMAT"), 0);
   }

   /**
    * Increases the count for the specified reason by one.
    * @param reason the reason for the transaction
    */
   public synchronized void increase(Reason reason) {
      map.put(reason, map.getOrDefault(reason, 0) + 1);
   }

   /**
    * Gets the count for the specified reason.
    * @param reason the reason for the transaction
    * @return the count for the specified reason
    */
   public synchronized int get(Reason reason) {
      return map.getOrDefault(reason, 0);
   }

   /**
    * Returns a JSON representation of the reason counts.
    * @return a JSON string representing the reason counts
    */
   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      String out = "";
      try {
         out = gson.toJson(map);
      } catch (Exception e) {
         System.err.println(e.getClass() + ": " + e.getMessage());
      }
      return out;
   }
}
