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
        EncryptDecryptPasswordsUtil.initialise("N6EHALQT3KiBhB4AAAAAAA");
    }

    @Test
    public void encryptAndDecryptString() throws GeneralSecurityException, IOException {
        String toBeEncrypted = "0hMQzrtrhGkLfbJB+nSMi0sfwXhij/OJJBfZn7GQDaOHpFVsrWcfK+SkUUE53qcZEDlUhxlGyXt7Oi269LiSAw==";
        String encryptedString = EncryptDecryptPasswordsUtil.encryptPassword(toBeEncrypted);
        System.out.println("Encrypted string: " + encryptedString);
        String decryptedString = EncryptDecryptPasswordsUtil.decryptPassword(encryptedString);
        assertEquals(toBeEncrypted, decryptedString);
    }
}
