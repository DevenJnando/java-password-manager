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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class BaseDetailsController implements Initializable {

    @FXML
    private VBox baseDetailsVbox = new VBox();
    @FXML
    private BorderPane detailsBorderPane = new BorderPane();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setNoDetailsLoaded();
    }

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

    public void prepareDetailsVbox() {
        BorderPane emptyBorderPane = new BorderPane();
        emptyBorderPane.setId("detailsBorderPane");
        emptyBorderPane.setPrefHeight(494.0);
        emptyBorderPane.setPrefWidth(824.0);
        baseDetailsVbox.getChildren().clear();
        baseDetailsVbox.getChildren().add(emptyBorderPane);
    }

    public void setWebDetailsBorderPane(WebsitePasswordEntryWrapper passwordEntry, PasswordEntryFolder parentFolder)
            throws IOException, InvalidAlgorithmParameterException, LoginException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        prepareDetailsVbox();
        FXMLLoader detailsLoader = new FXMLLoader(WebPasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/web-password-details.fxml"));
        detailsBorderPane = detailsLoader.load();
        WebPasswordDetailsController passwordDetailsController = detailsLoader.getController();
        loadWebPasswordDetailsView(passwordEntry, parentFolder, passwordDetailsController);
        baseDetailsVbox.getChildren().set(0, detailsBorderPane);
    }

    public void setDatabaseDetailsBorderPane(DatabasePasswordEntryWrapper databaseEntry, PasswordEntryFolder parentFolder)
            throws IOException, InvalidAlgorithmParameterException, LoginException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        prepareDetailsVbox();
        FXMLLoader detailsLoader = new FXMLLoader(DatabasePasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/database-password-details.fxml"));
        detailsBorderPane = detailsLoader.load();
        DatabasePasswordDetailsController databaseDetailsController = detailsLoader.getController();
        loadDatabasePasswordDetailsView(databaseEntry, parentFolder, databaseDetailsController);
        baseDetailsVbox.getChildren().set(0, detailsBorderPane);
    }

    public static void loadWebPasswordDetailsView(WebsitePasswordEntryWrapper passwordEntry, PasswordEntryFolder parentFolder,
                                                  WebPasswordDetailsController passwordDetailsController)
            throws IOException, InvalidAlgorithmParameterException, LoginException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        passwordDetailsController.clear();
        passwordDetailsController.setEntryWrapper(passwordEntry);
        passwordDetailsController.setParentFolder(parentFolder);
        passwordDetailsController.populatePasswordLayout();
    }

    public static void loadDatabasePasswordDetailsView(DatabasePasswordEntryWrapper databaseEntry, PasswordEntryFolder parentFolder,
                                                       DatabasePasswordDetailsController databaseDetailsController)
            throws InvalidAlgorithmParameterException, LoginException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {
        databaseDetailsController.clear();
        databaseDetailsController.setEntryWrapper(databaseEntry);
        databaseDetailsController.setParentFolder(parentFolder);
        databaseDetailsController.populatePasswordLayout();
    }

}
