package it.unipi.cross.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Response {
   private final int response;
   private final String errorMessage;

   public Response(int response, String errorMessage) {
      this.response = response;
      this.errorMessage = errorMessage;
   }

   public int getResponse() {
      return response;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
}
