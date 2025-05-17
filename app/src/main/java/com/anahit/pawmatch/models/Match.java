package com.anahit.pawmatch.models;

public class Match {
    private String matchId;
    private String petName;
    private String ownerName;
    private String otherUserId;
    private String petImageUrl;

    public Match(String matchId, String petName, String ownerName, String otherUserId, String petImageUrl) {
        this.matchId = matchId;
        this.petName = petName;
        this.ownerName = ownerName;
        this.otherUserId = otherUserId;
        this.petImageUrl = petImageUrl;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getPetName() {
        return petName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public String getPetImageUrl() {
        return petImageUrl;
    }

    public void setPetImageUrl(String petImageUrl) {
        this.petImageUrl = petImageUrl;
    }
}