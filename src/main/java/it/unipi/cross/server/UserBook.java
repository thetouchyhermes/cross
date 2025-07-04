package it.unipi.cross.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unipi.cross.data.User;
import it.unipi.cross.json.MessageResponse;

/**
 * Principal structure of user management of the app. Contains all user
 * management methods, including registration, update credentials, login, logout
 * and persistence starting data structures
 **/
public class UserBook {

   private final Map<String, User> userMap = new ConcurrentHashMap<>();

   public UserBook(List<User> users) {
      if (users != null && !users.isEmpty()) {
         for (User user : users) {
            userMap.put(user.getUsername(), user);
         }
      }
   }

   public synchronized MessageResponse register(String username, String password) {
      // username and password must be already cleared from null or isEmpty cases
      if (userMap.containsKey(username))
         return new MessageResponse(102, "username not available");

      userMap.put(username, new User(username, password));
      return new MessageResponse(100, "OK");
   }

   public synchronized MessageResponse login(String username, String password) {
      // username must be already cleared from null or isEmpty cases
      User user = userMap.get(username);
      if (user == null) {
         return new MessageResponse(101, "non existent username");
      }
      if (!user.getPassword().equals(password)) {
         return new MessageResponse(101, "username/password mismatch");
      }
      if (user.isLogged()) {
         return new MessageResponse(102, "user already logged in");
      }

      user.login();
      return new MessageResponse(100, "OK");
   }

   public synchronized MessageResponse logout(String username) {
      // username must be already cleared from null or isEmpty cases
      User user = userMap.get(username);

      user.logout();
      return new MessageResponse(100, "OK");
   }

   public synchronized MessageResponse updateCredentials(String username, String oldPassword, String newPassword) {
      // username and passwords must be already cleared from null or isEmpty cases
      User user = userMap.get(username);
      if (user == null) {
         return new MessageResponse(102, "non existent username");
      }
      if (!user.getPassword().equals(oldPassword)) {
         return new MessageResponse(102, "username/password mismatch");
      }
      if (newPassword.equals(oldPassword)) {
         return new MessageResponse(103, "new password equal to old one");
      }
      if (user.isLogged()) {
         return new MessageResponse(104, "user currently logged in");
      }

      user.setPassword(newPassword);
      return new MessageResponse(100, "OK");
   }

   public List<User> getUserList() {
      return new ArrayList<>(userMap.values());
   }

}
