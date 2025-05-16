package com.anahit.pawmatch.models;

public class ChatRoom {
    public String id;
    public String user1Id;
    public String user2Id;
    public String lastMessage;
    public long timestamp;

    public ChatRoom() {}

    public ChatRoom(String id, String user1Id, String user2Id, String lastMessage, long timestamp) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getUser1Id() { return user1Id; }
    public String getUser2Id() { return user2Id; }
    public String getLastMessage() { return lastMessage; }
    public long getTimestamp() { return timestamp; }
}