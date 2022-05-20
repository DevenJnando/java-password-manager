package com.jamesd.passwordmanager.Utils;

/**
 * Utility class which validates if a user input is an email address or not.
 */
public abstract class ValidateEmailUtil {

    /**
     * Throws error if an attempt to instantiate is made.
     */
    public ValidateEmailUtil() {
        throw new UnsupportedOperationException("Cannot instantiate abstract utility class.");
    }

    /**
     * Checks if an entry is an email address. Returns true if it is
     * @param email Potential email string
     * @return True if the string is an email address, else false
     */
    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
