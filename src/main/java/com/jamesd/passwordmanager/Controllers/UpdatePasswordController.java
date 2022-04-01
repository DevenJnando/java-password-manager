package com.jamesd.passwordmanager.Controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Abstract controller interface which provides abstract methods for any controller implementation responsible for
 * updating a master password
 */
public abstract class UpdatePasswordController extends ModifyPasswordController {

    /**
     * Default constructor
     */
    public UpdatePasswordController() {
        super();
    }

    /**
     * Method which updates the master password entry in the master database
     * @throws IOException Throws IOException if a connection to the master password database cannot be established
     */
    protected abstract void updatePassword() throws IOException;

    /**
     * Performs validation checks, and if the validation checks pass then the updatePassword method is called
     * and the master password is updated. If the validation checks fail, an error message(s) is fed back to the user
     * @throws GeneralSecurityException Throws GeneralSecurityException if this method is called whilst the user is not
     * logged in
     * @throws IOException Throws IOException if a connection to the master password database cannot be established
     */
    protected abstract void confirmAndUpdatePassword() throws GeneralSecurityException, IOException;
}
