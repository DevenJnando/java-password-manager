package com.jamesd.passwordmanager.Models;

public class WebsitePasswordEntry extends PasswordEntry {

    private String siteUrl;
    private String masterUsername;
    private String passwordUsername;
    private String dateSet;
    private String needsUpdatedMessage;

    public WebsitePasswordEntry() {
        super();
        this.needsUpdatedMessage = "";
    }

    public WebsitePasswordEntry(String passwordName, String siteUrl, String masterUsername, String passwordUsername, String dateSet, String encryptedPassword) {
        super(passwordName, encryptedPassword);
        this.siteUrl = siteUrl;
        this.masterUsername = masterUsername;
        this.passwordUsername = passwordUsername;
        this.dateSet = dateSet;
    }

    public String getSiteUrl() {
        return this.siteUrl;
    }

    public String getDateSet() {
        return this.dateSet;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public String getPasswordUsername() {
        return passwordUsername;
    }

    public String getNeedsUpdatedMessage() {
        return needsUpdatedMessage;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public void setDateSet(String dateSet) {
        this.dateSet = dateSet;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    public void setPasswordUsername(String passwordUsername) {
        this.passwordUsername = passwordUsername;
    }

    public void setNeedsUpdatedMessage(String needsUpdatedMessage) {
        this.needsUpdatedMessage = needsUpdatedMessage;
    }
}
