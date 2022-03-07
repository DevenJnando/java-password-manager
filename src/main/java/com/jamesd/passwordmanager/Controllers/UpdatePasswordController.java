package com.jamesd.passwordmanager.Controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;

public abstract class UpdatePasswordController extends ModifyPasswordController {

    public UpdatePasswordController() {
        super();
    }

    protected abstract void updatePassword() throws IOException;

    protected abstract void confirmAndUpdatePassword() throws GeneralSecurityException, IOException;
}
