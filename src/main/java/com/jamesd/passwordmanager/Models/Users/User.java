package com.jamesd.passwordmanager.Models.Users;

import java.util.UUID;

/**
 * Models a user of this application from the master database
 */
public class User {

    private String id;
    private String username;
    private String email;
    private String encryptedPass;
    private String reminderTimePeriod;

    /**
     * Default constructor
     */
    public User() {
        this.id = UUID.randomUUID().toString();
        this.username = "DemoUser";
        this.email = "totally@notreal.net";
        this.encryptedPass = "encrypted_password";
        this.reminderTimePeriod = "1 month";
    }

    /**
     * Constructor which takes a user's username, email and encrypted password as parameters
     * @param username Username String
     * @param email Email String
     * @param encryptedPass Encrypted password String
     */
    public User(String username, String email, String encryptedPass) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.encryptedPass = encryptedPass;
        this.reminderTimePeriod = "6 months";
    }

    /**
     * Constructor which takes a user's ID from the database, username, email, encrypted password and the time period
     * between reminders as parameters
     * @param id ID from the database String
     * @param username Username String
     * @param email Email String
     * @param encryptedPass Encrypted password String
     * @param reminderTimePeriod Time between reminders String
     */
    public User(String id, String username, String email, String encryptedPass, String reminderTimePeriod) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.encryptedPass = encryptedPass;
        this.reminderTimePeriod = reminderTimePeriod;
    }

    /**
     * Retrieves the user ID
     * @return ID String
     */
    public String getId() { return this.id; }

    /**
     * Retrieves the username
     * @return Username String
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Retrieves the user's email
     * @return Email String
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Retrieves the encrypted password
     * @return Encrypted password String
     */
    public String getEncryptedPassword() {
        return this.encryptedPass;
    }

    /**
     * Sets the user's ID
     * @param id ID String
     */
    public void setId(String id) { this.id = id; }

    /**
     * Sets the user's username
     * @param username Username String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the user's email
     * @param email Email String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's encrypted password
     * @param encryptedPassword Encrypted password String
     */
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPass = encryptedPassword;
    }

    /**
     * Gets the time between reminders
     * @return Time between reminders String
     */
    public String getReminderTimePeriod() {
        return reminderTimePeriod;
    }

    /**
     * Sets the time between reminders
     * @param reminderTimePeriod Time between reminders String
     */
    public void setReminderTimePeriod(String reminderTimePeriod) {
        this.reminderTimePeriod = reminderTimePeriod;
    }
}
