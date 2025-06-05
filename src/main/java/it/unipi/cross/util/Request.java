package it.unipi.cross.util;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
 * Map&lt;String, Object&gt; params = new HashMap&lt;&gt;();
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

   // for testing
   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
}