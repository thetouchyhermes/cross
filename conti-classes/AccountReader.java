package com;
import java.io.*;
import com.google.gson.stream.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * The AccountReader class reads Account objects from a JSON file.
 */
public class AccountReader {
   private JsonReader reader;
   private Account nextAccount;
   private boolean endedFile;

   /**
    * Constructs an AccountReader to read from the specified file.
    * @param filepath the path to the JSON file
    * @throws IOException if an I/O error occurs
    */
   public AccountReader(String filepath) throws IOException {
      this.reader = new JsonReader(new FileReader(filepath));
      this.reader.beginArray();
      this.endedFile = false;
   }

   /**
    * Checks if there is another Account object to read.
    * @return true if there is another Account object, false otherwise
    */
   public boolean hasNext() {
      if (nextAccount != null)
         return true;

      if (!endedFile) {
         try {
            if (reader.hasNext()) {
               nextAccount = getNext();
               return true;
            } else {
               endedFile = true;
               reader.endArray();
               reader.close();
            }
         } catch (IOException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
         } catch (IllegalStateException e) {
            System.err.println(e.getClass() + ": " + e.getMessage());
         }
      }

      return false;
   }

   /**
    * Returns the next Account object.
    * @return the next Account object
    */
   public Account next() {
      Account tempAccount = nextAccount;
      nextAccount = null;
      return tempAccount;
   }

   /**
    * Reads the next Account object from the JSON file.
    * @return the next Account object
    */
   private Account getNext() {
      // read Account object
      Account account = new Account();
      try {
         reader.beginObject();
         while (reader.hasNext()) {

            // read Account properties
            String accountName = reader.nextName();
            switch (accountName) {
               case "owner":
                  // read Owner field
                  account.setOwner(reader.nextString());
                  break;
               case "records":
                  // read Records array
                  List<Record> records = new LinkedList<>();
                  reader.beginArray();
                  while (reader.hasNext()) {
                     // read Record object
                     Record record = new Record();
                     reader.beginObject();
                     while (reader.hasNext()) {
                        // read Record properties
                        String recordName = reader.nextName();
                        switch (recordName) {
                           case "date":
                              // read Date field
                              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:ss a", Locale.ENGLISH);
                              LocalDateTime date = LocalDateTime.parse(reader.nextString(), formatter);
                              record.setDate(date);
                              break;
                           case "reason":
                              // read Reason field
                              record.setReason(Reason.valueOf(reader.nextString()));
                              break;
                           default:
                              reader.skipValue();
                        }
                     }
                     // finished Record object
                     reader.endObject();
                     records.add(record);
                  }
                  // finished Records array
                  reader.endArray();
                  account.setRecords(records);
                  break;
               default:
                  reader.skipValue();
            }
         }
         // finished Account object
         reader.endObject();
      } catch (IOException e) {
         System.err.println(e.getClass() + ": " + e.getMessage());
      } catch (IllegalStateException e) {
         System.err.println(e.getClass() + ": " + e.getMessage());
      }
      return account;
   }

}
