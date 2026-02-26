package com.hotel.model;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String role;
    private String fullName;
    private String email;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    public User() {}
    public User(int userId, String username, String role, String fullName, String email, boolean isActive) {
        this.userId = userId; this.username = username; this.role = role;
        this.fullName = fullName; this.email = email; this.isActive = isActive;
    }

    public int getUserId()                      { return userId; }
    public void setUserId(int v)                { userId = v; }
    public String getUsername()                 { return username; }
    public void setUsername(String v)           { username = v; }
    public String getPasswordHash()             { return passwordHash; }
    public void setPasswordHash(String v)       { passwordHash = v; }
    public String getRole()                     { return role; }
    public void setRole(String v)               { role = v; }
    public String getFullName()                 { return fullName; }
    public void setFullName(String v)           { fullName = v; }
    public String getEmail()                    { return email; }
    public void setEmail(String v)              { email = v; }
    public boolean isActive()                   { return isActive; }
    public void setActive(boolean v)            { isActive = v; }
    public Timestamp getCreatedAt()             { return createdAt; }
    public void setCreatedAt(Timestamp v)       { createdAt = v; }
    public Timestamp getLastLogin()             { return lastLogin; }
    public void setLastLogin(Timestamp v)       { lastLogin = v; }
    public boolean isAdmin()                    { return "ADMIN".equals(role); }
    public boolean isReceptionist()             { return "RECEPTIONIST".equals(role); }
}