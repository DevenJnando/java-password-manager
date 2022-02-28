package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.PasswordManagerApp;

import java.io.IOException;

public class LogoutController {

    public void logout() throws IOException {
        PasswordManagerApp.setLoggedInUser(null);
        StoredPassSQLQueries.close();
        PasswordHomeController.getStage().close();
        PasswordManagerApp.initRootLayout();
    }

    public void cancel() {
        PasswordHomeController.getStage().close();
    }
}
