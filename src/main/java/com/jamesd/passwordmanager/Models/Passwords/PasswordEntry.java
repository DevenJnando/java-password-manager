package com.jamesd.passwordmanager.Models.Passwords;

import java.time.LocalDate;
import java.util.UUID;

public abstract class PasswordEntry {

    protected String id;
    protected String passwordName;
    protected String decryptedPassword;
    protected String encryptedPassword;
    private String dateSet;
    private String needsUpdatedMessage;

    public PasswordEntry() {
        this.id = UUID.randomUUID().toString();
        this.passwordName = null;
        this.decryptedPassword = null;
        this.encryptedPassword = null;
        this.needsUpdatedMessage = "";
        this.dateSet = LocalDate.now().toString();
    }

    public PasswordEntry(String encryptedPassword) {
        this.id = UUID.randomUUID().toString();
        this.passwordName = null;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
        this.dateSet = LocalDate.now().toString();
    }

    public PasswordEntry(String passwordName, String encryptedPassword, String dateSet) {
        this.id = UUID.randomUUID().toString();
        this.passwordName = passwordName;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
        this.dateSet = dateSet;
    }

    public PasswordEntry(String id, String passwordName, String encryptedPassword, String dateSet) {
        this.id = id;
        this.passwordName = passwordName;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
        this.dateSet = dateSet;
    }

    public String getId() {
        return this.id;
    }

    public String getPasswordName() { return this.passwordName; }

    public String getDecryptedPassword() {
        return this.decryptedPassword;
    }

    public String getEncryptedPassword() {
        return this.encryptedPassword;
    }

    public String getDateSet() {
        return this.dateSet;
    }

    public String getNeedsUpdatedMessage() {
        return needsUpdatedMessage;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPasswordName(String passwordName) {
        this.passwordName = passwordName;
    }

    public void setDecryptedPassword(String decryptedPassword) {
        this.decryptedPassword = decryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void setDateSet(String dateSet) {
        this.dateSet = dateSet;
    }

    public void setNeedsUpdatedMessage(String needsUpdatedMessage) {
        this.needsUpdatedMessage = needsUpdatedMessage;
    }
}
