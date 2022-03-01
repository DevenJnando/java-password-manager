package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import javafx.scene.control.TableView;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;

public class DeletePasswordController {

    public DeletePasswordController() {

    }

    public void deleteSingleEntry(WebsitePasswordEntry entry) throws LoginException, IOException {
        StoredPassSQLQueries.deletePasswordInDb(entry);
        PasswordDetailsController.getStage().close();
    }

    public void deleteMultipleEntries(TableView<WebsitePasswordEntryWrapper> passwordEntryWrapperTableView) throws IOException, LoginException {
        HashSet toBeDeleted = new HashSet<WebsitePasswordEntry>();
        passwordEntryWrapperTableView.getItems().stream().forEach(o -> {
            if(o.isChecked().getValue()) {
                toBeDeleted.add(o);
            }
        });
        toBeDeleted.stream().forEach(o -> {
            WebsitePasswordEntryWrapper wrapper = (WebsitePasswordEntryWrapper) o;
            StoredPassSQLQueries.deletePasswordInDb(wrapper.getWebsitePasswordEntry());
        });
        passwordEntryWrapperTableView.getItems().removeAll(toBeDeleted);
        PasswordHomeController.getStage().close();
        PasswordManagerApp.loadPasswordHomeView();
    }
}
