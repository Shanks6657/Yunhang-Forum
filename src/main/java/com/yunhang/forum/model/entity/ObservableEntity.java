package com.yunhang.forum.model.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 抽象可观察实体。
 * 使用观察者模式维护一组关注该实体变化的用户（observers）。
 * 关键逻辑：当实体发生事件（如新增评论）时，调用 notifyObservers(event)
 * 将事件转换为 Notification 并分发给所有观察者。
 */
public abstract class ObservableEntity {
    private final List<User> observers = new ArrayList<>();

    public void addObserver(User user) {
        if (user == null) {
            return;
        }
        if (!observers.contains(user)) {
            observers.add(user);
        }
    }

    public List<User> getObservers() {
        return Collections.unmodifiableList(observers);
    }

    protected void notifyObservers(Event event) {
        if (event == null) {
            return;
        }
        Notification notification = Notification.fromEvent(event);
        for (User user : observers) {
            if (user == null) {
                continue;
            }
            // IMPORTANT: write to the canonical in-memory user instance (keyed by studentID)
            User canonical = GlobalVariables.userMap.get(user.getStudentID());
            if (canonical != null) {
                canonical.addNotification(notification);
            } else {
                user.addNotification(notification);
            }
        }
    }
}
