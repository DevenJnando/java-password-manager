package com.jamesd.passwordmanager.Controllers;

import javafx.fxml.Initializable;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public abstract class NewPasswordController extends ModifyPasswordController {

    public NewPasswordController() {
        super();
    }

    protected abstract void addNewPassword() throws GeneralSecurityException, UnsupportedEncodingException;

    protected abstract void confirmAndAddNewPassword() throws GeneralSecurityException, UnsupportedEncodingException;
}
