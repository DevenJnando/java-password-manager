package com.jamesd.passwordmanager.Models.Passwords;

import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class StoredPassDbKey extends PasswordEntry {

    private final String decryptionKey;

    public StoredPassDbKey() {
        super();
        this.decryptionKey = null;
    }

    public StoredPassDbKey(String encryptedPassword, String decryptionKey) {
        super(encryptedPassword);
        this.decryptionKey = decryptionKey;
    }

    public String getDecryptionKey() {
        return this.decryptionKey;
    }

    public void decryptMasterPassword()
            throws GeneralSecurityException,
            IOException {
        this.decryptedPassword = EncryptDecryptPasswordsUtil.decryptPassword(getEncryptedPassword());
    }
}
