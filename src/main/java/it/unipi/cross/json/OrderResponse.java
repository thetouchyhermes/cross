package it.unipi.cross.json;

public class OrderResponse extends Response{
   private int orderId;

   public OrderResponse(int orderId) {
      this.orderId = orderId;
   }

   public int getOrderId() {
      return orderId;
   }
}
