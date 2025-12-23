package com.yunhang.forum.service;

import com.yunhang.forum.dao.DataLoader;
import com.yunhang.forum.dao.impl.FileDataLoader;
import com.yunhang.forum.model.entity.GlobalVariables;
import com.yunhang.forum.model.entity.Student;
import com.yunhang.forum.model.entity.User;
import com.yunhang.forum.util.AppContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Local helper to seed several accounts with real hashed passwords.
 *
 * Run from IDE as a Java main.
 */
public class TestAccountSeeder {
  public static void main(String[] args) {
    DataLoader loader = new FileDataLoader();
    AppContext.setDataLoader(loader);

    // Load existing users first
    List<User> existing = loader.loadUsers();
    Map<String, User> bySid = new LinkedHashMap<>();
    for (User u : existing) {
      if (u != null && u.getStudentID() != null && !u.getStudentID().isBlank()) {
        bySid.put(u.getStudentID(), u);
      }
    }

    // Seed accounts (studentId -> nickname) with password = pass
    seed(bySid, "24000001", "alice", "pass");
    seed(bySid, "24000002", "bob", "pass");
    seed(bySid, "24000003", "carol", "pass");

    // Persist
    loader.saveUsers(new ArrayList<>(bySid.values()));

    System.out.println("Seeded accounts: 24000001/24000002/24000003 (password: pass)");
  }

  private static void seed(Map<String, User> bySid, String sid, String nickname, String password) {
    if (bySid.containsKey(sid)) {
      return;
    }
    Student s = new Student(sid, nickname, password);
    // ensure global map doesn't pollute seeding
    GlobalVariables.userMap.put(sid, s);
    bySid.put(sid, s);
  }
}

