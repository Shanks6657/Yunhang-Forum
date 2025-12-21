package com.yunhang.forum.controller.auth;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.enums.PostCategory;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.DateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.PixelWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * 帖子卡片控制器 - 控制单个帖子卡片的显示
 */
public class PostItemController {

  @FXML
  private HBox rootContainer;

  @FXML
  private ImageView avatarImageView;

  @FXML
  private Label titleLabel;

  @FXML
  private Label summaryLabel;

  @FXML
  private Label categoryLabel;

  @FXML
  private Label timeLabel;

  @FXML
  private Label likesLabel;

  @FXML
  private Label commentsLabel;

  @FXML
  private Label viewsLabel;

  @FXML
  private VBox rightContainer;

  @FXML
  private HBox tagContainer;

  private static final int AVATAR_SIZE = 50;

  private Post currentPost;

  /**
   * 初始化方法
   */
  @FXML
  public void initialize() {
    // 设置头像为圆形
    avatarImageView.setClip(new Circle(AVATAR_SIZE / 2.0, AVATAR_SIZE / 2.0, AVATAR_SIZE / 2.0));
    avatarImageView.setImage(createPlaceholderAvatar(AVATAR_SIZE));

    // 点赞点击事件
    if (likesLabel != null) {
      likesLabel.setOnMouseClicked(e -> onToggleLike());
      likesLabel.setStyle(likesLabel.getStyle() + "; -fx-cursor: hand;");
    }
  }

  private static Image createPlaceholderAvatar(int size) {
    WritableImage image = new WritableImage(size, size);
    PixelWriter writer = image.getPixelWriter();
    Color color = Color.web("#CCCCCC");
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        writer.setColor(x, y, color);
      }
    }
    return image;
  }

  /**
   * 设置帖子数据到UI组件
   */
  public void setPostData(Post post) {
    if (post == null) {
      return;
    }

    this.currentPost = post;

    // 设置标题
    titleLabel.setText(post.getTitle());

    // 设置内容摘要（截取前50字符）
    String content = post.getContent();
    if (content != null && content.length() > 50) {
      summaryLabel.setText(content.substring(0, 50) + "...");
    } else {
      summaryLabel.setText(content != null ? content : "");
    }

    // 设置分类标签
    setCategoryTag(post.getCategory());

    // 设置时间
    timeLabel.setText(DateUtil.getRelativeTime(post.getPublishTime()));

    // 设置点赞数
    updateLikeUI(post);

    // 设置评论数
    commentsLabel.setText("💬 " + post.getCommentCount());

    // 设置浏览数
    viewsLabel.setText("👁 " + post.getViewCount());

    // 设置作者头像（这里简化处理，实际应该根据作者ID加载）
    setAuthorAvatar(post.getAuthorId(), post.isAnonymous());
  }

  /**
   * 设置分类标签样式（根据分类动态设置样式）
   */
  private void setCategoryTag(PostCategory category) {
    if (category == null) {
      categoryLabel.setText("未分类");
      categoryLabel.setStyle(getTagStyle("#9e9e9e", "#ffffff"));
      return;
    }

    categoryLabel.setText(category.getCategoryName());

    // 根据不同的分类设置不同的样式
    switch (category) {
      case LEARNING:
        categoryLabel.setStyle(getTagStyle("#e3f2fd", "#1976d2"));
        break;
      case CAMPUS_LIFE:
        categoryLabel.setStyle(getTagStyle("#f3e5f5", "#7b1fa2"));
        break;
      case SECOND_HAND:
        categoryLabel.setStyle(getTagStyle("#fff3e0", "#ef6c00"));
        break;
      case ACTIVITY:
        categoryLabel.setStyle(getTagStyle("#e8f5e9", "#2e7d32"));
        break;
      case QNA:
        categoryLabel.setStyle(getTagStyle("#fff8e1", "#ff8f00"));
        break;
      case EMPLOYMENT:
        categoryLabel.setStyle(getTagStyle("#f1f8e9", "#558b2f"));
        break;
      case ANNOUNCEMENT:
        categoryLabel.setStyle(getTagStyle("#ffebee", "#c62828"));
        break;
      default:
        categoryLabel.setStyle(getTagStyle("#f5f5f5", "#616161"));
        break;
    }
  }

  /**
   * 获取标签样式
   */
  private String getTagStyle(String backgroundColor, String textColor) {
    return String.format(
        "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-background-radius: 4; " +
            "-fx-padding: 2 8 2 8; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold;",
        backgroundColor, textColor
    );
  }

  /**
   * 设置作者头像
   */
  private void setAuthorAvatar(String authorId, boolean isAnonymous) {
    // Phase 2：暂无用户头像资源，统一使用占位图即可
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

    // 这里用 studentID 作为点赞用户标识（与 Comment/Reply 的 authorId 语义保持一致）
    String userId = currentUser.getStudentID();
    String postId = currentPost.getPostId();

    new Thread(() -> {
      PostService.LikeResult result = PostService.getInstance().toggleLike(postId, userId);
      Platform.runLater(() -> {
        applyLikeUI(result.likeCount(), result.liked());
      });
    }).start();
  }

  private void updateLikeUI(Post post) {
    User currentUser = UserSession.getInstance().getCurrentUser();
    boolean liked = false;
    if (currentUser != null) {
      liked = post.isLikedBy(currentUser.getStudentID());
    }
    applyLikeUI(post.getLikeCount(), liked);
  }

  private void applyLikeUI(int likeCount, boolean liked) {
    likesLabel.setText("♥ " + likeCount);

    // 已点赞：保持原红色；未点赞：复用项目已有的灰色（#888888）
    String color = liked ? "#ff4757" : "#888888";
    likesLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + "; -fx-cursor: hand;");
  }

  /**
   * 获取根容器（用于ListView的Cell）
   */
  public HBox getRootContainer() {
    return rootContainer;
  }
}
