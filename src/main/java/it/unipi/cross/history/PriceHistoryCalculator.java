package it.unipi.cross.history;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.unipi.cross.config.ConfigReader;
import it.unipi.cross.data.Trade;

public class PriceHistoryCalculator {

   private File historyFile;
   private final List<Trade> trades = new LinkedList<>();

   public PriceHistoryCalculator() {
      ConfigReader config = new ConfigReader();
      try {
         config.loadServer();
         String historyFilePath = config.getString("history.file");
         historyFile = new File(historyFilePath);
      } catch (IOException e) {
         System.err.println("Error while searching for history file");
      }

      if (historyFile != null) {
         // import trades from file
      }
   }

   public List<PriceHistory> getPriceHistory(String month) {
      // main body of the calculation

      // stub code
      List<PriceHistory> prices = new ArrayList<>();
      prices.add(null);
      return prices;
   }

   // other private methods as needed
}