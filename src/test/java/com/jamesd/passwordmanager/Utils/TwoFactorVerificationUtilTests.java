package com.jamesd.passwordmanager.Utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TwoFactorVerificationUtilTests {

    @BeforeAll
    public static void initialise() throws FileNotFoundException {
        PropertiesUtil.initialise();
    }

    @Test
    public void sendVerificationCodeTest() {
        TwoFactorVerificationUtil.sendVerificationCode("replace with actual phone number");
    }

    @Test
    public void verifyTest() {
        assertTrue(TwoFactorVerificationUtil.verify("replace with actual phone number", "the code it gives you"));
    }
}
