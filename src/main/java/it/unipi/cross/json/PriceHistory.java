package it.unipi.cross.json;

import java.util.List;

import it.unipi.cross.history.DailyStats;

/**
 * Represents a response object for price history request, includes stats for
 * the requested month
 **/
public class PriceHistory extends Response {
   private List<DailyStats> dailyStats;

   public PriceHistory(List<DailyStats> dailyStats) {
      this.dailyStats = dailyStats;
   }

   public List<DailyStats> getDailyStats() {
      return dailyStats;
   }

   public void setDailyStats(List<DailyStats> dailyStats) {
      this.dailyStats = dailyStats;
   }

   /** prints stats as a table, with a line for every day of the month **/
   public void printDailyStats() {
      System.out.println("\nDailyStats for " + getDailyStats().get(0).getDay().substring(3) + " (1 unit = 0.001BTC)");
      System.out.println(" ------------------------------------------------------------------------------ ");
      System.out.printf("| %-12s | %13s | %13s | %13s | %13s |%n",
            "Day", "Opening Price", "Closing Price", "Max Price", "Min Price");
      System.out.println(" ------------------------------------------------------------------------------ ");
      for (DailyStats stats : getDailyStats()) {
         System.out.printf("| %-12s | %13d | %13d | %13d | %13d |%n",
               stats.getDay(), stats.getOpeningPrice(), stats.getClosingPrice(), stats.getMaximumPrice(),
               stats.getMinimumPrice());
      }
      System.out.println(" ------------------------------------------------------------------------------ \n");
   }

}
