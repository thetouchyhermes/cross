package it.unipi.cross.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Response {

   // for testing
   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
   
}
