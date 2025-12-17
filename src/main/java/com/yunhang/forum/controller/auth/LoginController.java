package com.yunhang.forum.controller.auth;

import com.yunhang.forum.util.UserService;
import com.yunhang.forum.util.ViewManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    // FXML注解用于将Java代码中的变量与FXML文件中定义的界面元素关联起来
    @FXML private TextField studentIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    // 依赖服务层
    private UserService userService = new UserService();

    @FXML
    protected void initialize() {
        // 可以在这里进行初始化工作，例如设置默认值
    }

    @FXML
    protected void handleLoginButtonAction() {
        String studentId = studentIdField.getText();
        String password = passwordField.getText();

        // 基础输入验证
        if (studentId.isEmpty() || password.isEmpty()) {
            messageLabel.setText("学号和密码不能为空哦😖");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (userService.login(studentId, password)) {
            messageLabel.setText("登录成功😋");
            messageLabel.setStyle("-fx-text-fill: black;");
            ViewManager.switchScene("auth/UserProfile.fxml");

        } else {
            messageLabel.setText("学号或密码错误☹️");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    protected void handleRegisterLinkAction() {
        // 跳转到注册界面
        ViewManager.switchScene("auth/Register.fxml");
    }
}
