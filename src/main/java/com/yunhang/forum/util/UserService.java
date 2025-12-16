package com.yunhang.forum.util;

import com.yunhang.forum.model.entity.Post;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.session.UserSession;
import com.yunhang.forum.model.entity.GlobalVariables;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.*;

public class UserService {

    private static final String SENDER_EMAIL = "yunhang_forum@163.com";
    private static final String SENDER_PASSWORD = "KZv8JKBRwfm8Z8BK";
    private static final String SMTP_HOST = "smtp.163.com";
    private static final String SMTP_PORT = "465";
    private static final Map<String, String> verificationCache = new HashMap<>();
    private static final Random random = new Random();


    private User findUserById(String id) {
        for (User user : GlobalVariables.userMap.values()) {
            if (user.getStudentID().equals(id))
                return user;
        }
        return null;
    }


    public boolean login(String studentId, String password) {
        User user = findUserById(studentId);

        if (user == null) {
            return false;
        }
        if (user.verifyPassword(password)) {
            UserSession.getInstance().startSession(user);
            return true;
        } else {
            return false;
        }
    }


    public boolean registerStudent(String studentId, String nickname, String password) {
        if (isStudentIdExists(studentId)) {
            System.err.println("注册失败：学号已存在。");
            return false;
        }

        for (User user : GlobalVariables.userMap.values()) {
            if (nickname.equals(user.getNickname())) {
                System.err.println("注册失败：昵称已存在。");
                return false;
            }
        }

        Student newUser = new Student(studentId, nickname, password);
        GlobalVariables.userMap.put(studentId, newUser);
        System.out.println("新用户 [" + newUser.getNickname() + "] 注册成功。");
        return true;
    }

    /**
     * 发送验证码逻辑
     */
    public boolean sendVerificationCode(String email) {
        String code = generateCodeAndCache(email);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("【Yunhang-Forum】邮箱验证码");
            message.setText("您的注册验证码是：" + code + "，5分钟内有效。请勿泄露给他人。");

            Transport.send(message);
            System.out.println("🥰邮件发送成功到: " + email);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("😭邮件发送失败: " + e.getMessage());
            verificationCache.remove(email);
            return false;
        }
    }

    public boolean isVerificationCodeValid(String email, String code) {
        if (!verificationCache.containsKey(email)) {
            return false;
        }

        String storedCode = verificationCache.get(email);

        if (storedCode.equals(code)) {
            verificationCache.remove(email);
            return true;
        }

        return false;
    }


    public boolean isStudentIdExists(String studentId) {
        return findUserById(studentId) != null;
    }

    private String generateCodeAndCache(String email) {
        String code = String.format("%06d", random.nextInt(999999));
        verificationCache.put(email, code);
        return code;
    }
    public List<Post> getUserPosts(String studentId) {
        List<Post> userPosts = new ArrayList<>();
        User user = GlobalVariables.userMap.get(studentId);
        userPosts = user.getPublishedPosts();
        userPosts.sort((p1, p2) -> p2.getPublishTime().compareTo(p1.getPublishTime()));
        return userPosts;
    }
}
