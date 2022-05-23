package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Tables.CreditDebitCardTable;
import com.jamesd.passwordmanager.Tables.DatabasePasswordTable;
import com.jamesd.passwordmanager.Tables.DocumentTable;
import com.jamesd.passwordmanager.Tables.WebsitePasswordTable;
import com.jamesd.passwordmanager.Utils.TreeViewIteratorUtil;
import com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.DocumentWrapper;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Home controller for the password manager application. Responsible for populating password entries and their containing
 * folders. It is also responsible for providing an entry point to modals such as "add password", "delete password", etc.
 */
public class PasswordHomeController implements Initializable {

    public static Logger logger = LoggerFactory.getLogger(PasswordHomeController.class);

    /**
     * FXML fields
     */
    @FXML
    private VBox passwordTableVbox = new VBox();
    @FXML
    private JFXButton deletePasswordsButton = new JFXButton();

    /**
     * Stage for modals
     */
    private static Stage stage;

    /**
     * Default constructor
     */
    public PasswordHomeController() {

    }

    /**
     * Initialize method which sets the password manager logo (currently a placeholder, sorry 1password!), sets the
     * buttons, the title pane and the icon for the delete button. Finally, the list of PasswordEntryFolder objects is
     * populated
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDeleteButtonIcon();
    }

    /**
     * Retrieves the VBox object which contains the TableView for all passwords in the selected password folder
     * @return VBox containing password's TableView
     */
    public VBox getPasswordTableVbox() {
        return this.passwordTableVbox;
    }

    /**
     * Sets the delete button icon
     */
    public void setDeleteButtonIcon() {
        Text delete = GlyphsDude.createIcon(FontAwesomeIcon.TRASH, "3.0em");
        deletePasswordsButton.setGraphic(delete);
    }

    /**
     * Loads the "delete multiple passwords" modal. Context does not switch to the DeletePasswordController until the deletion
     * action has been confirmed
     * @throws IOException Throws IOException if the "delete multiple passwords" modal cannot be loaded
     */
    private void loadDeletePasswordModal() throws IOException{
        Stage deletePasswordStage = new Stage();
        FXMLLoader deletePasswordLoader = new FXMLLoader(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/delete-multiple-passwords-modal.fxml"));
        AnchorPane deletePasswordPane = deletePasswordLoader.load();
        Scene deletePasswordScene = new Scene(deletePasswordPane);
        deletePasswordStage.setScene(deletePasswordScene);
        deletePasswordStage.setTitle("Delete Passwords");
        deletePasswordStage.initOwner(PasswordManagerApp.getMainStage());
        deletePasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = deletePasswordStage;
        stage.showAndWait();
    }


    /**
     * Triggered when a "website password" folder is selected by the user. A new WebsitePasswordTable is generated,
     * which contains all the WebsitePasswordEntry objects located within the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user. Contains only WebsitePasswordEntry objects
     */
    public void populateWebsiteEntryPasswords(PasswordEntryFolder folder) {
        WebsitePasswordTable websitePasswordTable = new WebsitePasswordTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren();
            TableView<WebsitePasswordEntryWrapper> websitePasswordEntryTableView =
                    websitePasswordTable.createTableView(folder);
            websitePasswordEntryTableView.setId("websiteEntryTableView");
            websitePasswordEntryTableView.setMinHeight(210.0);
            websitePasswordEntryTableView.setPrefHeight(210.0);
            websitePasswordEntryTableView.setPrefWidth(1085.0);
            websitePasswordEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(tableViewList.size() < 2) {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(websitePasswordEntryTableView);
            } else {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, websitePasswordEntryTableView);
            }
        } catch (MalformedURLException
                | LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered when a "database password" folder is selected by the user. A new DatabasePasswordTable is generated,
     * which contains all the DatabasePasswordEntry objects located within the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user. Contains only DatabasePasswordEntry objects
     */
    public void populateDatabaseEntryPasswords(PasswordEntryFolder folder) {
        DatabasePasswordTable databasePasswordTable = new DatabasePasswordTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren();
            TableView<DatabasePasswordEntryWrapper> databasePasswordEntryTableView =
                    databasePasswordTable.createTableView(folder);
            databasePasswordEntryTableView.setId("databaseEntryTableView");
            databasePasswordEntryTableView.setMinHeight(210.0);
            databasePasswordEntryTableView.setPrefHeight(210.0);
            databasePasswordEntryTableView.setPrefWidth(1085.0);
            databasePasswordEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(tableViewList.size() < 2) {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(databasePasswordEntryTableView);
            } else {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, databasePasswordEntryTableView);
            }
        } catch (MalformedURLException
                | LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered when a "credit card" folder is selected by the user. A new CreditDebitCardTable is generated,
     * which contains all the CreditDebitCardEntry objects located within the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user. Contains only CreditDebitCardEntry objects
     */
    public void populateCreditDebitCardEntryPasswords(PasswordEntryFolder folder) {
        CreditDebitCardTable creditDebitCardTable = new CreditDebitCardTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren();
            TableView<CreditDebitCardEntryWrapper> creditDebitCardEntryTableView =
                    creditDebitCardTable.createTableView(folder);
            creditDebitCardEntryTableView.setId("creditDebitCardEntryTableView");
            creditDebitCardEntryTableView.setMinHeight(210.0);
            creditDebitCardEntryTableView.setPrefHeight(210.0);
            creditDebitCardEntryTableView.setPrefWidth(1085.0);
            creditDebitCardEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(tableViewList.size() < 2) {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(creditDebitCardEntryTableView);
            } else {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, creditDebitCardEntryTableView);
            }
        } catch (MalformedURLException
                | LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered when a "document" folder is selected by the user. A new DocumentTable is generated,
     * which contains all the DocumentEntry objects located within the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user. Contains only DocumentEntry objects
     */
    public void populateDocumentEntryPasswords(PasswordEntryFolder folder) {
        DocumentTable documentTable = new DocumentTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren();
            TableView<DocumentWrapper> documentEntryTableView =
                    documentTable.createTableView(folder);
            documentEntryTableView.setId("documentEntryTableView");
            documentEntryTableView.setMinHeight(210.0);
            documentEntryTableView.setPrefHeight(210.0);
            documentEntryTableView.setPrefWidth(1085.0);
            documentEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(tableViewList.size() < 2) {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(documentEntryTableView);
            } else {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, documentEntryTableView);
            }
        } catch (LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Consolidates each Object which has been check-marked within a TableView, casts them to an appropriate
     * class Wrapper type e.g. WebsitePasswordEntryWrapper and adds them to a list. The "delete
     * multiple passwords" modal is then loaded if this list is not empty
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "delete multiple passwords" modal cannot be loaded
     */
    @FXML
    public void deletePasswords() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            List<Object> passwordsToBeDeleted = new ArrayList<>();
            TableView<Object> tableView =
                    (TableView<Object>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);

            // Each object in the TableView is looped over
            for(Object o : tableView.getItems()){
                try {

                    // A reflector pattern is used to check what type of Wrapper the Objects are, and then they are
                    // cast to that Wrapper type and added to the list of passwords to be deleted
                    Class<?> websitePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper");
                    Class<?> databasePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper");
                    Class<?> creditDebitCardEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper");
                    Class<?> documentEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.DocumentWrapper");
                    if(websitePasswordEntryWrapperClass.isInstance(o)) {
                        WebsitePasswordEntryWrapper websitePasswordEntryWrapper = (WebsitePasswordEntryWrapper) o;
                        if(websitePasswordEntryWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(websitePasswordEntryWrapper);
                        }
                    } if(databasePasswordEntryWrapperClass.isInstance(o)) {
                        DatabasePasswordEntryWrapper databasePasswordEntryWrapper = (DatabasePasswordEntryWrapper) o;
                        if(databasePasswordEntryWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(databasePasswordEntryWrapper);
                        }
                    } if(creditDebitCardEntryWrapperClass.isInstance(o)) {
                        CreditDebitCardEntryWrapper creditDebitCardEntryWrapper = (CreditDebitCardEntryWrapper) o;
                        if(creditDebitCardEntryWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(creditDebitCardEntryWrapper);
                        }
                    } if(documentEntryWrapperClass.isInstance(o)) {
                        DocumentWrapper documentWrapper = (DocumentWrapper) o;
                        if(documentWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(documentWrapper);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if(!passwordsToBeDeleted.isEmpty()) {
                loadDeletePasswordModal();
                logger.info("Loaded delete passwords modal.");
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Upon confirmation from the user, the selected passwords are removed from the password database by creating
     * a new DeletePasswordController object and calling the controller's deleteMultipleEntries method taking the
     * current TableView object and the selected PasswordEntryFolder as arguments. The TableView object is then
     * repopulated after the selected entries have been deleted
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged-in
     * @throws ClassNotFoundException Throws ClassNotFoundException if any subclass of PasswordEntry cannot be found
     */
    @FXML
    private void confirmDelete() throws LoginException, ClassNotFoundException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Class<?> websitePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            Class<?> databasePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
            Class<?> creditDebitCardEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
            Class<?> documentEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DocumentEntry");
            TableView<Object> tableView =
                    (TableView<Object>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);

            DeletePasswordController deletePasswordController = new DeletePasswordController();
            deletePasswordController.deleteMultipleEntries(tableView, PasswordManagerApp.getSidebarController().getSelectedFolder());
            PasswordManagerApp.getSidebarController().populatePasswordFolders();

            if(PasswordEntryFolder.EntryFactory.determineEntryType(PasswordManagerApp.getSidebarController().getSelectedFolder())
                    .equals(websitePasswordEntryClass)) {
                PasswordManagerApp.getPasswordHomeController().populateWebsiteEntryPasswords(PasswordManagerApp.getSidebarController().getSelectedFolder());
            } if(PasswordEntryFolder.EntryFactory.determineEntryType(PasswordManagerApp.getSidebarController().getSelectedFolder())
                    .equals(databasePasswordEntryClass)) {
                PasswordManagerApp.getPasswordHomeController().populateDatabaseEntryPasswords(PasswordManagerApp.getSidebarController().getSelectedFolder());
            } if(PasswordEntryFolder.EntryFactory.determineEntryType(PasswordManagerApp.getSidebarController().getSelectedFolder())
                    .equals(creditDebitCardEntryClass)) {
                PasswordManagerApp.getPasswordHomeController().populateCreditDebitCardEntryPasswords(PasswordManagerApp.getSidebarController().getSelectedFolder());
            } if(PasswordEntryFolder.EntryFactory.determineEntryType(PasswordManagerApp.getSidebarController().getSelectedFolder())
                    .equals(documentEntryClass)) {
                PasswordManagerApp.getPasswordHomeController().populateDocumentEntryPasswords(PasswordManagerApp.getSidebarController().getSelectedFolder());
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Cancels the deletion operation and closes the "delete multiple passwords" modal
     */
    @FXML
    private void cancelDelete() {
        getStage().close();
    }

    /**
     * Called when a new password is successfully added to the database. Displays the newly added password details to
     * the user
     * @throws ClassNotFoundException Throws ClassNotFoundException if any subclass of PasswordEntry cannot be found
     */
    public void viewNewlyAddedPassword() throws ClassNotFoundException {
        List<Node> treeView = PasswordManagerApp.getSidebarController().getSidebar().getChildren()
                .stream()
                .filter(o -> o.getId().equals("folderNavigationTreeView"))
                .collect(Collectors.toList());

        if(!treeView.isEmpty()) {
            TreeView<String> folderNavigationTreeView = (TreeView<String>) treeView.get(0);
            if (PasswordManagerApp.getSidebarController()
                    .getBaseAddPasswordController()
                    .getSelectedFolder()
                    .getPasswordFolder() != null) {

                PasswordEntryFolder selectedFolder = PasswordManagerApp.getSidebarController()
                        .getBaseAddPasswordController().getSelectedFolder();

                // Iterates over each child node of the folderNavigationTreeView root node and selects the
                // folder which has had a new password added to it
                TreeViewIteratorUtil<String> treeViewIteratorUtil = new TreeViewIteratorUtil<>(folderNavigationTreeView.getRoot());
                TreeItem<String> childNode = new TreeItem<>();
                while(treeViewIteratorUtil.hasNext()) {
                    TreeItem<String> currentNode = treeViewIteratorUtil.next();
                    if(currentNode.getValue().equals(selectedFolder.getPasswordFolder())) {
                        childNode = currentNode;
                        break;
                    }
                }
                childNode.getParent().setExpanded(true);
                folderNavigationTreeView.getSelectionModel().select(childNode);

                // Reflection used to determine which TableView to generate and populate. The newly added password is
                // then selected from this TableView
                Class<?> websitePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
                Class<?> databasePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
                Class<?> creditDebitCardEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
                Class<?> documentEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DocumentEntry");
                if(websitePasswordEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateWebsiteEntryPasswords(selectedFolder);
                    TableView<WebsitePasswordEntryWrapper> tableView = (TableView<WebsitePasswordEntryWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
                if(databasePasswordEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateDatabaseEntryPasswords(selectedFolder);
                    TableView<DatabasePasswordEntryWrapper> tableView = (TableView<DatabasePasswordEntryWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
                if(creditDebitCardEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateCreditDebitCardEntryPasswords(selectedFolder);
                    TableView<CreditDebitCardEntryWrapper> tableView = (TableView<CreditDebitCardEntryWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
                if(documentEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateDocumentEntryPasswords(selectedFolder);
                    TableView<DocumentWrapper> tableView = (TableView<DocumentWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
            }
        }
    }

    /**
     * Retrieves the Stage which contains a modal
     * @return Stage containing a modal
     */
    public static Stage getStage() {
        return stage;
    }
}
