// filepath: /Users/xigma/IdeaProjects/Yunhang-Forum/src/test/java/com/yunhang/forum/model/InteractionModuleTest.java
package com.yunhang.forum.model;

import com.yunhang.forum.dao.DataLoader;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 演示：当调用 post.addComment(comment) 时，自动为作者生成 Notification。
 */
public class InteractionModuleTest {

    static class DummyStudent extends Student {
        public DummyStudent(String studentID, String nickname, String password) {
            super(studentID, nickname, password);
        }
    }

    @Test
    public void testCommentTriggersNotification() {
        // Prepare in-memory persistence
        DataLoader loader = new DataLoader() {
            java.util.List<User> users = new ArrayList<>();
            java.util.List<Post> posts = new ArrayList<>();
            @Override public java.util.List<User> loadUsers() { return new ArrayList<>(users); }
            @Override public boolean saveUsers(java.util.List<User> users) { this.users = new ArrayList<>(users); return true; }
            @Override public java.util.List<Post> loadPosts() { return new ArrayList<>(posts); }
            @Override public boolean savePosts(java.util.List<Post> posts) { this.posts = new ArrayList<>(posts); return true; }
        };
        AppContext.setDataLoader(loader);

        // 准备用户与帖子
        DummyStudent author = new DummyStudent("20250001", "作者A", "pass");
        DummyStudent commenter = new DummyStudent("20250002", "评论者B", "pass");

        // Persist users
        java.util.List<User> initialUsers = new ArrayList<>();
        initialUsers.add(author);
        initialUsers.add(commenter);
        loader.saveUsers(initialUsers);

        // Warm user cache
        new UserService(loader, null);

        Post post = new Post("标题", "正文", author.getStudentID(), PostCategory.LEARNING);
        post.safeTransitionTo(PostStatus.PUBLISHED);
        PostService.getInstance().createPost(post);

        // 断言：初始无通知（从持久化侧读取，按 author studentID 定位）
        User persistedAuthor0 = loader.loadUsers().stream()
                .filter(u -> u.getStudentID().equals(author.getStudentID()))
                .findFirst().orElseThrow();
        assertEquals(0, persistedAuthor0.getNotifications().size());

        // 动作：发表评论（通过 PostService）
        com.yunhang.forum.model.entity.Comment c = new com.yunhang.forum.model.entity.Comment(post.getPostId(), commenter.getStudentID(), null, "支持一下！");
        PostService.getInstance().addComment(post.getPostId(), c);

        // 验证：作者收到通知（从持久化侧读取）
        User reloadedAuthor = loader.loadUsers().stream()
                .filter(u -> u.getStudentID().equals(author.getStudentID()))
                .findFirst().orElseThrow();
        assertEquals(1, reloadedAuthor.getNotifications().size());
        com.yunhang.forum.model.entity.Notification n = reloadedAuthor.getNotifications().get(0);
        assertEquals("新评论提醒", n.getTitle());
        assertNotNull(n.getContent());
        assertFalse(n.getContent().isBlank());
    }
}
