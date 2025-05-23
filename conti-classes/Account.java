package com;
import java.util.*;
import com.google.gson.*;
import java.time.*;

/**
 * The Account class represents a bank account with an owner and a list of transaction records.
 */
public class Account {

   private String owner;
   private List<Record> records;

   /**
    * Default constructor that initializes the owner to an empty string and the records to an empty list.
    */
   public Account() {
      this.owner = "";
      this.records = new LinkedList<Record>();
   }

   /**
    * Sets the owner of the account.
    * @param owner the name of the account owner
    */
   public void setOwner(String owner) {
      this.owner = owner;
   }

   /**
    * Gets the owner of the account.
    * @return the name of the account owner
    */
   public String getOwner() {
      return owner;
   }

   /**
    * Sets the records of the account.
    * @param records a list of transaction records
    */
   public void setRecords(List<Record> records) {
      this.records = new LinkedList<Record>(records);
   }

   /**
    * Gets the records of the account.
    * @return a list of transaction records
    */
   public List<Record> getRecords() {
      return records;
   }

   /**
    * Returns a JSON representation of the account.
    * @return a JSON string representing the account
    */
   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
         .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
         .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
         .setPrettyPrinting()
         .create();
      String out = "";
      try {
         out = gson.toJson(this);
      } catch (Exception e) {
         System.err.println(e.getClass() + ": " + e.getMessage());
      }
      return out;
   }
}
