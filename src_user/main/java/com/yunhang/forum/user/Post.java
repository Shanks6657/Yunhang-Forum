package com.yunhang.forum.user;


public class Post {
    String id;
    String content;
    public Post(String content) { this.content = content; this.id = java.util.UUID.randomUUID().toString(); }
    @Override
    public String toString() { return "Post[" + id + "]: " + content; }
}

