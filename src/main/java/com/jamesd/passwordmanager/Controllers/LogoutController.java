package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.PasswordManagerApp;

import java.io.IOException;

/**
 * Controller responsible for logout functionality
 */
public class LogoutController {

    /**
     * Logout method which sets the logged in user as null and redirects to the login/register page
     * @throws IOException Throws IOException if the login/register page cannot be loaded
     */
    public void logout() throws IOException {
        PasswordManagerApp.setLoggedInUser(null);
        StoredPassSQLQueries.close();
        MasterSQLQueries.close();
        PasswordHomeController.getStage().close();
        PasswordManagerApp.initRootLayout();
    }

    /**
     * Closes the logout modal
     */
    public void cancel() {
        PasswordHomeController.getStage().close();
    }
}
