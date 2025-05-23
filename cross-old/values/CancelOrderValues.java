package cross.values;

public class CancelOrderValues extends Values {

   private int orderId;

   public CancelOrderValues (int orderId) {
      this.orderId = orderId;
   }

   public int getOrderId() {
      return orderId;
   }

   // public void setOrderId(int orderId) {}
   
}
