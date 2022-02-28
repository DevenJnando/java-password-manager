package com.jamesd.passwordmanager.Models;

import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {
        this.decryptedPassword = EncryptDecryptPasswordsUtil.decryptPassword(getEncryptedPassword());
    }
}
