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
        String toBeEncrypted = "0zkphfw8AL2ET6OVsaXM7YcfVtqjPEfjUgKqFTJxzPP74HrTwMdrnZAdSAyADjO0sssA5AVwYGGNgS046j3jhQ==";
        String encryptedString = EncryptDecryptPasswordsUtil.encryptPassword(toBeEncrypted);
        System.out.println("Encrypted string: " + encryptedString);
        String decryptedString = EncryptDecryptPasswordsUtil.decryptPassword(encryptedString);
        assertEquals(toBeEncrypted, decryptedString);
    }
}
