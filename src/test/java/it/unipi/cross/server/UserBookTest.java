package it.unipi.cross.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.unipi.cross.data.User;

class UserBookTest {

   @Test
   void testGetUserListReturnsAllUsersOnInit() {
      User user1 = new User("alice", "pass1");
      User user2 = new User("bob", "pass2");
      UserBook userBook = new UserBook(Arrays.asList(user1, user2));

      List<User> users = userBook.getUserList();
      assertEquals(2, users.size());
      assertTrue(users.contains(user1));
      assertTrue(users.contains(user2));
   }

   @Test
   void testGetUserListReturnsEmptyListWhenNoUsers() {
      UserBook userBook = new UserBook(Collections.emptyList());
      List<User> users = userBook.getUserList();
      assertTrue(users.isEmpty());
   }

   @Test
   void testGetUserListAfterRegister() {
      UserBook userBook = new UserBook(Collections.emptyList());
      userBook.register("charlie", "pass3");
      List<User> users = userBook.getUserList();
      assertEquals(1, users.size());
      assertEquals("charlie", users.get(0).getUsername());
   }

   @Test
   void testGetUserListReturnsCopy() {
      User user1 = new User("dave", "pass4");
      UserBook userBook = new UserBook(Collections.singletonList(user1));
      List<User> users = userBook.getUserList();
      users.clear();
      // Internal user list should not be affected
      List<User> usersAfter = userBook.getUserList();
      assertEquals(1, usersAfter.size());
      assertEquals("dave", usersAfter.get(0).getUsername());
   }
}