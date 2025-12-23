package com.yunhang.forum.model;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Comment;
import com.yunhang.forum.model.entity.GlobalVariables;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.enums.PostStatus;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.AppContext;
import com.yunhang.forum.util.UserService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationDeliveryTest {

  @Test
  void commentShouldNotifyCanonicalAuthorInstance() {
    GlobalVariables.userMap.clear();

    // in-memory persistence
    DataLoader loader = new DataLoader() {
      java.util.List<User> users = new ArrayList<>();
      java.util.List<Post> posts = new ArrayList<>();
      @Override public java.util.List<User> loadUsers() { return new ArrayList<>(users); }
      @Override public boolean saveUsers(java.util.List<User> users) { this.users = new ArrayList<>(users); return true; }
      @Override public java.util.List<Post> loadPosts() { return new ArrayList<>(posts); }
      @Override public boolean savePosts(java.util.List<Post> posts) { this.posts = new ArrayList<>(posts); return true; }
    };
    AppContext.setDataLoader(loader);

    Student author = new Student("24000001", "author", "pass");
    Student commenter = new Student("24000002", "commenter", "pass");

    loader.saveUsers(java.util.List.of(author, commenter));
    new UserService(loader, null);

    Post post = new Post("t", "c", author.getStudentID(), PostCategory.LEARNING);
    post.safeTransitionTo(PostStatus.PUBLISHED);
    PostService.getInstance().createPost(post);

    int before = loader.loadUsers().stream().filter(u -> u.getStudentID().equals(author.getStudentID())).findFirst().orElseThrow().getNotifications().size();

    PostService.getInstance().addComment(post.getPostId(), new Comment(post.getPostId(), commenter.getStudentID(), null, "hello"));

    int after = loader.loadUsers().stream().filter(u -> u.getStudentID().equals(author.getStudentID())).findFirst().orElseThrow().getNotifications().size();
    assertEquals(before + 1, after);
  }
}
