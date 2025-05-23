package cross.values;

public class CredentialsValues extends Values {
   
   private String username;
   private String password;

   public CredentialsValues(String username, String password) {
      this.username = username;
      this.password = password;
   }

   public String getUsername() {
      return username;
   }

   //public void setUsername(String username) {}

   public String getPassword() {
      return password;
   }

   // public void setPassword(String password) {}

}
