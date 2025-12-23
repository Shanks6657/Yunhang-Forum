package com.yunhang.forum.controller.auth;

import com.yunhang.forum.util.UserService;
import com.yunhang.forum.util.ResourcePaths;
import com.yunhang.forum.util.ViewManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    // FXML注解用于将Java代码中的变量与FXML文件中定义的界面元素关联起来
    @FXML private TextField studentIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    // 依赖服务层
    private final UserService userService = new UserService();

    @FXML
    protected void initialize() {
        // 可以在这里进行初始化工作，例如设置默认值
    }

    private void setMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().removeAll("error", "success");
        messageLabel.getStyleClass().add(success ? "success" : "error");
    }

    @FXML
    protected void handleLoginButtonAction() {
        String studentId = studentIdField.getText();
        String password = passwordField.getText();

        // 基础输入验证
        if (studentId.isEmpty() || password.isEmpty()) {
            setMessage("学号和密码不能为空", false);
            return;
        }

        if (userService.login(studentId, password)) {
            messageLabel.getStyleClass().removeAll("error", "success");
            messageLabel.setText("登录成功");

            // IMPORTANT: enter the main shell (MainLayout with sidebar/header)
            ViewManager.switchScene(ResourcePaths.FXML_MAIN_LAYOUT);

        } else {
            setMessage("学号或密码错误", false);
        }
    }

    @FXML
    protected void handleRegisterLinkAction() {
        // 跳转到注册界面
        ViewManager.switchScene(ResourcePaths.FXML_AUTH_REGISTER);
    }
}
