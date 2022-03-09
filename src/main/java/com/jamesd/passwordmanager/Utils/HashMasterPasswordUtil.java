package com.jamesd.passwordmanager.Utils;

import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.Models.Users.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HashMasterPasswordUtil {

    private static final Logger logger = LoggerFactory.getLogger(HashMasterPasswordUtil.class);
    private static final int ROUNDS = 10;

    public HashMasterPasswordUtil() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

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

    private static boolean checkHash(String plaintextPassword, String hash) {
        return BCrypt.checkpw(plaintextPassword, hash);
    }

    public static String hashPassword(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt(ROUNDS));
    }

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
