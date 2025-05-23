package cross.values;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cross.Prompt;
import cross.adapters.SuperFirstTypeAdapter;

/**
 * The Values superclass defines all those classes that represent a TCP request
 */
public class Values {

   @Override
   public String toString() {

      Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(SuperFirstTypeAdapter.newFactory(this.getClass()))
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
