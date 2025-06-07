package it.unipi.cross.history;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.unipi.cross.data.Trade;
import it.unipi.cross.json.TradeTypeAdapter;

public class TradeHistory {
   private List<Trade> trades;

   public TradeHistory(List<Trade> trades) {
      this.trades = trades;
   }

   public List<Trade> getTrades() {
      return trades;
   }

   public void setTrades(List<Trade> trades) {
      this.trades = trades;
   }

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .registerTypeAdapter(Trade.class, new TradeTypeAdapter())
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
}
