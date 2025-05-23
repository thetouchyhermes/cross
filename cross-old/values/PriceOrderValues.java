package cross.values;

public class PriceOrderValues extends OrderValues {

   private int price;

   public PriceOrderValues (String type, int size, int price) throws IllegalArgumentException {
      super(type, size);
      this.price = price;
   }

   public int getPrice() {
      return price;
   }

   // public void setPrice(int price) {}

}
