package cross.requests;

import java.util.NoSuchElementException;

import cross.Prompt;
import cross.values.CancelOrderValues;
import cross.values.CredentialsValues;
import cross.values.HistoryValues;
import cross.values.OrderValues;
import cross.values.PriceOrderValues;
import cross.values.UpdateCredentialsValues;
import cross.values.Values;

public class RequestFactory {

   public static Request create(String command, String username) throws NoSuchElementException {

      boolean error = false;
      Request request = new Request();
      Values values = null;
      String[] cmd = command.split(" ");
      String op = cmd[0].toLowerCase();
      request.setOperation(op);

      int size, price, orderId;

      try {
         switch (op) {
            case "register":
            case "login":
               if (cmd.length != 3) {
                  Prompt.printError("RequestFactory: Command is not defined for this number of arguments");
                  error = true;
                  break;
               }
               values = new CredentialsValues(cmd[1], cmd[2]);
               break;
            case "updatecredentials":
               if (cmd.length != 4) {
                  Prompt.printError("RequestFactory: Command is not defined for this number of arguments");
                  error = true;
                  break;
               }
               request.setOperation("updateCredentials");
               values = new UpdateCredentialsValues(cmd[1], cmd[2], cmd[3]);
               break;
            case "logout":
               if (cmd.length != 2) {
                  Prompt.printError("RequestFactory: Command is not defined for this number of arguments");
                  error = true;
                  break;
               }
               if ( !cmd[1].equals(username) ) {
                  Prompt.printError("RequestFactory: Username is not equal to logged user");
                  error = true;
                  break;
               }
               values = new Values();
               break;
            case "insertlimitorder":
               request.setOperation("insertLimitOrder");
            case "insertstoporder":
               if (cmd.length != 4) {
                  Prompt.printError("RequestFactory: Command is not defined for this number of arguments");
                  error = true;
                  break;
               }
               if (op.equals("insertstoporder"))
                  request.setOperation("insertStopOrder");
               size = Integer.parseInt(cmd[2]);
               if (size < 0) {
                  Prompt.printError("RequestFactory: Order size is not valid");
                  error = true;
                  break;
               }
               price = Integer.parseInt(cmd[3]);
               if (price < 0) {
                  Prompt.printError("RequestFactory: Order price is not valid");
                  error = true;
                  break;
               }
               values = new PriceOrderValues(cmd[1], size, price);
               break;
            case "insertmarketorder":
               if (cmd.length != 3) {
                  Prompt.printError("RequestFactory: Command is not defined for this number of arguments");
                  error = true;
                  break;
               }
               request.setOperation("insertMarketOrder");
               size = Integer.parseInt(cmd[2]);
               if (size < 0) {
                  Prompt.printError("RequestFactory: Order size is not valid");
                  error = true;
                  break;
               }
               values = new OrderValues(cmd[1], size);
               break;
            case "cancelorder":
               if (cmd.length != 2) {
                  Prompt.printError("RequestFactory: Command is not defined for this number of arguments");
                  error = true;
                  break;
               }
               request.setOperation("cancelOrder");
               orderId = Integer.parseInt(cmd[1]);
               if (orderId < 0) {
                  Prompt.printError("RequestFactory: Order id is not valid");
                  error = true;
                  break;
               }
               values = new CancelOrderValues(orderId);
               break;
            case "getpricehistory":
               if (cmd.length != 2) {
                  Prompt.printError("RequestFactory: Command is not defined for this number of arguments");
                  error = true;
                  break;
               }
               request.setOperation("getPriceHistory");
               values = new HistoryValues(cmd[1]);
               break;
            default:
               Prompt.printError("RequestFactory: Command is not defined for this input");
         }

         if (error)
            return null;

         request.setValues(values);
         return request;

      } catch (IllegalArgumentException e) {
         Prompt.printError("RequestFactory: " + e.getMessage());
         return null;
      }

   }

}
