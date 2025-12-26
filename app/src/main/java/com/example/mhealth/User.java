package com.example.mhealth;

public class User {
    private int id;
    private String name;
    private String email;
    private String status;
    private String role; // Add this field

    // Updated constructor
    public User(int id, String name, String email, String status, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.role = role;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}