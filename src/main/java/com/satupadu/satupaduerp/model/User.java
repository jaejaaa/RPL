package com.satupadu.satupaduerp.model;

public class User {
    private int userId;
    private String username;
    private String fullName;
    private String roleName;

    public User(int userId, String username, String fullName, String roleName) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.roleName = roleName;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return fullName + " (" + roleName + ")";
    }
}
