package it.unipi.cross.history;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.data.Trade;
import it.unipi.cross.json.JsonUtil;
import it.unipi.cross.json.PriceHistory;

public class PriceHistoryCalculator {

   private File historyFile;
   private TradeHistory tradeHistory;

   public PriceHistoryCalculator() throws IOException {
      ConfigReader config = new ConfigReader();
      config.loadServer();
      String historyFilePath = config.getString("history.file");
      historyFile = new File(historyFilePath);

      if (historyFile != null) {
         this.tradeHistory = JsonUtil.readFromFile(historyFile, TradeHistory.class);
      } else {
         throw new IOException("History file not available");
      }
   }

   public PriceHistory getPriceHistory(String month) {
 
         // Parse month input (MMYYYY format)
         int monthNum = Integer.parseInt(month.substring(0, 2));
         int year = Integer.parseInt(month.substring(2));

         // Group trades by day
         Map<String, List<Trade>> tradesByDay = new TreeMap<>();
         DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

         for (Trade trade : tradeHistory.getTrades()) {
            // Convert timestamp to GMT date
            ZonedDateTime tradeDate = Instant.ofEpochSecond(trade.getTimestamp())
                  .atZone(ZoneOffset.UTC);

            // Check if trade belongs to the requested month/year
            if (tradeDate.getMonthValue() == monthNum && tradeDate.getYear() == year) {
               String dayKey = tradeDate.format(dayFormatter);
               if (!tradesByDay.containsKey(dayKey)) {
                  tradesByDay.put(dayKey, new ArrayList<>());
               }

               // Add to the list of trades of the same day
               tradesByDay.get(dayKey).add(trade);
            }
         }

         // Generate daily stats for all days in the month (including days with no trades)
         List<DailyStats> dailyStatsList = new ArrayList<>();
         ZonedDateTime startDate = ZonedDateTime.of(year, monthNum, 1, 0, 0, 0, 0, ZoneId.systemDefault());
         ZonedDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

         // Keep track of last known price for days with no trades
         int lastKnownPrice = 0;

         for (ZonedDateTime d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            String dayKey = d.format(dayFormatter);
            List<Trade> dayTrades = tradesByDay.get(dayKey);

            if (dayTrades != null && !dayTrades.isEmpty()) {
               // Sort trades by timestamp for this day
               dayTrades.sort(Comparator.comparingLong(t -> t.getTimestamp()));

               int openingPrice = dayTrades.get(0).getPrice();
               int closingPrice = dayTrades.get(dayTrades.size() - 1).getPrice();
               int maximumPrice = Collections.max(dayTrades, Comparator.comparingInt(Trade::getPrice)).getPrice();
               int minimumPrice = Collections.min(dayTrades, Comparator.comparingInt(Trade::getPrice)).getPrice();

               // Update last known price for the following days
               lastKnownPrice = closingPrice;
               dailyStatsList.add(new DailyStats(dayKey, openingPrice, closingPrice, maximumPrice, minimumPrice));
            } else {
               // No trades for this day - use last known price or 0 if no previous data
               int price = lastKnownPrice;
               dailyStatsList.add(new DailyStats(dayKey, price, price, price, price));
            }
         }

         return new PriceHistory(dailyStatsList);

   }

}