package it.unipi.cross.json;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Represents a request containing an operation and associated values (as a map)
 */
public class Request {

   private String operation;
   private Map<String, Object> values;

   public Request() {
   }

   public String getOperation() {
      return operation;
   }

   public void setOperation(String operation) {
      this.operation = operation;
   }

   public Map<String, Object> getValues() {
      return values;
   }

   public void setValues(Map<String, Object> values) {
      this.values = values;
   }

   /** Returns the indicated string from the values map if present, or null **/
   public String getAsString(String key) {
      Object value = values.get(key);
      return (value != null) ? String.valueOf(value) : null;
   }

   /** Returns the indicated integer from the values map if present, or null **/
   public Integer getAsInteger(String key) {
      Object value = values.get(key);
      if (value == null)
         return null;

      if (value instanceof Integer) {
         return (Integer) value;
      } else if (value instanceof String) {
         try {
            return Integer.parseInt((String) value);
         } catch (NumberFormatException e) {
            return null;
         }
      }
      return null;
   }

   @Override
   public String toString() {
      String[] integerKeys = { "size", "price", "orderId" };
      Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                  new TypeToken<Map<String, Object>>() {
                  }.getType(),
                  new ConditionalMapTypeAdapter(integerKeys))
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
}