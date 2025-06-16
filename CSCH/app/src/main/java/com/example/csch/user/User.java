package com.example.csch.user;

public class User {
    private int id;
    private String username;
    private String phoneNumber;
    private String dateCreated;
    private String password;
    private String avatar; // Thêm thuộc tính avatar

    public User(int id, String username, String phoneNumber, String dateCreated, String password, String avatar) {
        this.id = id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.dateCreated = dateCreated;
        this.password = password;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getPassword() {
        return password; // Phương thức getter cho password
    }

    public String getAvatar() {
        return avatar;
    }



}
