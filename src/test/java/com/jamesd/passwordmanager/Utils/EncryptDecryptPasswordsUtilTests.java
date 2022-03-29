package com.jamesd.passwordmanager.Utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptDecryptPasswordsUtilTests {

    @BeforeAll
    public static void initialise() throws InvalidKeySpecException, NoSuchAlgorithmException {
        EncryptDecryptPasswordsUtil.initialise("TestKeyNotReal");
    }

    @Test
    public void encryptAndDecryptString() throws GeneralSecurityException, IOException {
        String toBeEncrypted = "Extended warranty? How can I lose!";
        String encryptedString = EncryptDecryptPasswordsUtil.encryptPassword(toBeEncrypted);
        System.out.println("Encrypted string: " + encryptedString);
        String decryptedString = EncryptDecryptPasswordsUtil.decryptPassword(encryptedString);
        assertEquals(toBeEncrypted, decryptedString);
    }
}
