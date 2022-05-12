package com.jamesd.passwordmanager.Models.Users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Models a user of this application from the master database
 */
public class User {

    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private String encryptedPass;
    private String reminderTimePeriod;
    private boolean twoFactorEnabled;
    private List<HashMap<String, String>> recognisedDevices;

    /**
     * Default constructor
     */
    public User() {
        this.id = UUID.randomUUID().toString();
        this.username = "DemoUser";
        this.email = "totally@notreal.net";
        this.phoneNumber = "07123456789";
        this.encryptedPass = "encrypted_password";
        this.reminderTimePeriod = "1 month";
        this.twoFactorEnabled = false;
        this.recognisedDevices = new ArrayList<>();
    }

    /**
     * Constructor which takes a user's username, email and encrypted password as parameters
     * @param username Username String
     * @param email Email String
     * @param encryptedPass Encrypted password String
     * @param recognisedDevices List of recognisedDevices unique to this user
     */
    public User(String username, String email, String phoneNumber, String encryptedPass, List<HashMap<String, String>> recognisedDevices) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.encryptedPass = encryptedPass;
        this.reminderTimePeriod = "6 months";
        this.twoFactorEnabled = false;
        this.recognisedDevices = recognisedDevices;
    }

    /**
     * Constructor which takes a user's ID from the database, username, email, encrypted password and the time period
     * between reminders as parameters
     * @param id ID from the database String
     * @param username Username String
     * @param email Email String
     * @param encryptedPass Encrypted password String
     * @param reminderTimePeriod Time between reminders String
     * @param recognisedDevices List of recognisedDevices unique to this user
     */
    public User(String id, String username, String email, String phoneNumber, String encryptedPass, String reminderTimePeriod,
                List<HashMap<String, String>> recognisedDevices) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.encryptedPass = encryptedPass;
        this.reminderTimePeriod = reminderTimePeriod;
        this.twoFactorEnabled = false;
        this.recognisedDevices = recognisedDevices;
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

    /**
     * Retrieves the list of all recognised devices unique to this user
     * @return List of recognised devices
     */
    public List<HashMap<String, String>> getRecognisedDevices() {
        return recognisedDevices;
    }

    /**
     * Sets the list of recognised devices unique to this user
     * @param recognisedDevices List of recognised devices
     */
    public void setRecognisedDevices(List<HashMap<String, String>> recognisedDevices) {
        this.recognisedDevices = recognisedDevices;
    }

    /**
     * Gets the flag which shows if a user's two-factor authentication is enabled or not
     * @return True if enabled, else false
     */
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    /**
     * Sets the flag which shows if a user's two-factor authentication is enabled or not
     * @param twoFactorEnabled True if enabled, else false
     */
    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    /**
     * Retrieves the user's phone number
     * @return Phone number String
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number
     * @param phoneNumber Sets the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
