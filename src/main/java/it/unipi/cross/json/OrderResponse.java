package it.unipi.cross.json;

/** Represents a response object for orderbook commands, returns order id **/
public class OrderResponse extends Response {
   private int orderId;

   public OrderResponse(int orderId) {
      this.orderId = orderId;
   }

   public int getOrderId() {
      return orderId;
   }
}
