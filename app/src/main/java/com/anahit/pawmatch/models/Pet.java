package com.anahit.pawmatch.models;

public class Pet {
    private String id;
    private String name;
    private int age;
    private String breed;
    private String ownerId;
    private String bio;
    private String imageUrl;

    public Pet() {
        // Default constructor required for Firebase
    }

    public Pet(String name, int age, String breed, String ownerId) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.ownerId = ownerId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}