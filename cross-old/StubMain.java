package cross;

import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import cross.requests.Request;
import cross.values.CancelOrderValues;
import cross.values.CredentialsValues;
import cross.values.HistoryValues;
import cross.values.OrderValues;
import cross.values.PriceOrderValues;
import cross.values.UpdateCredentialsValues;
import cross.values.Values;

public class StubMain {
   
   public static void main(String[] args) {
      try (JsonReader reader = new JsonReader(new FileReader("src/main/resources/request.json"))) {

         Gson gson = new Gson();
         JsonObject obj = gson.fromJson(reader, JsonObject.class);
         String operation = obj.get("operation").getAsString();
         JsonElement elem = obj.get("values");
         Values values = null;

         switch (operation) {
            case "register":
            case "login":
               values = gson.fromJson(elem, CredentialsValues.class);
               break;
            case "updateCredentials":
               values = gson.fromJson(elem, UpdateCredentialsValues.class);
               break;
            case "insertLimitOrder":
            case "insertStopOrder":
               values = gson.fromJson(elem, PriceOrderValues.class);
               break;
            case "insertMarketOrder":
               values = gson.fromJson(elem, OrderValues.class);
               break;
            case "cancelOrder":
               values = gson.fromJson(elem, CancelOrderValues.class);
               break;
            case "getPriceHistory":
               values = gson.fromJson(elem, HistoryValues.class);
               break;
            case "logout":
            default:
               values = gson.fromJson(elem, Values.class);
         }

         Request request = new Request();
         request.setOperation(operation);
         request.setValues(values);
         System.out.println(request.toString());
         
      } catch (IOException e) {
         Prompt.printError(e.getClass() + ": " + e.getMessage());
         Prompt.exit(1);
      }   
   }

}
