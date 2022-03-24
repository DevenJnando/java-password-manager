package com.jamesd.passwordmanager.Models.Passwords;

public class WebsitePasswordEntry extends PasswordEntry {

    private String siteUrl;
    private String masterUsername;
    private String passwordUsername;

    public WebsitePasswordEntry() {
        super();
    }

    public WebsitePasswordEntry(String id, String passwordName, String siteUrl, String masterUsername, String passwordUsername, String dateSet, String encryptedPassword) {
        super(id, passwordName, encryptedPassword, dateSet);
        this.siteUrl = siteUrl;
        this.masterUsername = masterUsername;
        this.passwordUsername = passwordUsername;
    }

    public WebsitePasswordEntry(String passwordName, String siteUrl, String masterUsername, String passwordUsername, String dateSet, String encryptedPassword) {
        super(passwordName, encryptedPassword, dateSet);
        this.siteUrl = siteUrl;
        this.masterUsername = masterUsername;
        this.passwordUsername = passwordUsername;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public String getPasswordUsername() {
        return passwordUsername;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    public void setPasswordUsername(String passwordUsername) {
        this.passwordUsername = passwordUsername;
    }
}
