package com.yunhang.forum.service;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Notification;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.util.AppContext;
import com.yunhang.forum.util.UserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationPersistenceTest {

  private static final class InMemoryDataLoader implements DataLoader {
    final AtomicInteger saveUsersCalls = new AtomicInteger();
    List<User> users = new ArrayList<>();

    @Override
    public List<User> loadUsers() {
      return new ArrayList<>(users);
    }

    @Override
    public boolean saveUsers(List<User> users) {
      saveUsersCalls.incrementAndGet();
      this.users = new ArrayList<>(users);
      return true;
    }

    @Override
    public List<com.yunhang.forum.model.entity.Post> loadPosts() {
      return new ArrayList<>();
    }

    @Override
    public boolean savePosts(List<com.yunhang.forum.model.entity.Post> posts) {
      return true;
    }
  }

  @Test
  void sendNotificationShouldPersist() {
    InMemoryDataLoader loader = new InMemoryDataLoader();
    AppContext.setDataLoader(loader);

    // prepare user list
    Student b = new Student("24000002", "b", "pass");
    loader.users.add(b);

    UserService userService = new UserService(loader, null);

    boolean ok = userService.sendNotification("24000002", new Notification("t", "hello"));

    assertTrue(ok);
    assertTrue(loader.saveUsersCalls.get() >= 1);

    List<User> reloaded = loader.loadUsers();
    assertEquals(1, reloaded.getFirst().getNotifications().size());
  }
}

