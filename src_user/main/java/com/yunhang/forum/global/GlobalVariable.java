package com.yunhang.forum.global;

import com.yunhang.forum.user.*;
import java.util.HashMap;
import java.util.Map;

public class GlobalVariable {
    public static Map<String, User> userMap = new HashMap<>();
    public static Map<String, Student> studentMap = new HashMap<>();
    public static Map<String, Admin> adminMap = new HashMap<>();
    public static Map<String,Report> reportMap = new HashMap<>();

}
