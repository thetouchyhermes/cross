package cross.requests;

public class ResponseManager {

   public static boolean handle(Request request, Response response) {

      boolean success = true;

      if (response instanceof OrderResponse) {
         OrderResponse orderResponse = (OrderResponse) response;
         if (orderResponse.getOrderId() == -1) {
            // remove orderId
            System.out.println(orderResponse.getOrderId() + ": " + request.getOperation() + " could not be registered");
            success = false;
         } else
            System.out.println(request.getOperation() + ": Order correctly registered with id [" + orderResponse.getOrderId() + "]");
      } else if (response instanceof MessageResponse) {
         MessageResponse messageResponse = (MessageResponse) response;
         if (messageResponse.getResponse() != 100) {
            System.out.println(request.getOperation() + ": Error " + messageResponse.getResponse() + ". " + messageResponse.getErrorMessage());
            success = false;
         } else // remove getresponse
            System.out.println(messageResponse.getResponse() + ": " + request.getOperation() + " correctly registered");
      } else {
         System.out.println(response.toString());
      }

      return success;

   }
}
