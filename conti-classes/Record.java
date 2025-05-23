package com;
import java.time.*;

/**
 * The Record class represents a transaction record with a date and a reason.
 */
public class Record {

   private LocalDateTime date;
   private Reason reason;

   /**
    * Default constructor
    */
   public Record() {}

   /**
    * Sets the date of the transaction.
    * @param date the date of the transaction
    */
   public void setDate(LocalDateTime date) {
      this.date = date;
   }

   /**
    * Gets the date of the transaction.
    * @return the date of the transaction
    */
   public LocalDateTime getDate() {
      return date;
   }

   /**
    * Sets the reason for the transaction.
    * @param reason the reason for the transaction
    */
   public void setReason(Reason reason) {
      this.reason = reason;
   }

   /**
    * Gets the reason for the transaction.
    * @return the reason for the transaction
    */
   public Reason getReason() {
      return reason;
   }

}
