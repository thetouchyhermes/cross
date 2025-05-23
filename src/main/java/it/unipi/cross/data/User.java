package it.unipi.cross.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents a user with a username, password, and login status.
 * <p>
 * This class provides methods to access user credentials and manage the user's
 * login state.
 * </p>
 */
public class User {
   private String username;
   private String password;
   private boolean logged;

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

   public boolean isLogged() {
      return logged;
   }

   public void setLogged(boolean logged) {
      this.logged = logged;
   }

   @Override
   public String toString() {
      Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
      return gson.toJson(this);
   }
}
