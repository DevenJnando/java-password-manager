package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AddFolderController extends BaseController implements Initializable {

    @FXML
    private VBox addFolderVbox = new VBox();
    @FXML
    private Label folderNameLabel = new Label();
    @FXML
    private Label folderTypeLabel = new Label();
    @FXML
    private JFXTextField folderNameTextField = new JFXTextField();
    @FXML
    private ComboBox<String> folderTypeComboBox = new ComboBox<>();
    @FXML
    private Button addNewFolderButton = new Button();

    private boolean folderNameEmptyFlag = false;
    private boolean folderTypeEmptyFlag = false;

    private final String FOLDER_NAME_EMPTY_ID = "folderNameEmptyLabel";
    private final String FOLDER_NAME_EMPTY_ERROR_MSG = "Folder name cannot be empty!";
    private final String FOLDER_TYPE_EMPTY_ID = "folderTypeEmptyLabel";
    private final String FOLDER_TYPE_EMPTY_ERROR_MSG = "Folder type cannot be empty!";

    private static final Logger logger = LoggerFactory.getLogger(AddFolderController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLabels();
        setTextField();
        setComboBox();
        setButton();
        addFolderVbox.getChildren().add(folderNameLabel);
        addFolderVbox.getChildren().add(folderNameTextField);
        addFolderVbox.getChildren().add(folderTypeLabel);
        addFolderVbox.getChildren().add(folderTypeComboBox);
        addFolderVbox.getChildren().add(addNewFolderButton);
    }

    private void setLabels() {
        Label folderNameLabel = new Label("Folder Name: ");
        Insets folderNameLabelInsets = new Insets(20, 0, 0, 0);
        folderNameLabel.setId("folderNameLabel");
        folderNameLabel.setPrefHeight(16);
        folderNameLabel.setPrefWidth(133);
        VBox.setMargin(folderNameLabel, folderNameLabelInsets);
        Label folderTypeLabel = new Label("Folder Type: ");
        Insets folderTypeLabelInsets = new Insets(20, 0, 0, 0);
        folderTypeLabel.setId("folderTypeLabel");
        folderTypeLabel.setPrefHeight(16);
        folderTypeLabel.setPrefWidth(133);
        VBox.setMargin(folderTypeLabel, folderTypeLabelInsets);
        this.folderNameLabel = folderNameLabel;
        this.folderTypeLabel = folderTypeLabel;
    }

    private void setButton() {
        Button addNewFolderButton = new Button("Add Folder");
        addNewFolderButton.setId("addNewFolderButton");
        Insets insets = new Insets(20, 0, 0, 0);
        VBox.setMargin(addNewFolderButton, insets);
        addNewFolderButton.setOnAction(e -> {
            try {
                confirmAndAddNewFolder(e);
            } catch (GeneralSecurityException ex) {
                ex.printStackTrace();
            }
        });
        this.addNewFolderButton = addNewFolderButton;
    }

    private void setTextField() {
        JFXTextField folderNameTextField = new JFXTextField();
        folderNameTextField.setId("folderNameTextField");
        this.folderNameTextField = folderNameTextField;
    }

    private void setComboBox() {
        List<String> types = Arrays.asList(
                "WebPassword",
                "DatabasePassword",
                "CreditCard",
                "Passport",
                "Document");
        ObservableList<String> oTypes = FXCollections.observableList(types);
        ComboBox<String> folderTypeComboBox = new ComboBox<>(oTypes);
        folderTypeComboBox.setId("folderTypeComboBox");
        Insets insets = new Insets(20, 0, 0, 0);
        VBox.setMargin(folderTypeComboBox, insets);
        this.folderTypeComboBox = folderTypeComboBox;
    }

    protected void checkAndResetLabels() {
        if(isFolderNameEmptyFlag() || retrieveNode(FOLDER_NAME_EMPTY_ID, addFolderVbox) != null) {
            resetLabel(FOLDER_NAME_EMPTY_ID, addFolderVbox);
            setFolderNameEmptyFlag(false);
        }
        if(isFolderTypeEmptyFlag() || retrieveNode(FOLDER_TYPE_EMPTY_ID, addFolderVbox) != null) {
            resetLabel(FOLDER_TYPE_EMPTY_ID, addFolderVbox);
            setFolderTypeEmptyFlag(false);
        }
    }

    protected Boolean hasErroneousFields() {
        boolean erroneousFields = false;
        if (folderNameTextField.getText().isEmpty()) {
            setErrorLabel(FOLDER_NAME_EMPTY_ID, FOLDER_NAME_EMPTY_ERROR_MSG, addFolderVbox);
            setFolderNameEmptyFlag(true);
            erroneousFields = true;
            logger.error("Folder name is empty");
        } if (folderTypeComboBox.getSelectionModel().isEmpty()) {
            setErrorLabel(FOLDER_TYPE_EMPTY_ID, FOLDER_TYPE_EMPTY_ERROR_MSG, addFolderVbox);
            setFolderTypeEmptyFlag(true);
            erroneousFields = true;
            logger.error("No folder type selected.");
        }
        return erroneousFields;
    }

    @FXML
    public void confirmAndAddNewFolder(Event event) throws GeneralSecurityException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();
            if(hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            } else {
                addNewFolder();
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    private void addNewFolder() {
        PasswordEntryFolder newFolder = new PasswordEntryFolder(folderTypeComboBox.getSelectionModel().getSelectedItem(),
                folderNameTextField.getText());
        StoredPassSQLQueries.addNewPasswordFolderToDb(newFolder);
        setFolderTypeEmptyFlag(false);
        setFolderNameEmptyFlag(false);
        PasswordHomeController.getStage().close();
    }

    public boolean isFolderNameEmptyFlag() {
        return folderNameEmptyFlag;
    }

    public void setFolderNameEmptyFlag(boolean folderNameEmptyFlag) {
        this.folderNameEmptyFlag = folderNameEmptyFlag;
    }

    public boolean isFolderTypeEmptyFlag() {
        return folderTypeEmptyFlag;
    }

    public void setFolderTypeEmptyFlag(boolean folderTypeEmptyFlag) {
        this.folderTypeEmptyFlag = folderTypeEmptyFlag;
    }
}