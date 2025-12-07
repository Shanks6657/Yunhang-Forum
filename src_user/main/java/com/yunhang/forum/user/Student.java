package com.yunhang.forum.user;

import com.yunhang.forum.global.GlobalVariable;

public class Student extends User {

    public Student(String studentID, String nickname, String password) {
        super(studentID, nickname, password);
        GlobalVariable.studentMap.put(userID, this);
    }

    @Override
    public boolean login(String password) {
        boolean success = this.verifyPassword(password);
        if (success) {
            System.out.println("[" + this.nickname + "] 登录成功。");
            UserSession.getInstance().startSession(this);
        } else {
            System.out.println("登录失败：密码错误。");
        }
        return success;
    }
    public void reportUser(String targetId, String reason) {
        Report newReport = new Report(targetId, reason);
        GlobalVariable.reportMap.put(newReport.getTargetId(),newReport);
        if(GlobalVariable.reportMap.containsKey(newReport.getTargetId())) {
            System.out.println("举报成功");
        }
        else {
            System.out.println("举报失败，请重新尝试");
        }
    }
}
