package com.jamesd.passwordmanager.Models.Passwords;

/**
 * Class which models a website password entry from the password database
 */
public class WebsitePasswordEntry extends PasswordEntry {

    private String siteUrl;
    private String masterUsername;
    private String passwordUsername;

    /**
     * Default constructor
     */
    public WebsitePasswordEntry() {
        super();
    }

    /**
     * Constructor which takes the website password entry ID from the database, password name, website URL, username
     * of the user in this application, password username, last updated date and encrypted password as parameters
     * @param id String of website password ID
     * @param passwordName String of password name
     * @param siteUrl String of website URL
     * @param masterUsername String of master username
     * @param passwordUsername String of password username
     * @param dateSet String of last updated date in the database
     * @param encryptedPassword String of encrypted password
     */
    public WebsitePasswordEntry(String id, String passwordName, String siteUrl, String masterUsername,
                                String passwordUsername, String dateSet, String encryptedPassword) {
        super(id, passwordName, encryptedPassword, dateSet);
        this.siteUrl = siteUrl;
        this.masterUsername = masterUsername;
        this.passwordUsername = passwordUsername;
    }

    /**
     * Constructor which takes the password name, website URL, username
     * of the user in this application, password username, last updated date and encrypted password as parameters
     * @param passwordName String of password name
     * @param siteUrl String of website URL
     * @param masterUsername String of master username
     * @param passwordUsername String of password username
     * @param dateSet String of last updated date in the database
     * @param encryptedPassword String of encrypted password
     */
    public WebsitePasswordEntry(String passwordName, String siteUrl, String masterUsername, String passwordUsername, String dateSet, String encryptedPassword) {
        super(passwordName, encryptedPassword, dateSet);
        this.siteUrl = siteUrl;
        this.masterUsername = masterUsername;
        this.passwordUsername = passwordUsername;
    }

    /**
     * Retrieves the website URL
     * @return Website URL String
     */
    public String getSiteUrl() {
        return siteUrl;
    }

    /**
     * Retrieves the username of the user in this application
     * @return Master username String
     */
    public String getMasterUsername() {
        return masterUsername;
    }

    /**
     * Retrieves the password username
     * @return Password username String
     */
    public String getPasswordUsername() {
        return passwordUsername;
    }

    /**
     * Sets the website URL
     * @param siteUrl Website URL String
     */
    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    /**
     * Sets the username of the user in this application
     * @param masterUsername Master username String
     */
    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    /**
     * Sets the password username
     * @param passwordUsername Password username String
     */
    public void setPasswordUsername(String passwordUsername) {
        this.passwordUsername = passwordUsername;
    }
}
