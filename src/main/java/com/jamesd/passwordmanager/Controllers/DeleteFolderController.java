package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import javafx.collections.FXCollections;
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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller responsible for deleting a folder and all passwords contained within
 */
public class DeleteFolderController implements Initializable {

    /**
     * FXML fields
     */
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

    /**
     * Default constructor
     */
    public DeleteFolderController() {

    }

    /**
     * Initialize method which populates the labels, buttons and folder choice box in the view and adds them to the
     * deleteFolderVbox field
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLabels();
        setButtons();
        populateFolderChoiceBox();
        deleteFolderVbox.getChildren().add(getFolderNameLabel());
        deleteFolderVbox.getChildren().add(getFolderChoiceBox());
        deleteFolderVbox.getChildren().add(getDeleteFolderButton());
    }

    /**
     * Creates the "Folder to delete" label and applies insets
     */
    private void setLabels() {
        Label folderNameLabel = new Label("Folder to delete: ");
        Insets folderNameLabelInsets = new Insets(20, 0, 0, 0);
        folderNameLabel.setId("folderNameLabel");
        folderNameLabel.setPrefHeight(16);
        folderNameLabel.setPrefWidth(133);
        VBox.setMargin(folderNameLabel, folderNameLabelInsets);
        this.folderNameLabel = folderNameLabel;
    }

    /**
     * Creates the "Delete folder" button, applies insets and adds a listener which calls the deleteFolder method
     * on click
     */
    private void setButtons() {
        Button deleteFolderButton = new Button("Delete Folder");
        deleteFolderButton.setId("deleteFolderButton");
        Insets insets = new Insets(20, 0, 0, 0);
        VBox.setMargin(deleteFolderButton, insets);
        deleteFolderButton.setOnAction(e -> {
            try {
                deleteFolder();
            } catch (LoginException ex) {
                ex.printStackTrace();
            }
        });
        this.deleteFolderButton = deleteFolderButton;
    }

    /**
     * Retrieves a list of all folders and adds them to a ChoiceBox object. Also adds a listener to the ChoiceBox
     * to call the setSelectedFolder method once the user has made a selection
     */
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

    /**
     * Action assigned to the "Delete folder" button. Checks the user is logged in and if they are,  prompts the user
     * to confirm that the folder they have selected is the one they wish to delete
     * @throws LoginException Throws LoginException if the user calls this method whilst not logged in
     */
    public void deleteFolder() throws LoginException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            confirmAction();
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Opens a new modal prompting the user if they wish to delete the selected folder, or if they wish to cancel. If
     * the user confirms, the confirmAndDeleteFolder method is called. If the user cancels, the modal is closed and no
     * action occurs
     */
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
            } catch (LoginException | ClassNotFoundException | IOException ex) {
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

    /**
     * Method called once the user has confirmed that they wish to delete the selected folder. Deletes the folder from
     * the password database and reloads the list of folders to the user after a successful deletion
     * @throws LoginException
     * @throws ClassNotFoundException
     */
    public void confirmAndDeleteFolder() throws LoginException, ClassNotFoundException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            StoredPassSQLQueries.deletePasswordFolderInDb(getSelectedFolder());
            logger.info("Folder " + getSelectedFolder().getPasswordFolder() + " deleted successfully");
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

    /**
     * Retrieves the folder selected for deletion
     * @return PasswordEntryFolder object selected by user
     */
    public PasswordEntryFolder getSelectedFolder() {
        return selectedFolder;
    }

    /**
     * Sets the folder selected for deletion
     * @param selectedFolder PasswordEntryFolder object selected by user
     */
    public void setSelectedFolder(PasswordEntryFolder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    /**
     * Retrieves the "Folder to delete" label
     * @return Label that indicates which folder has been selected for deletion
     */
    public Label getFolderNameLabel() {
        return folderNameLabel;
    }

    /**
     * Sets the "Folder to delete" label
     * @param folderNameLabel Label that indicates which folder has been selected for deletion
     */
    public void setFolderNameLabel(Label folderNameLabel) {
        this.folderNameLabel = folderNameLabel;
    }

    /**
     * Retrieves the ChoiceBox object containing all selectable folders
     * @return ChoiceBox containing all folders
     */
    public ChoiceBox<String> getFolderChoiceBox() {
        return folderChoiceBox;
    }

    /**
     * Sets the ChoiceBox object containing all selectable folders
     * @param folderChoiceBox ChoiceBox containing all folders
     */
    public void setFolderChoiceBox(ChoiceBox<String> folderChoiceBox) {
        this.folderChoiceBox = folderChoiceBox;
    }

    /**
     * Retrieves the "Delete folder" button
     * @return Button to open "Confirm delete" modal
     */
    public Button getDeleteFolderButton() {
        return deleteFolderButton;
    }

    /**
     * Sets the "Delete folder" button
     * @param deleteFolderButton Button to open "Confirm delete" modal
     */
    public void setDeleteFolderButton(Button deleteFolderButton) {
        this.deleteFolderButton = deleteFolderButton;
    }
}
