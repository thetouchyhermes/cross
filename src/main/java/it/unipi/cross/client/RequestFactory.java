package it.unipi.cross.client;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.unipi.cross.json.Request;

public class RequestFactory {

   public static Request create(String line) {

      int parIdx1 = line.indexOf('(');
      int parIdx2 = line.lastIndexOf(')');

      String command = line.substring(0, parIdx1).trim();
      String[] params = line.substring(parIdx1 + 1, parIdx2).split(",");

      List<String> paramList = new ArrayList<>(Arrays.asList(params));
      paramList.replaceAll(str -> str.trim());

      System.out.println(command + " (" + String.join(", ", paramList) + ")");

      Request request = new Request();
      Map<String, Object> values = new LinkedHashMap<>();

      request.setOperation(command);
      request.setValues(values);

      boolean emptyParam = false;
      for (String str : paramList) {
         if (str.isBlank()) {
            emptyParam = true;
            break;
         }
      }
      if (emptyParam) {
         switch (command) {
            case "logout":
            case "help":
            case "exit":
               break;
            default:
               request.setOperation("notDefined");
               return request;
         }
      }

      String type;
      int size, price, orderId;

      try {
         switch (command) {
            case "register":
            case "login":
               if (paramList.size() != 2) {
                  request.setOperation("notDefined");
                  break;
               }

               values.put("username", paramList.get(0));
               values.put("password", paramList.get(1));
               break;
            case "updateCredentials":
               if (paramList.size() != 3) {
                  request.setOperation("notDefined");
                  break;
               }

               values.put("username", paramList.get(0));
               values.put("old_password", paramList.get(1));
               values.put("new_password", paramList.get(2));
               break;
            case "insertLimitOrder":
            case "insertStopOrder":
               if (paramList.size() != 3) {
                  request.setOperation("notDefined");
                  break;
               }

               size = Integer.parseInt(paramList.get(1));
               price = Integer.parseInt(paramList.get(2));
               type = paramList.get(0).toLowerCase();
               if (!type.equals("ask") && !type.equals("bid") || size < 0 || price < 0) {
                  request.setOperation("invalidArgs");
                  break;
               }

               values.put("type", type);
               values.put("size", size);
               values.put("price", price);
               break;
            case "insertMarketOrder":
               if (paramList.size() != 2) {
                  request.setOperation("notDefined");
                  break;
               }

               size = Integer.parseInt(paramList.get(1));
               type = paramList.get(0).toLowerCase();
               if (!type.equals("ask") && !type.equals("bid") || size < 0) {
                  request.setOperation("invalidArgs");
                  break;
               }

               values.put("type", type);
               values.put("size", size);
               break;
            case "cancelOrder":
               if (paramList.size() != 1) {
                  request.setOperation("notDefined");
                  break;
               }

               orderId = Integer.parseInt(paramList.get(0));
               if (orderId <= 0) {
                  request.setOperation("invalidArgs");
                  break;
               }

               values.put("orderId", orderId);
               break;
            case "getPriceHistory":
               if (paramList.size() != 1) {
                  request.setOperation("notDefined");
                  break;
               }

               String monthString = paramList.get(0);
               int month = Integer.parseInt(monthString);
               if (month < 10000 || month > 129999) {
                  request.setOperation("invalidArgs");
                  break;
               }
               int monthNum = Integer.parseInt(monthString.substring(0,2));
               int yearNum = Integer.parseInt(monthString.substring(2));
               if (monthNum < 1 || monthNum > 12 || yearNum < 1970 || yearNum > Year.now().getValue()) {
                  request.setOperation("invalidArgs");
                  break;
               }

               values.put("month", monthString);
               break;
            case "help":
               if (paramList.size() > 1) {
                  request.setOperation("notDefined");
                  break;
               }
               String com = paramList.get(0);
               if (!com.isEmpty())
                  values.put("command", com);
               break;
            case "logout":
            case "exit":
               if (paramList.size() != 1 || !paramList.get(0).isEmpty()) {
                  request.setOperation("notDefined");
               }
               break;
            default:
               request.setOperation("notDefined");
         }
      } catch (NumberFormatException e) {
         request.setOperation("invalidArgs");
      } catch (IllegalArgumentException e) {
         request.setOperation("notDefined");
      }

      return request;
   }

}
