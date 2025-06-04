package it.unipi.cross.util;

import java.util.Map;

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
}