package cross.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cross.Prompt;

public class Response {

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            //.setPrettyPrinting()
            .create();
      String out = "";
      try {
         out = gson.toJson(this);
      } catch (Exception e) {
         Prompt.printError(e.getClass() + ": " + e.getMessage());
      }
      return out;
   }
}
