package com.yunhang.forum.controller;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.service.strategy.PostSortStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * 简易 PostController，与类图一致：依赖 DataLoader 与 PostSortStrategy。
 */
public class PostController {
    private final DataLoader dataLoader;
    private PostSortStrategy sortStrategy;

    public PostController(DataLoader dataLoader, PostSortStrategy sortStrategy) {
        this.dataLoader = dataLoader;
        this.sortStrategy = sortStrategy;
    }

    public void setSortStrategy(PostSortStrategy strategy) {
        this.sortStrategy = strategy;
    }

    public List<Post> loadAllPosts() {
        List<Post> posts = new ArrayList<>(dataLoader.loadPosts());
        if (sortStrategy != null) {
            sortStrategy.sort(posts);
        }
        return posts;
    }
}

