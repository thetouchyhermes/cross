package it.unipi.cross.history;

public class DailyStats {
   private String day;
   private int openingPrice;
   private int closingPrice;
   private int maximumPrice;
   private int minimumPrice;

   public DailyStats(String day, int openingPrice, int closingPrice, int maximumPrice, int minimumPrice) {
      this.day = day;
      this.openingPrice = openingPrice;
      this.closingPrice = closingPrice;
      this.maximumPrice = maximumPrice;
      this.minimumPrice = minimumPrice;
   }

   public String getDay() {
      return day;
   }

   public void setDay(String day) {
      this.day = day;
   }

   public int getOpeningPrice() {
      return openingPrice;
   }

   public void setOpeningPrice(int openingPrice) {
      this.openingPrice = openingPrice;
   }

   public int getClosingPrice() {
      return closingPrice;
   }

   public void setClosingPrice(int closingPrice) {
      this.closingPrice = closingPrice;
   }

   public int getMaximumPrice() {
      return maximumPrice;
   }

   public void setMaximumPrice(int maximumPrice) {
      this.maximumPrice = maximumPrice;
   }

   public int getMinimumPrice() {
      return minimumPrice;
   }

   public void setMinimumPrice(int minimumPrice) {
      this.minimumPrice = minimumPrice;
   }

}
