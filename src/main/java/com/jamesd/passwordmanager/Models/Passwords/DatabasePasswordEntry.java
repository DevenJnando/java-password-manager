package com.jamesd.passwordmanager.Models.Passwords;

/**
 * Class that models a database password entry from the database
 */
public class DatabasePasswordEntry extends PasswordEntry {

    private String hostName;
    private String databaseName;
    private String masterUsername;
    private String databaseUsername;

    /**
     * Default constructor
     */
    public DatabasePasswordEntry() {
        super();
    }

    /**
     * Constructor which takes the database password entry ID from the database, password name, database hostname,
     * database name, username of the user in this application, database username, last date updated in database and
     * encrypted database password as parameters
     * @param id String of database password ID
     * @param passwordName String of database password name
     * @param hostName String of hostname
     * @param databaseName String of database name
     * @param masterUsername String of master username
     * @param databaseUsername String of database username
     * @param dateSet String of last updated date in database
     * @param encryptedPassword String of encrypted database password
     */
    public DatabasePasswordEntry(String id, String passwordName, String hostName, String databaseName,
                                 String masterUsername, String databaseUsername,
                                 String dateSet,  String encryptedPassword) {
        super(id, passwordName, encryptedPassword, dateSet);
        this.hostName = hostName;
        this.databaseName = databaseName;
        this.masterUsername = masterUsername;
        this.databaseUsername = databaseUsername;
    }

    /**
     * Constructor which takes the password name, database hostname, database name, username of the user in this
     * application, database username, last date updated in database and encrypted database password as parameters
     * @param passwordName String of database password name
     * @param hostName String of hostname
     * @param databaseName String of database name
     * @param masterUsername String of master username
     * @param databaseUsername String of database username
     * @param dateSet String of last updated date in database
     * @param encryptedPassword String of encrypted database password
     */
    public DatabasePasswordEntry(String passwordName, String hostName, String databaseName,
                                 String masterUsername, String databaseUsername, String dateSet,
                                 String encryptedPassword) {
        super(passwordName, encryptedPassword, dateSet);
        this.hostName = hostName;
        this.databaseName = databaseName;
        this.masterUsername = masterUsername;
        this.databaseUsername = databaseUsername;
    }

    /**
     * Retrieves the hostname
     * @return Hostname String
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Retrieves the database name
     * @return Database name String
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Retrieves the username of the user in this application
     * @return Master username String
     */
    public String getMasterUsername() { return masterUsername; }

    /**
     * Retrieves the database username
     * @return Database username String
     */
    public String getDatabaseUsername() { return databaseUsername; }

    /**
     * Sets the hostname
     * @param hostName Hostname String
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Sets the database name
     * @param databaseName Database name String
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Sets the username of the user in this application
     * @param masterUsername Master username String
     */
    public void setMasterUsername(String masterUsername) { this.masterUsername = masterUsername; }

    /**
     * Sets the database username
     * @param databaseUsername Database username String
     */
    public void setDatabaseUsername(String databaseUsername) { this.databaseUsername = databaseUsername; }
}
