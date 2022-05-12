package com.jamesd.passwordmanager.Authentication;

import com.jamesd.passwordmanager.Controllers.LoginController;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.DAO.StorageAccountManager;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Utils.HashMasterPasswordUtil;
import com.jamesd.passwordmanager.Models.Passwords.StoredPassDbKey;
import com.jamesd.passwordmanager.Models.Users.User;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;

/**
 * Class for authenticating a user attempting to login to the password manager. The user will be redirected
 * to the password manager home screen upon a successful login, and an invalid login message will be returned upon a
 * failed login.
 */
public class LoginAuthentication extends Authenticator{

    protected static Logger logger = LoggerFactory.getLogger(LoginAuthentication.class);

    /**
     * Constructor which sets the LoginController field.
     * @param loginController contains user-input fields (username/email, password)
     */
    public LoginAuthentication(LoginController loginController) {
        super(loginController);
    }

    /**
     * Method which attempts a user login using either a username or an email. Errors out of the login method is unknown.
     * @param loginMethod String which specifies if the user is logging in using a username or email
     * @return Returns a boolean - true if login is successful, false if unsuccessful
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the decryption key is incorrect, or if
     * the master password decryption process fails
     * @throws IOException Throws an IO exception if the response from CosmosDB cannot be read
     * @throws SQLException Throws an SQLException if the CosmosDB query has an incorrect formatting/syntax
     */
    public Boolean login(String loginMethod) throws GeneralSecurityException,
            IOException,
            SQLException {
        String loginId = getLoginController().getUsernameEmailLoginField().getText();
        String password = getLoginController().getPasswordLoginField().getText();
        getLoginController().getLoginStatusLabel().setText("Logging in...");
        if(loginMethod.equals("username")) {
            return validateUsernameAndPassword(loginId, password);
        }
        else if(loginMethod.equals("email")) {
            return validateEmailAndPassword(loginId, password);
        }
        else {
            logger.error("Login method not known. Cannot proceed.");
            throw new LoginException("Login method " + loginMethod + " is not known. Cannot proceed.");
        }
    }

    /**
     * Method which attempts to retrieve a list of User objects (should only ever be one User object retrieved)
     * which match the given username, and then attempts to validate the User object's credentials
     * @param loginId User-input username
     * @param password User-input password
     * @return Returns a boolean - true if validation is successful, false if unsuccessful
     */
    private Boolean validateUsernameAndPassword(String loginId, String password) {
        List<User> users = MasterSQLQueries.queryUsersByUsername(loginId);
        return authenticate(users, password);
    }

    /**
     * Method which attempts to retrieve a list of User objects (should only ever be one User object retrieved)
     * which match the given email address, and then attempts to validate the User object's credentials
     * @param loginId User-input email address
     * @param password User-input password
     * @return Returns a boolean - true if validation is successful, false if unsuccessful
     */
    private Boolean validateEmailAndPassword(String loginId, String password) {
        List<User> users = MasterSQLQueries.queryUsersByEmail(loginId);
        return authenticate(users, password);
    }

    /**
     * Calls the checkPassword method to validate the User attempting to log in
     * @param users List of User objects which match the input username/email (should always be only one User)
     * @param plaintextPassword User-input password
     * @return Returns a boolean - true if validation is successful, false if unsuccessful
     */
    private Boolean validate(List<User> users, String plaintextPassword) {
        if(users.size() > 0) {
            for(User user : users) {
                return checkPassword(plaintextPassword, user);
            }
        }
        return false;
    }

    /**
     * Method which initialises and unlocks the password database upon a successful authentication and returns an
     * unsuccessful login message upon an unsuccessful authentication
     * @param users List of User objects retrieved matching the given username/email address (should always be one User)
     * @param password User-input password
     * @return Returns a boolean - true if login is successful, false if unsuccessful
     */
    private Boolean authenticate(List<User> users, String password) {
        if(validate(users, password)) {
            return true;
        } else {
            getLoginController().getLoginStatusLabel().setText("Login credentials incorrect. Please try again.");
            getLoginController().getLoginStatusLabel().setTextFill(Color.RED);
            return false;
        }
    }

    /**
     * Checks the given password against the stored encrypted password of the retrieved User object.
     * @param plaintextPassword User-input password
     * @param user User object which contains the correct encrypted password
     * @return Returns true if the given password matches the stored encrypted password and false if it does not match
     */
    private Boolean checkPassword(String plaintextPassword, User user) {
        Boolean passwordMatch = HashMasterPasswordUtil.checkHashAndUpdate(plaintextPassword, user);
        if(passwordMatch) {
            this.user = user;
        }
        return passwordMatch;
    }
}
