package com.yunhang.forum.service;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Comment;
import com.yunhang.forum.model.entity.Notification;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.AppContext;
import com.yunhang.forum.util.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SelfCommentNotificationTest {

  private static final class InMemoryDataLoader implements DataLoader {
    List<User> users = new ArrayList<>();
    List<Post> posts = new ArrayList<>();

    @Override public List<User> loadUsers() { return new ArrayList<>(users); }
    @Override public boolean saveUsers(List<User> users) { this.users = new ArrayList<>(users); return true; }
    @Override public List<Post> loadPosts() { return new ArrayList<>(posts); }
    @Override public boolean savePosts(List<Post> posts) { this.posts = new ArrayList<>(posts); return true; }
  }

  @AfterEach
  void cleanup() {
    UserSession.getInstance().clearSession();
  }

  @Test
  void selfCommentShouldNotCreateNotification() {
    InMemoryDataLoader loader = new InMemoryDataLoader();
    AppContext.setDataLoader(loader);

    Student me = new Student("24000001", "me", "pass");
    loader.users.add(me);

    // warm user cache
    new UserService(loader, null);

    UserSession.getInstance().startSession(me);

    Post p = new Post("t", "c", me.getStudentID(), PostCategory.LEARNING);
    PostService.getInstance().createPost(p);

    Comment c = new Comment(p.getPostId(), me.getStudentID(), null, "self");
    PostService.getInstance().addComment(p.getPostId(), c);

    // reload user and verify no notifications were persisted
    List<User> reloaded = loader.loadUsers();
    assertEquals(0, reloaded.getFirst().getNotifications().size());
  }
}

