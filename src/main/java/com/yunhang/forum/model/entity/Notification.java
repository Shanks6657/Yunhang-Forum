package com.yunhang.forum.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

/** 通知实体：由事件转换而来，推送给用户。 */
public class Notification {
    private final String id;
    private final String title;
    private final String content;
    private boolean read;
    private final LocalDateTime time;

    public Notification(String title, String content) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.read = false;
        this.time = LocalDateTime.now();
    }

    public static Notification fromEvent(Event event) {
        String title = switch (event.getType()) {
            case COMMENT_CREATED -> "新评论提醒";
            case REPLY_CREATED -> "新回复提醒";
        };
        String content = event.getMessage();
        return new Notification(title, content);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isRead() { return read; }
    public void markRead() { this.read = true; }
    public LocalDateTime getTime() { return time; }
}

