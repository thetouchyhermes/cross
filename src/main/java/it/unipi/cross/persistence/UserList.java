package it.unipi.cross.persistence;

import java.util.List;

import it.unipi.cross.data.User;

public class UserList {
   public List<User> users;

   public UserList() {}

   public UserList(List<User> users) {
      this.users = users;
   }
}
