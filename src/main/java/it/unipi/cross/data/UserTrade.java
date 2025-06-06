package it.unipi.cross.data;

public class UserTrade extends Trade {
   
   private String username;

   public UserTrade(Order order, int size, int price) {
      super(order, size, price);
      this.username = order.getUsername();
   }

   public String getUsername() {
      return username;
   }

}
