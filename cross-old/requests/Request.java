package cross.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cross.Prompt;
import cross.adapters.SuperFirstTypeAdapter;
import cross.values.Values;

/**
 * The Request class represents the standard format of a TCP request to the server, with an operation field and all the necessary values/parameters.
 */
public class Request {

   private String operation;
   private Values values;

   public Request() {}

   public String getOperation() {
      return operation;
   }
   
   public void setOperation(String operation) {
      this.operation = operation;
   }

   public Values getValues() {
      return values;
   }

   public void setValues(Values values) {
      this.values = values;
   }

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(SuperFirstTypeAdapter.newFactory(values.getClass()))
            .setPrettyPrinting()
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
