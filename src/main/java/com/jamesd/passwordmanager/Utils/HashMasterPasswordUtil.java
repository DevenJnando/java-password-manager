package com.jamesd.passwordmanager.Utils;

import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.Models.Users.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class which one-way encrypts a plaintext master password and compares that encrypted hash to a user-inputted
 * plaintext password to see if they match
 */
public abstract class HashMasterPasswordUtil {

    private static final Logger logger = LoggerFactory.getLogger(HashMasterPasswordUtil.class);
    private static final int ROUNDS = 10;

    /**
     * Constructor throws an UnsupportedOperationException - class is abstract
     */
    public HashMasterPasswordUtil() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

    /**
     * Gets the number of rounds in a salt
     * @param salt Salt to check rounds of
     * @return Number of rounds a salt has as an Integer
     */
    private static int getRounds(String salt) {
        char minor = (char)0;
        int off = 0;

        if (salt.charAt(0) != '$' || salt.charAt(1) != '2') {
            throw new IllegalArgumentException("Invalid salt version");
        }
        if (salt.charAt(2) == '$') {
            off = 3;
        }
        else {
            minor = salt.charAt(2);
            if (minor != 'a' || salt.charAt(3) != '$')
                throw new IllegalArgumentException ("Invalid salt revision");
            off = 4;
        }

        // Extract number of rounds
        if (salt.charAt(off + 2) > '$') {
            throw new IllegalArgumentException("Missing salt rounds");
        }
        return Integer.parseInt(salt.substring(off, off + 2));
    }

    /**
     * Compares a plaintext password to the one-way encrypted hash in the master database
     * @param plaintextPassword User-inputted plaintext password String
     * @param hash One-way encrypted hash from the master database
     * @return Boolean true if matches, else false
     */
    private static boolean checkHash(String plaintextPassword, String hash) {
        return BCrypt.checkpw(plaintextPassword, hash);
    }

    /**
     * One-way encrypts a plaintext password. This hash will then serve as the master password in the master database
     * @param plaintextPassword Plaintext password String to be hashed
     * @return Hashed String object of the plaintext password
     */
    public static String hashPassword(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt(ROUNDS));
    }

    /**
     * Compares a plaintext password to the one-way encrypted hash in the master database. If the number of rounds has
     * been updated in this application, then the plaintext password is rehashed and updated in the database. As a rule
     * of thumb, the ROUNDS field should be updated once every 8-12 months for security purposes.
     * @param plaintextPassword Plaintext password to be compared to one-way encrypted hash in database
     * @param user Currently logged-in user
     * @return Boolean true if matches, else false
     */
    public static boolean checkHashAndUpdate(String plaintextPassword, User user) {
        String hash = user.getEncryptedPassword();
        if(checkHash(plaintextPassword, hash)) {
            int rounds = getRounds(hash);
            if(rounds != ROUNDS) {
                logger.debug("Updating plaintext password in database from " + rounds + " to " + ROUNDS + " rounds");
                String updatedHash = hashPassword(plaintextPassword);
                user.setEncryptedPassword(updatedHash);
                MasterSQLQueries.updateUserInDb(user);
                return true;
            }
            return true;
        }
        return false;
    }
}
