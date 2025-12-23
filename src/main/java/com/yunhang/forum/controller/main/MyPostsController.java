package com.yunhang.forum.controller.main;

import com.yunhang.forum.model.entity.GlobalVariables;
import com.yunhang.forum.model.entity.Notification;
import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.service.strategy.PostService;
import com.yunhang.forum.util.LogUtil;
import com.yunhang.forum.util.TaskRunner;
import com.yunhang.forum.util.ViewManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * MyPostsController: center-view for "My Posts" inside MainLayout.
 * IMPORTANT: This view must NOT include any sidebar.
 */
public class MyPostsController {

  @FXML private Label welcomeLabel;
  @FXML private Label studentIdLabel;

  @FXML private ListView<Post> postListView;
  @FXML private ListView<Notification> notificationListView;

  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  @FXML
  public void initialize() {
    // Basic cell rendering
    if (postListView != null) {
      postListView.setCellFactory(lv -> new ListCell<>() {
        @Override
        protected void updateItem(Post item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || item == null) {
            setText(null);
          } else {
            setText(item.getTitle() + "  (" + item.getFormattedPublishTime() + ")");
          }
        }
      });

      // click -> detail
      postListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal != null) {
          ViewManager.showPostDetail(newVal);
          postListView.getSelectionModel().clearSelection();
        }
      });
    }

    if (notificationListView != null) {
      notificationListView.setCellFactory(lv -> new ListCell<>() {
        @Override
        protected void updateItem(Notification item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || item == null) {
            setText(null);
          } else {
            String time = item.getTime() != null ? item.getTime().format(TIME_FMT) : "";
            setText("[" + time + "] " + item.getContent());
          }
        }
      });
    }

    refreshData();
  }

  /**
   * Refresh posts + notifications from the latest session state.
   * Called on initialize and should be re-called whenever the view is shown.
   */
  public void refreshData() {
    User sessionUser = UserSession.getInstance().getCurrentUser();
    if (sessionUser == null) {
      LogUtil.warn("MyPostsController.refreshData: no current user");
      if (welcomeLabel != null) {
        welcomeLabel.setText("未登录");
      }
      if (postListView != null) {
        postListView.setItems(FXCollections.observableArrayList());
      }
      if (notificationListView != null) {
        notificationListView.setItems(FXCollections.observableArrayList());
      }
      return;
    }

    // Canonical user instance (prevents notification list from going out-of-sync)
    User currentUser = GlobalVariables.userMap.getOrDefault(sessionUser.getStudentID(), sessionUser);

    if (welcomeLabel != null) {
      welcomeLabel.setText("欢迎，" + currentUser.getNickname());
    }
    if (studentIdLabel != null) {
      studentIdLabel.setText("学号: " + currentUser.getStudentID());
    }

    // Use background thread for service calls
    TaskRunner.runAsync(() -> {
      try {
        List<Post> myPosts = PostService.getInstance().getPostsByAuthor(currentUser.getStudentID());
        myPosts.sort(Comparator.comparing(Post::getPublishTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        List<Notification> notifications = currentUser.getNotifications();
        // newest first
        notifications.sort(Comparator.comparing(Notification::getTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        TaskRunner.runOnUI(() -> {
          if (postListView != null) {
            postListView.setItems(FXCollections.observableArrayList(myPosts));
          }
          if (notificationListView != null) {
            notificationListView.setItems(FXCollections.observableArrayList(notifications));
          }
        });
      } catch (Exception e) {
        LogUtil.error("MyPostsController.refreshData failed", e);
      }
    });
  }
}
