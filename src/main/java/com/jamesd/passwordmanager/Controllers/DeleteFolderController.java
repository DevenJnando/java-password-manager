package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DeleteFolderController implements Initializable {

    @FXML
    private VBox deleteFolderVbox = new VBox();
    @FXML
    private Label folderNameLabel = new Label();
    @FXML
    private ChoiceBox<String> folderChoiceBox = new ChoiceBox<>();
    @FXML
    private Button deleteFolderButton = new Button();

    private PasswordEntryFolder selectedFolder;
    private static Stage modalStage = new Stage();
    private static Logger logger = LoggerFactory.getLogger(DeleteFolderController.class);

    public DeleteFolderController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLabels();
        setButtons();
        populateFolderChoiceBox();
        deleteFolderVbox.getChildren().add(getFolderNameLabel());
        deleteFolderVbox.getChildren().add(getFolderChoiceBox());
        deleteFolderVbox.getChildren().add(getDeleteFolderButton());
    }

    private void setLabels() {
        Label folderNameLabel = new Label("Folder to delete: ");
        Insets folderNameLabelInsets = new Insets(20, 0, 0, 0);
        folderNameLabel.setId("folderNameLabel");
        folderNameLabel.setPrefHeight(16);
        folderNameLabel.setPrefWidth(133);
        VBox.setMargin(folderNameLabel, folderNameLabelInsets);
        this.folderNameLabel = folderNameLabel;
    }

    private void setButtons() {
        Button deleteFolderButton = new Button("Delete Folder");
        deleteFolderButton.setId("deleteFolderButton");
        Insets insets = new Insets(20, 0, 0, 0);
        VBox.setMargin(deleteFolderButton, insets);
        deleteFolderButton.setOnAction(e -> {
            try {
                deleteFolder(e);
            } catch (LoginException ex) {
                ex.printStackTrace();
            }
        });
        this.deleteFolderButton = deleteFolderButton;
    }

    private void populateFolderChoiceBox() {
        List<PasswordEntryFolder> folders = PasswordManagerApp.getPasswordHomeController().getPasswordEntryFolders();
        List<String> folderNames = new ArrayList<>();
        folders.forEach(o -> {
            folderNames.add(o.getPasswordFolder());
        });
        ChoiceBox<String> folderChoiceBox = new ChoiceBox<>(FXCollections.observableList(folderNames));
        folderChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            for(PasswordEntryFolder folder : folders) {
                if(folder.getPasswordFolder().equals(newVal)) {
                    setSelectedFolder(folder);
                }
            }
        });
        setFolderChoiceBox(folderChoiceBox);
    }

    public void deleteFolder(Event event) throws LoginException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            confirmAction();
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    public void confirmAction() {
        Stage confirmationStage = new Stage();
        confirmationStage.initModality(Modality.WINDOW_MODAL);
        confirmationStage.initOwner(modalStage);
        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");
        confirmButton.setOnAction(e -> {
            try {
                confirmAndDeleteFolder();
                confirmationStage.close();
                PasswordHomeController.getStage().close();
            } catch (LoginException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        cancelButton.setOnAction(e -> {
            confirmationStage.close();
        });
        VBox confirmationVbox = new VBox(
                new Label("Are you sure you want to delete " + getSelectedFolder().getPasswordFolder() + "?"),
                confirmButton,
                cancelButton);
        confirmationStage.setScene(new Scene(confirmationVbox));
        confirmationStage.showAndWait();
    }

    public void confirmAndDeleteFolder() throws LoginException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            StoredPassSQLQueries.deletePasswordFolderInDb(getSelectedFolder());
            PasswordManagerApp.getPasswordHomeController().populatePasswordFolders();
            if(PasswordManagerApp.getPasswordHomeController().getSelectedFolder().equals(getSelectedFolder())) {
                PasswordEntryFolder firstFolderInList = PasswordManagerApp.getPasswordHomeController().getPasswordEntryFolders().get(0);
                PasswordManagerApp.getPasswordHomeController().populateWebsiteEntryPasswords(firstFolderInList);
            }
            PasswordManagerApp.getPasswordDetailsController().setNoDetailsLoaded();
        } else {
            throw new LoginException("User is not logged in. Aborting process");
        }
    }

    public PasswordEntryFolder getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(PasswordEntryFolder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public Label getFolderNameLabel() {
        return folderNameLabel;
    }

    public void setFolderNameLabel(Label folderNameLabel) {
        this.folderNameLabel = folderNameLabel;
    }

    public ChoiceBox<String> getFolderChoiceBox() {
        return folderChoiceBox;
    }

    public void setFolderChoiceBox(ChoiceBox<String> folderChoiceBox) {
        this.folderChoiceBox = folderChoiceBox;
    }

    public Button getDeleteFolderButton() {
        return deleteFolderButton;
    }

    public void setDeleteFolderButton(Button deleteFolderButton) {
        this.deleteFolderButton = deleteFolderButton;
    }
}
