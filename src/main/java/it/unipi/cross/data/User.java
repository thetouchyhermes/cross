package it.unipi.cross.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents a user with a username and password.
 * <p>
 * The {@code User} class provides methods to manage user credentials and login state.
 * The {@code logged} field is marked as {@code transient} and is not serialized.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     User user = new User("alice", "password123");
 *     user.login();
 *     System.out.println(user.isLogged()); // true
 * </pre>
 * </p>
 */
public class User {
   private String username;
   private String password;
   private transient boolean logged = false;

   public User(String username, String password) {
      this.username = username;
      this.password = password;
      this.logged = false;
   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String newPassword) {
      this.password = newPassword;
   }

   public boolean isLogged() {
      return logged;
   }

   public void login() {
      this.logged = true;
   }

   public void logout() {
      this.logged = false;
   }
   

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
}
