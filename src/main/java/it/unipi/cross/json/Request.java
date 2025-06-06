package it.unipi.cross.json;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Represents a request containing an operation and associated values.
 * <p>
 * This class encapsulates the details of a request, including the operation to
 * be performed
 * and a map of key-value pairs representing additional parameters or data
 * required for the operation.
 * </p>
 *
 * <p>
 * Typical usage:
 * </p>
 * 
 * <pre>
 * Request request = new Request();
 * request.setOperation("createUser");
 * Map&lt;String, Object&gt; params = new LinkedHashMap&lt;&gt;();
 * params.put("username", "john");
 * request.setValues(params);
 * </pre>
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

   public String getAsString(String key) {
      Object value = values.get(key);
      return (value != null) ? String.valueOf(value) : null;
   }

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

   // for testing
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