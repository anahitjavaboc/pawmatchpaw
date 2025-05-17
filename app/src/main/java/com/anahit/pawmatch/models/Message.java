package com.anahit.pawmatch.models;

public class Message {
    private String messageId; // Added for message ID
    private String message;   // Content of the message
    private String sender;    // Sender ID
    private String receiver;  // Receiver ID
    private String timestamp; // Timestamp as a String

    public Message() {
    }

    // Constructor matching the one used in sendMessage()
    public Message(String messageId, String sender, String content, long timestamp) {
        this.messageId = messageId;
        this.sender = sender;
        this.message = content;
        this.timestamp = String.valueOf(timestamp); // Convert long to String
    }

    // Constructor with receiverId
    public Message(String messageId, String sender, String receiver, String content, String timestamp) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.message = content;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Methods required by MessageAdapter
    public String getContent() {
        return message;
    }

    public String getSenderId() {
        return sender;
    }
}