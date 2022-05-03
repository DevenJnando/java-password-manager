package com.jamesd.passwordmanager.Authentication;

import com.jamesd.passwordmanager.Controllers.LoginController;
import com.jamesd.passwordmanager.Models.Users.User;

/**
 * Superclass for Authentication implementation classes
 */
public abstract class Authenticator {

    protected LoginController loginController;
    protected User user;

    /**
     * Constructor which takes the LoginController currently being used
     * @param loginController LoginController
     */
    public Authenticator(LoginController loginController) {
        this.loginController = loginController;
    }

    /**
     * Returns the LoginController
     * @return LoginController field
     */
    public LoginController getLoginController() {
        return loginController;
    }

    /**
     * Sets the LoginController field
     * @param loginController contains user-input fields (username/email, password)
     */
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    /**
     * Returns an object of the User
     * @return the User object
     */
    public User getUser() {
        return this.user;
    }
}
