package it.unipi.cross.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unipi.cross.data.User;
import it.unipi.cross.util.Response;

public class UserBook {

   private final Map<String, User> userMap = new ConcurrentHashMap<>();

   /**
    * Constructs a UserBook by initializing the internal user map with the provided list of users.
    * Each user is added to the map using their username as the key.
    *
    * @param users the list of User objects to be added to the UserBook; if null or empty, no users are added
    */
   public UserBook(List<User> users) {
      if (users != null && !users.isEmpty()) {
         for (User user : users) {
            userMap.put(user.getUsername(), user);
         }
      }
   }

   public synchronized Response register(String username, String password) {
      // username and password must be already cleared from null or isEmpty cases
      if (userMap.containsKey(username))
         return new Response(102, "username not available");
      userMap.put(username, new User(username, password));
      return new Response(100, "OK");
   }
   
   public synchronized Response login(String username, String password) {
      // username must be already cleared from null or isEmpty cases
      User user = userMap.get(username);
      if (user == null)
         return new Response(101, "non existent username");
      if (user.isLogged())
         return new Response(102, "user already logged in");
      if (!user.getPassword().equals(password))
         return new Response(101, "username/password mismatch");

      user.login();
      return new Response(100, "OK");
   }

   public synchronized Response logout(String username) {
      // username must be already cleared from null or isEmpty cases
      User user = userMap.get(username);
      if (!user.isLogged())
         return new Response(101, "user not logged in");

      user.logout();
      return new Response(100, "OK");
   }
   
   public synchronized Response updateCredentials(String username, String oldPassword, String newPassword) {
      // username and passwords must be already cleared from null or isEmpty cases
      User user = userMap.get(username);
      if (user == null)
         return new Response(102, "non existent username");
      if (user.isLogged())
         return new Response(104, "user currently logged in");
      if (!user.getPassword().equals(oldPassword))
         return new Response(102, "username/password mismatch");
      if (oldPassword.equals(newPassword))
      return new Response(103, "new password equal to old one");

      return new Response(100, "OK");
   }

   /**
    * Returns a list of all users currently stored in the user map.
    *
    * @return a new {@link List} containing all {@link User} objects from the user map
    */
   public List<User> getUserList() {
      return new ArrayList<>(userMap.values());
   }
}
