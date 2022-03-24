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

public class BaseAddPasswordController implements Initializable {

    @FXML
    private VBox baseAddPasswordVbox = new VBox();
    @FXML
    private ChoiceBox<String> passwordTypeChoiceBox = new ChoiceBox<>();
    @FXML
    private ChoiceBox<String> folderChoiceBox = new ChoiceBox<>();
    @FXML
    private AnchorPane addPasswordAnchorPane = new AnchorPane();

    private PasswordEntryFolder selectedFolder;

    public BaseAddPasswordController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        folderChoiceBox.setDisable(true);
        populatePasswordTypeChoiceBox();
        baseAddPasswordVbox.getChildren().set(1, passwordTypeChoiceBox);
    }

    private void populatePasswordTypeChoiceBox() {
        List<String> passwordTypes = List.of("Website password",
                "Database password",
                "Credit/Debit card",
                "Passport",
                "Document");
        ChoiceBox<String> passwordTypeChoiceBox = new ChoiceBox<>(FXCollections.observableList(passwordTypes));
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
            case "Passport":
                typeToReturn = passwordType;
                break;
            case "Document":
                typeToReturn = passwordType;
                break;
        }
        return typeToReturn;
    }

    private void populateFolderChoiceBox(String passwordType) {
        List<PasswordEntryFolder> folders = PasswordManagerApp.getPasswordHomeController().getPasswordEntryFolders();
        List<String> relevantFolderNames = new ArrayList<>();
        folders.forEach(o -> {
                    if(o.getPasswordType().equals(typeFinder(passwordType))) {
                        relevantFolderNames.add(o.getPasswordFolder());
                    }
                });
        ChoiceBox<String> folderChoiceBox = new ChoiceBox<>(FXCollections.observableList(relevantFolderNames));
        folderChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            for(PasswordEntryFolder folder : folders) {
                if(folder.getPasswordFolder().equals(newVal)) {
                    PasswordManagerApp.getPasswordHomeController().getBaseAddPasswordController().setSelectedFolder(folder);
                }
            }
        });
        PasswordManagerApp.getPasswordHomeController()
                .getBaseAddPasswordController().setFolderChoiceBox(folderChoiceBox);
        if(this.folderChoiceBox.isDisabled()) {
            this.folderChoiceBox.setDisable(false);
        }
        baseAddPasswordVbox.getChildren().set(3, PasswordManagerApp.getPasswordHomeController()
                .getBaseAddPasswordController().getFolderChoiceBox());
    }

    private void loadAddPasswordView(String passwordType) throws IOException {
        String viewToLoad = "/com/jamesd/passwordmanager/views/add-website-password-modal.fxml";
        Class controllerClass = AddWebsitePasswordController.class;
        switch(passwordType) {
            case "Website password":
                viewToLoad = "/com/jamesd/passwordmanager/views/add-website-password-modal.fxml";
                controllerClass = AddWebsitePasswordController.class;
                break;
            case "Database password":
                //TODO: replace with actual view + controller once finished
                viewToLoad = "/com/jamesd/passwordmanager/views/add-database-password-modal.fxml";
                controllerClass = AddDatabasePasswordController.class;
                break;
            case "Credit/Debit card":
                viewToLoad = "/com/jamesd/passwordmanager/views/add-website-password-modal.fxml";
                controllerClass = AddWebsitePasswordController.class;
                break;
            case "Passport":
                //TODO: replace with actual view + controller once finished
                viewToLoad = "/com/jamesd/passwordmanager/views/add-website-password-modal.fxml";
                controllerClass = AddWebsitePasswordController.class;
                break;
            case "Document":
                //TODO: replace with actual view + controller once finished
                viewToLoad = "/com/jamesd/passwordmanager/views/add-website-password-modal.fxml";
                controllerClass = AddWebsitePasswordController.class;
                break;
        }
        FXMLLoader viewLoader = new FXMLLoader(controllerClass.getResource(viewToLoad));
        AnchorPane addPasswordAnchorPane = viewLoader.load();
        PasswordManagerApp.getPasswordHomeController()
                .getBaseAddPasswordController().setAddPasswordAnchorPane(addPasswordAnchorPane);
        baseAddPasswordVbox.getChildren().set(4, PasswordManagerApp.getPasswordHomeController()
                .getBaseAddPasswordController().getAddPasswordAnchorPane());
    }

    public ChoiceBox<String> getPasswordTypeChoiceBox() {
        return passwordTypeChoiceBox;
    }

    public void setPasswordTypeChoiceBox(ChoiceBox<String> passwordTypeChoiceBox) {
        this.passwordTypeChoiceBox = passwordTypeChoiceBox;
    }

    public ChoiceBox<String> getFolderChoiceBox() {
        return folderChoiceBox;
    }

    public void setFolderChoiceBox(ChoiceBox<String> folderChoiceBox) {
        this.folderChoiceBox = folderChoiceBox;
    }

    public AnchorPane getAddPasswordAnchorPane() {
        return addPasswordAnchorPane;
    }

    public void setAddPasswordAnchorPane(AnchorPane addPasswordAnchorPane) {
        this.addPasswordAnchorPane = addPasswordAnchorPane;
    }

    public PasswordEntryFolder getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(PasswordEntryFolder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }
}
