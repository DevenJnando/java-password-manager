package com.jamesd.passwordmanager.Models.Passwords;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Abstract class for all password entries. Contains common fields and methods for subclasses of PasswordEntry.
 */
public abstract class PasswordEntry {

    protected String id;
    protected String passwordName;
    protected String decryptedPassword;
    protected String encryptedPassword;
    private String dateSet;
    private String needsUpdatedMessage;

    /**
     * Default constructor
     */
    public PasswordEntry() {
        this.id = UUID.randomUUID().toString();
        this.passwordName = null;
        this.decryptedPassword = null;
        this.encryptedPassword = null;
        this.needsUpdatedMessage = "";
        this.dateSet = LocalDate.now().toString();
    }

    /**
     * Constructor which takes only the encrypted password
     * @param encryptedPassword encrypted password String
     */
    public PasswordEntry(String encryptedPassword) {
        this.id = UUID.randomUUID().toString();
        this.passwordName = null;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
        this.dateSet = LocalDate.now().toString();
    }

    /**
     * Constructor which takes the password name, encrypted password and date the password was updated
     * @param passwordName password name String
     * @param encryptedPassword encrypted password String
     * @param dateSet date the password was last updated String
     */
    public PasswordEntry(String passwordName, String encryptedPassword, String dateSet) {
        this.id = UUID.randomUUID().toString();
        this.passwordName = passwordName;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
        this.dateSet = dateSet;
    }

    /**
     * Constructor which takes the ID from the database, password name, encrypted password and the date the password
     * was updated
     * @param id ID of password in database String
     * @param passwordName password name String
     * @param encryptedPassword encrypted password String
     * @param dateSet date the password was last updated String
     */
    public PasswordEntry(String id, String passwordName, String encryptedPassword, String dateSet) {
        this.id = id;
        this.passwordName = passwordName;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
        this.dateSet = dateSet;
    }

    /**
     * Retrieves password ID
     * @return ID of password String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Retrieves password name
     * @return name of password String
     */
    public String getPasswordName() { return this.passwordName; }

    /**
     * Retrieves decrypted password
     * @return decrypted password String
     */
    public String getDecryptedPassword() {
        return this.decryptedPassword;
    }

    /**
     * Retrieves encrypted password
     * @return encrypted password String
     */
    public String getEncryptedPassword() {
        return this.encryptedPassword;
    }

    /**
     * Retrieves date password was last updated
     * @return date password was last updated String
     */
    public String getDateSet() {
        return this.dateSet;
    }

    /**
     * Retrieves a message informing the user the password needs updating
     * @return needs updated message String
     */
    public String getNeedsUpdatedMessage() {
        return needsUpdatedMessage;
    }

    /**
     * Sets the password ID
     * @param id password ID String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the password name
     * @param passwordName password name String
     */
    public void setPasswordName(String passwordName) {
        this.passwordName = passwordName;
    }

    /**
     * Sets the decrypted password
     * @param decryptedPassword decrypted password String
     */
    public void setDecryptedPassword(String decryptedPassword) {
        this.decryptedPassword = decryptedPassword;
    }

    /**
     * Sets the encrypted password
     * @param encryptedPassword encrypted password String
     */
    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * Sets the date the password was last updated
     * @param dateSet date last updated String
     */
    public void setDateSet(String dateSet) {
        this.dateSet = dateSet;
    }

    /**
     * Sets a message to update the password
     * @param needsUpdatedMessage message to update password String
     */
    public void setNeedsUpdatedMessage(String needsUpdatedMessage) {
        this.needsUpdatedMessage = needsUpdatedMessage;
    }
}
