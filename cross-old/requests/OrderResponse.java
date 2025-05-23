package cross.requests;

import com.google.gson.Gson;

public class OrderResponse extends Response {
   
   private int orderId;

   public OrderResponse(String s) {
      Gson gson = new Gson();
      OrderResponse r = gson.fromJson(s, OrderResponse.class);
      
      this.orderId = r.getOrderId();
   }

   public OrderResponse() {}

   public int getOrderId() {
      return orderId;
   }

   public void setOrderId(int orderId) {
      this.orderId = orderId;
   }

}
