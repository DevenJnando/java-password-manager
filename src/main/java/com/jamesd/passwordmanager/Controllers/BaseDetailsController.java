package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;

/**
 * Base class for displaying every form of entry details.
 * The specific details screen to load is determined by the type of entry selected by the user.
 */
public class BaseDetailsController implements Initializable {

    /**
     * FXML fields
     */
    @FXML
    private VBox baseDetailsVbox = new VBox();
    @FXML
    private BorderPane detailsBorderPane = new BorderPane();

    /**
     * Initialize method which sets the initial details screen as a "no passwords loaded" screen.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setNoDetailsLoaded();
    }

    /**
     * Populates the baseDetailsVbox field with a screen informing the user that no password details are currently loaded.
     * It also provides instructions on how to select a password from a password folder
     */
    public void setNoDetailsLoaded() {
        Label noPasswordLoadedLabel = new Label("No Password Selected...");
        noPasswordLoadedLabel.setFont(new Font(30));
        Insets noPasswordLoadedInsets = new Insets(200, 0, 0 ,250);
        Label selectFolderLabel = new Label("Select a folder from the sidepane on the left");
        selectFolderLabel.setFont(new Font(20));
        Insets selectFolderInsets = new Insets(100, 0, 0, 180);
        Label selectPasswordLabel = new Label("And select a password from that folder from the table at the bottom.");
        selectPasswordLabel.setFont(new Font(20));
        Insets selectPasswordInsets = new Insets(25, 0, 0, 75);
        VBox.setMargin(noPasswordLoadedLabel, noPasswordLoadedInsets);
        VBox.setMargin(selectFolderLabel, selectFolderInsets);
        VBox.setMargin(selectPasswordLabel, selectPasswordInsets);
        baseDetailsVbox.getChildren().clear();
        baseDetailsVbox.getChildren().add(noPasswordLoadedLabel);
        baseDetailsVbox.getChildren().add(selectFolderLabel);
        baseDetailsVbox.getChildren().add(selectPasswordLabel);
    }

    /**
     * Clears whatever view currently populates the baseDetailsVbox in preparation for the new view which will take its
     * place
     */
    public void prepareDetailsVbox() {
        BorderPane emptyBorderPane = new BorderPane();
        emptyBorderPane.setId("detailsBorderPane");
        emptyBorderPane.setPrefHeight(494.0);
        emptyBorderPane.setPrefWidth(824.0);
        baseDetailsVbox.getChildren().clear();
        baseDetailsVbox.getChildren().add(emptyBorderPane);
    }

    /**
     * Populates the baseDetailsVbox with the "Web password details" view. Called when a website password entry is
     * selected by the user
     * @param passwordEntry Password selected by user
     * @param parentFolder Parent folder the selected password belongs to
     * @throws IOException Throws IOException if the specified view cannot be loaded
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the user calls this method whilst not logged
     * in, or if the password retrieved from the database cannot successfully be decrypted.
     */
    public void setWebDetailsBorderPane(WebsitePasswordEntryWrapper passwordEntry, PasswordEntryFolder parentFolder)
            throws IOException, GeneralSecurityException {
        prepareDetailsVbox();
        FXMLLoader detailsLoader = new FXMLLoader(WebPasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/web-password-details.fxml"));
        detailsBorderPane = detailsLoader.load();
        WebPasswordDetailsController passwordDetailsController = detailsLoader.getController();
        loadWebPasswordDetailsView(passwordEntry, parentFolder, passwordDetailsController);
        baseDetailsVbox.getChildren().set(0, detailsBorderPane);
    }

    /**
     * Populates the baseDetailsVbox with the "Database password details" view. Called when a database password entry is
     * selected by the user
     * @param databaseEntry Password selected by user
     * @param parentFolder Parent folder the selected password belongs to
     * @throws IOException Throws IOException if the specified view cannot be loaded
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the user calls this method whilst not logged
     * in, or if the password retrieved from the database cannot successfully be decrypted.
     */
    public void setDatabaseDetailsBorderPane(DatabasePasswordEntryWrapper databaseEntry, PasswordEntryFolder parentFolder)
            throws IOException, GeneralSecurityException {
        prepareDetailsVbox();
        FXMLLoader detailsLoader = new FXMLLoader(DatabasePasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/database-password-details.fxml"));
        detailsBorderPane = detailsLoader.load();
        DatabasePasswordDetailsController databaseDetailsController = detailsLoader.getController();
        loadDatabasePasswordDetailsView(databaseEntry, parentFolder, databaseDetailsController);
        baseDetailsVbox.getChildren().set(0, detailsBorderPane);
    }

    /**
     * Initialises the WebPasswordDetailsController by first clearing anything which may have been loaded already,
     * setting the selected password and parent folder, and displaying the website password entry details to the user
     * @param passwordEntry Password selected by user
     * @param parentFolder Parent folder the selected password belongs to
     * @throws IOException Throws IOException if the specified view cannot be loaded
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the user calls this method whilst not logged
     * in, or if the password retrieved from the database cannot successfully be decrypted.
     */
    public static void loadWebPasswordDetailsView(WebsitePasswordEntryWrapper passwordEntry, PasswordEntryFolder parentFolder,
                                                  WebPasswordDetailsController passwordDetailsController)
            throws IOException, GeneralSecurityException {
        passwordDetailsController.clear();
        passwordDetailsController.setEntryWrapper(passwordEntry);
        passwordDetailsController.setParentFolder(parentFolder);
        passwordDetailsController.populatePasswordLayout();
    }

    /**
     * Initialises the DatabasePasswordDetailsController by first clearing anything which may have been loaded already,
     * setting the selected password and parent folder, and displaying the database password entry details to the user
     * @param databaseEntry Password selected by user
     * @param parentFolder Parent folder the selected password belongs to
     * @throws IOException Throws IOException if the specified view cannot be loaded
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the user calls this method whilst not logged
     * in, or if the password retrieved from the database cannot successfully be decrypted.
     */
    public static void loadDatabasePasswordDetailsView(DatabasePasswordEntryWrapper databaseEntry, PasswordEntryFolder parentFolder,
                                                       DatabasePasswordDetailsController databaseDetailsController)
            throws IOException, GeneralSecurityException {
        databaseDetailsController.clear();
        databaseDetailsController.setEntryWrapper(databaseEntry);
        databaseDetailsController.setParentFolder(parentFolder);
        databaseDetailsController.populatePasswordLayout();
    }

}
