package com.jamesd.passwordmanager.Models.Passwords;

public class DatabasePasswordEntry extends PasswordEntry {

    private String hostName;
    private String databaseName;
    private String masterUsername;
    private String databaseUsername;

    public DatabasePasswordEntry() {
        super();
    }

    public DatabasePasswordEntry(String id, String passwordName, String hostName, String databaseName,
                                 String masterUsername, String databaseUsername,
                                 String dateSet,  String encryptedPassword) {
        super(id, passwordName, encryptedPassword, dateSet);
        this.hostName = hostName;
        this.databaseName = databaseName;
        this.masterUsername = masterUsername;
        this.databaseUsername = databaseUsername;
    }

    public DatabasePasswordEntry(String passwordName, String hostName, String databaseName,
                                 String masterUsername, String databaseUsername, String dateSet,
                                 String encryptedPassword) {
        super(passwordName, encryptedPassword, dateSet);
        this.hostName = hostName;
        this.databaseName = databaseName;
        this.masterUsername = masterUsername;
        this.databaseUsername = databaseUsername;
    }

    public String getHostName() {
        return hostName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getMasterUsername() { return masterUsername; }

    public String getDatabaseUsername() { return databaseUsername; }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setMasterUsername(String masterUsername) { this.masterUsername = masterUsername; }

    public void setDatabaseUsername(String databaseUsername) { this.databaseUsername = databaseUsername; }
}
