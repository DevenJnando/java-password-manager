package com.jamesd.passwordmanager.Authentication;

import com.jamesd.passwordmanager.Controllers.LoginController;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
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

public class LoginAuthentication extends Authenticator{

    protected static Logger logger = LoggerFactory.getLogger(LoginAuthentication.class);

    public LoginAuthentication(LoginController loginController) {
        super(loginController);
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public User getLoggedInUser() {
        return this.user;
    }

    public Boolean login(String loginMethod) throws GeneralSecurityException,
            IOException,
            SQLException {
        String loginId = getLoginController().getUsernameEmailLoginField().getText();
        String password = getLoginController().getPasswordLoginField().getText();
        if(loginMethod.equals("username")) {
            return validateUsernameAndPassword(loginId, password, loginMethod);
        }
        else if(loginMethod.equals("email")) {
            return validateEmailAndPassword(loginId, password, loginMethod);
        }
        else {
            logger.error("Login method not known. Cannot proceed.");
            throw new LoginException("Login method " + loginMethod + " is not known. Cannot proceed.");
        }
    }

    public static void unlockStoredPassDb(List<StoredPassDbKey> storedPassKeys, String string, String loginMethod)
            throws GeneralSecurityException,
            IOException,
            SQLException {
        for(StoredPassDbKey entry : storedPassKeys) {
            EncryptDecryptPasswordsUtil.initialise(entry.getDecryptionKey());
            entry.decryptMasterPassword();
            if(loginMethod.equals("username")) {
                StoredPassSQLQueries.initialiseWithUsername(entry.getDecryptedPassword(), string);
            }
            else if(loginMethod.equals("email")) {
                StoredPassSQLQueries.initialiseWithEmail(entry.getDecryptedPassword(), string);
            }
            else {
                throw new LoginException("Login method " + loginMethod + " not known. Could not initialise password database.");
            }
        }
    }

    public Boolean validateUsernameAndPassword(String loginId, String password, String loginMethod)
            throws GeneralSecurityException,
            IOException,
            SQLException {
        List<User> users = MasterSQLQueries.queryUsersByUsername(loginId);
        return authenticate(users, loginId, loginMethod, password);
    }

    public Boolean validateEmailAndPassword(String loginId, String password, String loginMethod)
            throws GeneralSecurityException,
            IOException,
            SQLException {
        List<User> users = MasterSQLQueries.queryUsersByEmail(loginId);
        return authenticate(users, loginId, loginMethod, password);
    }

    public Boolean validate(List<User> users, String plaintextPassword) {
        if(users.size() > 0) {
            for(User user : users) {
                return checkPassword(plaintextPassword, user);
            }
        }
        return false;
    }

    public Boolean authenticate(List<User> users, String loginId, String loginMethod, String password)
            throws GeneralSecurityException, SQLException,
            IOException {
        if(validate(users, password)) {
            getLoginController().getLoginStatusLabel().setText("Login Successful, welcome back " + loginId + "!");
            MasterSQLQueries.initialiseStoredPassKey();
            List<StoredPassDbKey> keys = MasterSQLQueries.queryEncryptedStoredPassKey();
            unlockStoredPassDb(keys, loginId, loginMethod);
            return true;
        } else {
            getLoginController().getLoginStatusLabel().setText("Login credentials incorrect. Please try again.");
            getLoginController().getLoginStatusLabel().setTextFill(Color.RED);
            return false;
        }
    }

    public Boolean checkPassword(String plaintextPassword, User user) {
        Boolean passwordMatch = HashMasterPasswordUtil.checkHashAndUpdate(plaintextPassword, user);
        if(passwordMatch) {
            this.user = user;
        }
        return passwordMatch;
    }
}
