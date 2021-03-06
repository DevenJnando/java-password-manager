package com.jamesd.passwordmanager.Models.Passwords;

import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Class which models the key to the password database
 */
public class StoredPassDbKey extends PasswordEntry {

    private final String decryptionKey;
    private final String encryptedStorage;

    /**
     * Default constructor
     */
    public StoredPassDbKey() {
        super();
        this.decryptionKey = null;
        this.encryptedStorage = null;
    }

    /**
     * Constructor which takes the encrypted password and decryption key as parameters
     * @param encryptedPassword String of encrypted password
     * @param decryptionKey String of decryption key
     */
    public StoredPassDbKey(String encryptedPassword, String decryptionKey) {
        super(encryptedPassword);
        this.decryptionKey = decryptionKey;
        this.encryptedStorage = null;
    }

    /**
     * Constructor which takes the encrypted password, decryption key and encrypted storage key as parameters
     * @param encryptedPassword String of encrypted password
     * @param decryptionKey String of decryption key
     * @param encryptedStorage String of encrypted storage key
     */
    public StoredPassDbKey(String encryptedPassword, String decryptionKey, String encryptedStorage) {
        super(encryptedPassword);
        this.decryptionKey = decryptionKey;
        this.encryptedStorage = encryptedStorage;
    }

    /**
     * Retrieves the decryption key
     * @return Decryption key String
     */
    public String getDecryptionKey() {
        return this.decryptionKey;
    }

    /**
     * Retrieves the encrypted storage key
     * @return Encrypted storage key
     */
    public String getEncryptedStorage() { return this.encryptedStorage; }

    /**
     * Decrypts the master password in the database
     * @throws GeneralSecurityException Throws GeneralSecurityException if the password cannot be decrypted
     * @throws IOException Throws IOException if the encrypted/decrypted password cannot be read
     */
    public void decryptMasterPassword()
            throws GeneralSecurityException,
            IOException {
        this.decryptedPassword = EncryptDecryptPasswordsUtil.decryptPassword(getEncryptedPassword());
    }
}
