package com.anahit.pawmatch.models;

public class User {
    public String id;
    public String name;
    public String email;

    public User() {}
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}