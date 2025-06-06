package it.unipi.cross.json;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.unipi.cross.data.UserTrade;

public class Notification {
   private final String notification = "closedTrades";
   private final List<UserTrade> trades;

   public Notification(List<UserTrade> trades) {
      this.trades = new LinkedList<>(trades);
   }

   public String getNotification() {
      return notification;
   }

   public List<UserTrade> getTrades() {
      return trades;
   }

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
   
}
