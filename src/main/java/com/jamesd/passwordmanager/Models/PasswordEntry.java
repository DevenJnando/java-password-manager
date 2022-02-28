package com.jamesd.passwordmanager.Models;

import java.util.UUID;

public abstract class PasswordEntry {

    protected String id;
    protected String passwordName;
    protected String decryptedPassword;
    protected String encryptedPassword;

    public PasswordEntry() {
        this.id = UUID.randomUUID().toString();
        this.passwordName = null;
        this.decryptedPassword = null;
        this.encryptedPassword = null;
    }

    public PasswordEntry( String encryptedPassword) {
        this.id = UUID.randomUUID().toString();
        this.passwordName = null;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
    }

    public PasswordEntry(String passwordName, String encryptedPassword) {
        this.id = UUID.randomUUID().toString();
        this.passwordName = passwordName;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
    }

    public PasswordEntry(String id, String passwordName, String encryptedPassword) {
        this.id = id;
        this.passwordName = passwordName;
        this.encryptedPassword = encryptedPassword;
        this.decryptedPassword = null;
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
}
