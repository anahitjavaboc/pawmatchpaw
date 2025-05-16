package com.anahit.pawmatch.models;

public class Match {
    public String userId;
    public String petId;
    public long timestamp;

    public Match() {}
    public Match(String userId, String petId, long timestamp) {
        this.userId = userId;
        this.petId = petId;
        this.timestamp = timestamp;
    }
}