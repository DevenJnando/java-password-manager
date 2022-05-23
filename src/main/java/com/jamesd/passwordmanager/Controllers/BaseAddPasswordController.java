package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Base class for selecting which kind of password entry should be added to the password database. Each
 * of the other "add password" controllers are initialised from here depending on which password type is selected
 * by the user.
 */
public class BaseAddPasswordController implements Initializable {

    /**
     * FXML fields
     */
    @FXML
    private VBox baseAddPasswordVbox = new VBox();
    @FXML
    private ChoiceBox<String> passwordTypeChoiceBox = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> folderChoiceBox = new ChoiceBox<>();
    @FXML
    private AnchorPane addPasswordAnchorPane = new AnchorPane();

    private PasswordEntryFolder selectedFolder;

    /**
     * Default constructor
     */
    public BaseAddPasswordController() {

    }

    /**
     * Initialize method which keeps the folder choice box disabled until a password type has been selected, and
     * populates the password type choice box
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        folderChoiceBox.setDisable(true);
        populatePasswordTypeChoiceBox();
        baseAddPasswordVbox.getChildren().set(1, passwordTypeChoiceBox);
    }

    /**
     * Populates the password type choice box with all possible password types, these being:
     * website password, database password, credit/debit card, passport and uploadable document
     */
    private void populatePasswordTypeChoiceBox() {
        List<String> passwordTypes = List.of("Website password",
                "Database password",
                "Credit/Debit card",
                "Document");
        ChoiceBox<String> passwordTypeChoiceBox = new ChoiceBox<>(FXCollections.observableList(passwordTypes));

        // Adds a listener to call the loadAddPasswordView method with the selected type once selected by the user
        passwordTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            populateFolderChoiceBox(newVal);
            try {
                loadAddPasswordView(newVal);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        setPasswordTypeChoiceBox(passwordTypeChoiceBox);
    }

    /**
     * Converts the readable types for the user into types which will be stored in the database and used to determine
     * types of password entries
     * @param passwordType readable password type displayed to the user
     * @return type to be stored in the password entry/database
     */
    private String typeFinder(String passwordType) {
        String typeToReturn = "";
        switch (passwordType) {
            case "Website password":
                typeToReturn = "WebPassword";
                break;
            case "Database password":
                typeToReturn = "DatabasePassword";
                break;
            case "Credit/Debit card":
                typeToReturn = "CreditCard";
                break;
            case "Document":
                typeToReturn = passwordType;
                break;
        }
        return typeToReturn;
    }

    /**
     * Method to populate all folders of the selected password type into the folder choice box
     * @param passwordType password type to sort in-memory folders by
     */
    private void populateFolderChoiceBox(String passwordType) {

        // Consolidates all in-memory folders and sorts them by password type
        List<PasswordEntryFolder> folders = PasswordManagerApp.getSidebarController().getPasswordEntryFolders();
        List<String> relevantFolderNames = new ArrayList<>();
        folders.forEach(o -> {
                    if(o.getPasswordType().equals(typeFinder(passwordType))) {
                        relevantFolderNames.add(o.getPasswordFolder());
                    }
                });

        // Adds all folders with the selected type into the choice box and adds a listener to set the selected folder
        // upon user selection.
        ChoiceBox<String> folderChoiceBox = new ChoiceBox<>(FXCollections.observableList(relevantFolderNames));
        folderChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            for(PasswordEntryFolder folder : folders) {
                if(folder.getPasswordFolder().equals(newVal)) {
                    PasswordManagerApp.getSidebarController().getBaseAddPasswordController().setSelectedFolder(folder);
                }
            }
        });

        PasswordManagerApp.getSidebarController().getBaseAddPasswordController().setFolderChoiceBox(folderChoiceBox);

        if(this.folderChoiceBox.isDisabled()) {
            this.folderChoiceBox.setDisable(false);
        }

        baseAddPasswordVbox.getChildren().set(3, PasswordManagerApp.getSidebarController()
                .getBaseAddPasswordController().getFolderChoiceBox());
    }

    /**
     * Determines which "add password" controller to load into memory based on the password type selected by the user
     * @param passwordType The selected type of password entry
     * @throws IOException Throws an IOException if the controller class cannot be accessed
     */
    private void loadAddPasswordView(String passwordType) throws IOException {
        String viewToLoad = "/com/jamesd/passwordmanager/views/add-website-password-modal.fxml";
        Class<?> controllerClass = null;
        switch(passwordType) {
            case "Website password":
                viewToLoad = "/com/jamesd/passwordmanager/views/add-website-password-modal.fxml";
                controllerClass = AddWebsitePasswordController.class;
                break;
            case "Database password":
                viewToLoad = "/com/jamesd/passwordmanager/views/add-database-password-modal.fxml";
                controllerClass = AddDatabasePasswordController.class;
                break;
            case "Credit/Debit card":
                viewToLoad = "/com/jamesd/passwordmanager/views/add-credit-debit-card-view.fxml";
                controllerClass = AddCreditDebitCardController.class;
                break;
            case "Document":
                viewToLoad = "/com/jamesd/passwordmanager/views/add-document-view.fxml";
                controllerClass = AddDocumentController.class;
                break;
        }
        if(controllerClass != null) {
            FXMLLoader viewLoader = new FXMLLoader(controllerClass.getResource(viewToLoad));
            AnchorPane addPasswordAnchorPane = viewLoader.load();
            PasswordManagerApp.getSidebarController()
                    .getBaseAddPasswordController().setAddPasswordAnchorPane(addPasswordAnchorPane);
            baseAddPasswordVbox.getChildren().set(4, PasswordManagerApp.getSidebarController()
                    .getBaseAddPasswordController().getAddPasswordAnchorPane());
        }
    }

    /**
     * Retrieves the password type choice box
     * @return password type choice box
     */
    public ChoiceBox<String> getPasswordTypeChoiceBox() {
        return passwordTypeChoiceBox;
    }

    /**
     * Sets the password type choice box object
     * @param passwordTypeChoiceBox password type choice box
     */
    public void setPasswordTypeChoiceBox(ChoiceBox<String> passwordTypeChoiceBox) {
        this.passwordTypeChoiceBox = passwordTypeChoiceBox;
    }

    /**
     * Retrieves the folder choice box
     * @return folder choice box
     */
    public ChoiceBox<String> getFolderChoiceBox() {
        return folderChoiceBox;
    }

    /**
     * Sets the folder choice box
     * @param folderChoiceBox folder choice box
     */
    public void setFolderChoiceBox(ChoiceBox<String> folderChoiceBox) {
        this.folderChoiceBox = folderChoiceBox;
    }

    /**
     * Retrieves the main add password AnchorPane object
     * @return add password AnchorPane
     */
    public AnchorPane getAddPasswordAnchorPane() {
        return addPasswordAnchorPane;
    }

    /**
     * Sets the main add password AnchorPane object
     * @param addPasswordAnchorPane add password AnchorPane
     */
    public void setAddPasswordAnchorPane(AnchorPane addPasswordAnchorPane) {
        this.addPasswordAnchorPane = addPasswordAnchorPane;
    }

    /**
     * Retrieves the selected folder
     * @return PasswordEntryFolder object selected by user
     */
    public PasswordEntryFolder getSelectedFolder() {
        return selectedFolder;
    }

    /**
     * Sets the selected folder
     * @param selectedFolder PasswordEntryFolder object user is selecting
     */
    public void setSelectedFolder(PasswordEntryFolder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }
}
