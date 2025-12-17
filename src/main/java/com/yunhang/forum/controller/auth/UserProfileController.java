package com.yunhang.forum.controller.auth;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.util.UserService;
import com.yunhang.forum.util.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;

public class UserProfileController {

    // FXML 元素
    @FXML private Label welcomeLabel;
    @FXML private Label studentIdLabel;
    @FXML private ListView<String> postListView;

    private final UserService userService = new UserService();

    @FXML
    protected void initialize() {
        User currentUser = UserSession.getInstance().getCurrentUser();

        if (currentUser != null) {
            // 1. 显示用户基本信息
            welcomeLabel.setText("欢迎，" + currentUser.getNickname() + "!");
            studentIdLabel.setText("学号: " + currentUser.getStudentID());
            loadUserPosts(currentUser);
        } else {
            ViewManager.switchScene("auth/Login.fxml");
        }
    }

    private void loadUserPosts(User user) {
        List<Post> posts = userService.getUserPosts(user.getStudentID());

        if (posts != null && !posts.isEmpty()) {
            for (Post post : posts) {
                postListView.getItems().add(post.getTitle() + " (" + post.getPublishTime() + ")");
            }
        } else {
            postListView.getItems().add("您还没有发布任何帖子‍哦，快发个新帖一起玩吧😋");
        }
    }
    @FXML
    protected void handleLogoutAction() {
        UserSession.getInstance().endSession();
        ViewManager.switchScene("auth/Login.fxml");
    }

}
