package com.jamesd.passwordmanager.Models;

import java.util.UUID;

public class User {

    private String id;
    private String username;
    private String email;
    private String encryptedPass;
    private String reminderTimePeriod;

    public User() {
        this.id = UUID.randomUUID().toString();
        this.username = "DemoUser";
        this.email = "totally@notreal.net";
        this.encryptedPass = "encrypted_password";
        this.reminderTimePeriod = "1 month";
    }

    public User(String username, String email, String encryptedPass) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.encryptedPass = encryptedPass;
        this.reminderTimePeriod = "6 months";
    }

    public User(String id, String username, String email, String encryptedPass, String reminderTimePeriod) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.encryptedPass = encryptedPass;
        this.reminderTimePeriod = reminderTimePeriod;
    }

    public String getId() { return this.id; }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getEncryptedPassword() {
        return this.encryptedPass;
    }

    public void setId(String id) { this.id = id; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPass = encryptedPassword;
    }

    public String getReminderTimePeriod() {
        return reminderTimePeriod;
    }

    public void setReminderTimePeriod(String reminderTimePeriod) {
        this.reminderTimePeriod = reminderTimePeriod;
    }
}
