package cross.requests;

public class ResponseFactory {
   
   public static Response create(Request request) {

      Response response = null;

      switch (request.getOperation()) {
         case "register":
         case "login":
         case "updateCredentials":
         case "cancelOrder":
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setResponse(100);
            messageResponse.setErrorMessage("tutto ok");
            return messageResponse;
         case "insertLimitOrder":
         case "insertStopOrder":
         case "insertMarketOrder":
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(1);
            return orderResponse;
         case "getPriceHistory":
            //response = new HistoryResponse();
            break;
         default:
            response = new Response();
      }

      return response;
   }
}
