package com.yunhang.forum.controller.auth;

import com.yunhang.forum.util.UserService;
import com.yunhang.forum.util.ViewManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class RegisterController {

    @FXML private TextField studentIdField;
    @FXML private TextField nicknameField;
    @FXML private TextField emailPrefixField;
    @FXML private TextField verificationCodeField;
    @FXML private Button sendCodeButton;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();
    private Timeline countdownTimeline;
    private int secondsRemaining = 60;
    private static final String REQUIRED_SUFFIX = "@buaa.edu.cn";

    @FXML
    protected void initialize() {
        // 初始化
    }

    @FXML
    protected void handleSendCodeAction() {
        String emailPrefix = emailPrefixField.getText().trim();

        if (emailPrefix.isEmpty()) {
            messageLabel.setText("邮箱前缀不能为空😌");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String email = emailPrefix + REQUIRED_SUFFIX;

        if (userService.sendVerificationCode(email)) {
            messageLabel.setText("验证码已发送到 " + email + ",请查收😝");
            messageLabel.setStyle("-fx-text-fill: green;");
            startCountdown();
        } else {
            messageLabel.setText("验证码发送失败，请稍后再试🫣");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void startCountdown() {
        secondsRemaining = 60;
        sendCodeButton.setDisable(true);
        sendCodeButton.setText("重新发送 (" + secondsRemaining + ")");

        countdownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    secondsRemaining--;
                    if (secondsRemaining > 0) {
                        sendCodeButton.setText("重新发送 (" + secondsRemaining + ")");
                    } else {
                        countdownTimeline.stop();
                        sendCodeButton.setDisable(false);
                        sendCodeButton.setText("发送验证码");
                    }
                })
        );
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void stopCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    @FXML
    protected void handleRegisterButtonAction() {
        String studentId = studentIdField.getText();
        String nickname = nicknameField.getText();
        String emailPrefix = emailPrefixField.getText().trim();
        String verificationCode = verificationCodeField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 拼接完整的邮箱
        String email = emailPrefix + REQUIRED_SUFFIX;

        // 1. 客户端输入校验
        if (studentId.isEmpty() || nickname.isEmpty() || emailPrefix.isEmpty() || verificationCode.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("所有字段都不能为空😒");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("两次输入的密码不一致😕");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // 2. 验证码校验
        if (!userService.isVerificationCodeValid(email, verificationCode)) {
            messageLabel.setText("验证码错误或已过期🙃");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // 3. 学号唯一性校验
        if (userService.isStudentIdExists(studentId)) {
            messageLabel.setText("该学号已被注册🤨");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // 4. 调用业务逻辑
        if (userService.registerStudent(studentId, nickname, password)) {
            messageLabel.setText("注册成功,正在跳转到登录页...😇");
            messageLabel.setStyle("-fx-text-fill: green;");
            stopCountdown();
            handleBackToLoginAction();
        } else {
            messageLabel.setText("注册失败,请检查输入🥹");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    protected void handleBackToLoginAction() {
        stopCountdown();
        ViewManager.switchScene("auth/Login.fxml");
    }
}
