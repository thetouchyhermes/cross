package it.unipi.cross.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Response {

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
   
}
