package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StorageAccountManager;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.DocumentWrapper;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;

import javafx.scene.control.TableView;
import java.util.HashSet;

/**
 * Controller responsible for deleting one or multiple password entries from a parent folder
 */
public class DeletePasswordController {

    /**
     * Default constructor
     */
    public DeletePasswordController() {

    }

    /**
     * Deletes a single PasswordEntry from the specified parent PasswordEntryFolder object
     * @param entry Object (should always be of the PasswordEntry superclass) to be deleted
     * @param parentFolder PasswordEntryFolder which the password entry belongs to
     * @throws ClassNotFoundException Throws ClassNotFoundException if the entry Object is not a subclass of
     * PasswordEntry
     */
    public void deleteSingleEntry(Object entry, PasswordEntryFolder parentFolder)
            throws ClassNotFoundException {
        StoredPassSQLQueries.deletePasswordInDb(entry, parentFolder);
        WebPasswordDetailsController.getStage().close();
    }

    /**
     * Deletes multiple PasswordEntry objects from the specified parent PasswordEntryFolder object
     * @param tableView TableView containing the list of PasswordEntry objects to be deleted
     * @param selectedFolder PasswordEntryFolder which the password entries belong to
     * @throws ClassNotFoundException Throws ClassNotFoundException if the Objects in tableView are not wrapped
     * subclasses of PasswordEntry
     */
    public void deleteMultipleEntries(TableView<Object> tableView, PasswordEntryFolder selectedFolder)
            throws ClassNotFoundException {
        Class<?> websitePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper");
        Class<?> databasePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper");
        Class<?> creditDebitCardEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper");
        Class<?> documentEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.DocumentWrapper");
        HashSet<Object> toBeDeleted = new HashSet<>();
        for(Object o : tableView.getItems()) {
            if(websitePasswordEntryWrapperClass.isInstance(o)) {
                WebsitePasswordEntryWrapper websitePasswordEntryWrapper = (WebsitePasswordEntryWrapper) o;
                if(websitePasswordEntryWrapper.isChecked().getValue()) {
                    toBeDeleted.add(websitePasswordEntryWrapper);
                }
            } if(databasePasswordEntryWrapperClass.isInstance(o)) {
                DatabasePasswordEntryWrapper databasePasswordEntryWrapper = (DatabasePasswordEntryWrapper) o;
                if(databasePasswordEntryWrapper.isChecked().getValue()) {
                    toBeDeleted.add(databasePasswordEntryWrapper);
                }
            } if(creditDebitCardEntryWrapperClass.isInstance(o)) {
                CreditDebitCardEntryWrapper creditDebitCardEntryWrapper = (CreditDebitCardEntryWrapper) o;
                if(creditDebitCardEntryWrapper.isChecked().getValue()) {
                    toBeDeleted.add(creditDebitCardEntryWrapper);
                }
            } if(documentEntryWrapperClass.isInstance(o)) {
                DocumentWrapper documentWrapper = (DocumentWrapper) o;
                if(documentWrapper.isChecked().getValue()) {
                    toBeDeleted.add(documentWrapper);
                }
            }
        }
        for(Object o : toBeDeleted) {
            if(websitePasswordEntryWrapperClass.isInstance(o)) {
                WebsitePasswordEntryWrapper wrapper = (WebsitePasswordEntryWrapper) o;
                StoredPassSQLQueries.deletePasswordInDb(wrapper.getWebsitePasswordEntry(), selectedFolder);
                PasswordManagerApp.getPasswordHomeController().populateWebsiteEntryPasswords(selectedFolder);
            } if(databasePasswordEntryWrapperClass.isInstance(o)) {
                DatabasePasswordEntryWrapper wrapper = (DatabasePasswordEntryWrapper) o;
                StoredPassSQLQueries.deletePasswordInDb(wrapper.getDatabasePasswordEntry(), selectedFolder);
                PasswordManagerApp.getPasswordHomeController().populateDatabaseEntryPasswords(selectedFolder);
            } if(creditDebitCardEntryWrapperClass.isInstance(o)) {
                CreditDebitCardEntryWrapper wrapper = (CreditDebitCardEntryWrapper) o;
                StoredPassSQLQueries.deletePasswordInDb(wrapper.getCreditDebitCardEntry(), selectedFolder);
                PasswordManagerApp.getPasswordHomeController().populateCreditDebitCardEntryPasswords(selectedFolder);
            } if(documentEntryWrapperClass.isInstance(o)) {
                DocumentWrapper wrapper = (DocumentWrapper) o;
                StorageAccountManager.deleteBlob(selectedFolder.getPasswordFolder() + "/" + wrapper.getDocumentEntry().getPasswordName());
                StoredPassSQLQueries.deletePasswordInDb(wrapper.getDocumentEntry(), selectedFolder);
                PasswordManagerApp.getPasswordHomeController().populateDocumentEntryPasswords(selectedFolder);
            }
        }
        if(PasswordManagerApp.getSidebarController().getSelectedFolder().equals(selectedFolder)) {
            PasswordManagerApp.getPasswordDetailsController().setNoDetailsLoaded();
        }
        PasswordHomeController.getStage().close();
    }
}
