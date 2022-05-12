package com.jamesd.passwordmanager.Utils;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TwoFactorVerificationUtil {

    private final static Logger logger = LoggerFactory.getLogger(TwoFactorVerificationUtil.class);

    public TwoFactorVerificationUtil() {
        throw new UnsupportedOperationException("Cannot instantiate abstract utility class.");
    }

    public static void sendVerificationCode(String phoneNumber) {
        Twilio.init(PropertiesUtil.getTwilioProperties().getProperty("account_id"),
                PropertiesUtil.getTwilioProperties().getProperty("account_token"));
        Verification verification = Verification.creator(
                        PropertiesUtil.getTwilioProperties().getProperty("service_id"),
                        phoneNumber,
                        "sms")
                .create();

        logger.info(verification.getStatus());
    }

    public static boolean verify(String phoneNumber, String enteredCode) {
        Twilio.init(PropertiesUtil.getTwilioProperties().getProperty("account_id"),
                PropertiesUtil.getTwilioProperties().getProperty("account_token"));
        VerificationCheck verificationCheck = VerificationCheck.creator(
                        PropertiesUtil.getTwilioProperties().getProperty("service_id"),
                        enteredCode)
                .setTo(phoneNumber).create();

        logger.info(verificationCheck.getStatus());

        if(verificationCheck.getStatus().contentEquals("approved")) {
            return true;
        } else {
            return false;
        }
    }
}
