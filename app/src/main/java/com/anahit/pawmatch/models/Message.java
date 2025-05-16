package com.anahit.pawmatch.models;

public class Message {
    public String id;
    public String senderId;
    public String content;
    public long timestamp;

    public Message() {}
    public Message(String id, String senderId, String content, long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }
}