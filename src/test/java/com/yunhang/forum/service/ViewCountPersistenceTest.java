package com.yunhang.forum.service;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.AppContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ViewCountPersistenceTest {

  private static final class InMemoryLoader implements DataLoader {
    final AtomicInteger savePostsCalls = new AtomicInteger();
    List<Post> posts = new ArrayList<>();

    @Override
    public List<com.yunhang.forum.model.entity.User> loadUsers() {
      return new ArrayList<>();
    }

    @Override
    public boolean saveUsers(List<com.yunhang.forum.model.entity.User> users) {
      return true;
    }

    @Override
    public List<Post> loadPosts() {
      return new ArrayList<>(posts);
    }

    @Override
    public boolean savePosts(List<Post> posts) {
      savePostsCalls.incrementAndGet();
      this.posts = new ArrayList<>(posts);
      return true;
    }
  }

  @Test
  void incrementViewShouldPersist() {
    InMemoryLoader loader = new InMemoryLoader();
    AppContext.setDataLoader(loader);

    Post p = new Post("t", "c", "24000001", PostCategory.LEARNING);
    PostService.getInstance().createPost(p);

    int before = p.getViewCount();
    int saveBefore = loader.savePostsCalls.get();

    int newCount = PostService.getInstance().incrementView(p.getPostId());

    assertEquals(before + 1, newCount);
    assertTrue(loader.savePostsCalls.get() > saveBefore, "incrementView should persist via savePosts");
  }
}

