package com.yunhang.forum.controller.post;

import com.yunhang.forum.model.entity.Comment;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.TaskRunner;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PostDetailController: 负责渲染帖子详情与评论列表。
 */
public class PostDetailController {
    private Post currentPost;

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label timeLabel;
    @FXML private Label contentLabel;
    @FXML private Label likesLabel;
    @FXML private VBox commentsContainer;
    @FXML private TextField commentInput;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        if (likesLabel != null) {
            likesLabel.setOnMouseClicked(e -> onToggleLike());
            likesLabel.setStyle(likesLabel.getStyle() + "; -fx-cursor: hand;");
        }
    }

    // 初始化数据：由列表页或路由传递进来
    public void initData(Post post) {
        this.currentPost = post;
        if (post == null) return;
        titleLabel.setText(post.getTitle());
        authorLabel.setText(post.getDisplayAuthor());
        timeLabel.setText(post.getFormattedPublishTime());
        contentLabel.setText(post.getFullContent());
        updateLikeUI();
        renderComments();
    }

    private void updateLikeUI() {
        if (currentPost == null || likesLabel == null) {
            return;
        }
        User currentUser = UserSession.getInstance().getCurrentUser();
        boolean liked = currentUser != null && currentPost.isLikedBy(currentUser.getStudentID());
        applyLikeUI(currentPost.getLikeCount(), liked);
    }

    private void applyLikeUI(int likeCount, boolean liked) {
        likesLabel.setText("♥ " + likeCount);
        String color = liked ? "#ff4757" : "#888888";
        likesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + "; -fx-cursor: hand;");
    }

    private void onToggleLike() {
        if (currentPost == null) {
            return;
        }

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            new Alert(Alert.AlertType.WARNING, "请先登录再点赞").showAndWait();
            return;
        }

        String userId = currentUser.getStudentID();
        String postId = currentPost.getPostId();

        TaskRunner.runAsync(() -> {
            PostService.LikeResult result = PostService.getInstance().toggleLike(postId, userId);
            TaskRunner.runOnUI(() -> applyLikeUI(result.likeCount(), result.liked()));
        });
    }

    private void renderComments() {
        commentsContainer.getChildren().clear();
        if (currentPost == null) return;
        List<Comment> comments = currentPost.getComments();
        for (Comment c : comments) {
            VBox box = buildCommentNode(c);
            commentsContainer.getChildren().add(box);
        }
    }

    private VBox buildCommentNode(Comment c) {
        Label author = new Label("@" + c.getAuthorId());
        author.getStyleClass().add("comment-author");
        Label content = new Label(c.getContent());
        content.setWrapText(true);
        Label time = new Label(c.getTime().format(TIME_FMT));
        time.getStyleClass().add("comment-time");

        VBox container = new VBox(2);
        container.getChildren().addAll(author, content, time);
        container.getStyleClass().add("comment-container");
        return container;
    }

    @FXML
    private void onSendComment() {
        if (currentPost == null) return;
        String text = commentInput.getText();
        if (text == null || text.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "评论内容不能为空").showAndWait();
            return;
        }

        String content = text.trim();
        User currentUser = UserSession.getInstance().getCurrentUser();
        String authorId = currentUser != null ? currentUser.getStudentID() : "anonymous";
        Comment inputComment = new Comment(currentPost.getPostId(), authorId, null, content);

        // Service 可能涉及 IO，放到虚拟线程执行
        TaskRunner.runAsync(() -> {
            Comment saved = PostService.getInstance().addComment(currentPost.getPostId(), inputComment);
            TaskRunner.runOnUI(() -> {
                if (saved != null) {
                    commentInput.clear();
                    commentsContainer.getChildren().add(buildCommentNode(saved));
                } else {
                    new Alert(Alert.AlertType.ERROR, "发送失败，请稍后再试").showAndWait();
                }
            });
        });
    }
}
