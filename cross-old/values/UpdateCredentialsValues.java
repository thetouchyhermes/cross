package cross.values;

public class UpdateCredentialsValues extends Values {
   
   private String username;
   private String old_password;
   private String new_password;

   public UpdateCredentialsValues(String username, String old_password, String new_password) {
      this.username = username;
      this.old_password = old_password;
      this.new_password = new_password;
   }

   public String getUsername() {
      return username;
   }

   // public void setUsername(String username) {}

   public String getOldPassword() {
      return old_password;
   }

   // public void setPassword(String password) {}

   public String getNewPassword() {
      return new_password;
   }

   // public void setPassword(String password) {}

}
