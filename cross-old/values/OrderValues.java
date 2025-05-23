package cross.values;

public class OrderValues extends Values {
   
   private String type;
   private int size;

   public OrderValues (String type, int size) throws IllegalArgumentException {
      String tempType = type.toLowerCase();
      switch(tempType) {
         case "ask":
         case "bid":
            this.type = tempType;
            break;
         default:
            throw new IllegalArgumentException("Order type "+ type + " is not valid");
      }
      this.size = size;
   }

   public String getType() {
      return type;
   }

   // public void setType(String type) {}

   public int getSize() {
      return size;
   }

   // public void setSize(int size) {}

}
