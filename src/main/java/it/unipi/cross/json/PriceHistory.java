package it.unipi.cross.json;

import java.util.List;

import it.unipi.cross.history.DailyStats;

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

}
