package it.unipi.cross.persistence;

import java.io.File;
import java.io.IOException;
import java.util.List;

import it.unipi.cross.data.User;

/**
 * Handles persistence operations for {@link User} objects using a JSON file.
 * <p>
 * This class provides methods to load and save a list of users to a file,
 * utilizing the {@link JsonUtil} utility for serialization and deserialization.
 * </p>
 */
public class UserStore {

   public static List<User> loadUsers(File file) throws IOException {
      UserList userList = JsonUtil.readFromFile(file, UserList.class);
      return userList.users;
   }

   public static void saveUsers(List<User> users, File file) throws IOException {
      JsonUtil.writeToFile(file, new UserList(users));
   }

}
