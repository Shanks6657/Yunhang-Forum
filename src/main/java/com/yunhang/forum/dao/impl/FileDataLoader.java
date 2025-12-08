package com.yunhang.forum.dao.impl;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import java.util.List;

/**
 * 与类图命名一致的 DataLoader 实现，内部委托 JsonDataLoader。
 */
public class FileDataLoader implements DataLoader {
    private final JsonDataLoader delegate = new JsonDataLoader();

    @Override
    public List<User> loadUsers() {
        return delegate.loadUsers();
    }

    @Override
    public boolean saveUsers(List<User> users) {
        return delegate.saveUsers(users);
    }

    @Override
    public List<Post> loadPosts() {
        return delegate.loadPosts();
    }

    @Override
    public boolean savePosts(List<Post> posts) {
        return delegate.savePosts(posts);
    }
}

