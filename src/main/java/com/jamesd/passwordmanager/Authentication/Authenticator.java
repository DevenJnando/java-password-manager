package com.jamesd.passwordmanager.Authentication;

import com.jamesd.passwordmanager.Controllers.LoginController;
import com.jamesd.passwordmanager.Models.User;

public abstract class Authenticator {
    protected LoginController loginController;
    protected User user;

    public Authenticator(LoginController loginController) {
        this.loginController = loginController;
    }
}
