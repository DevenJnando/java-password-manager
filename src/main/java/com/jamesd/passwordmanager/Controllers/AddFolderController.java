package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class which is responsible for adding a new folder into the password database once validation has passed
 */
public class AddFolderController extends ErrorChecker implements Initializable {

    /**
     * FXML fields
     */
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

    /**
     * Validation flags
     */
    private boolean folderNameEmptyFlag = false;
    private boolean folderTypeEmptyFlag = false;

    /**
     * IDs of error labels and the error messages they should display
     */
    private final String FOLDER_NAME_EMPTY_ID = "folderNameEmptyLabel";
    private final String FOLDER_NAME_EMPTY_ERROR_MSG = "Folder name cannot be empty!";
    private final String FOLDER_TYPE_EMPTY_ID = "folderTypeEmptyLabel";
    private final String FOLDER_TYPE_EMPTY_ERROR_MSG = "Folder type cannot be empty!";

    private static final Logger logger = LoggerFactory.getLogger(AddFolderController.class);

    /**
     * Default constructor
     */
    public AddFolderController() {

    }

    /**
     * Initialize method which sets labels, the folder name text field, the folder type combo box and buttons on screen.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLabels();
        setTextField();
        setComboBox();
        setButtons();
        addFolderVbox.getChildren().add(folderNameLabel);
        addFolderVbox.getChildren().add(folderNameTextField);
        addFolderVbox.getChildren().add(folderTypeLabel);
        addFolderVbox.getChildren().add(folderTypeComboBox);
        addFolderVbox.getChildren().add(addNewFolderButton);
    }

    /**
     * Method to set the labels which display the folder name and folder type
     */
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

    /**
     * Method which creates the add folder button
     */
    private void setButtons() {
        Button addNewFolderButton = new Button("Add Folder");
        addNewFolderButton.setId("addNewFolderButton");
        Insets insets = new Insets(20, 0, 0, 0);
        VBox.setMargin(addNewFolderButton, insets);
        addNewFolderButton.setOnAction(e -> {
            try {
                confirmAndAddNewFolder();
            } catch (GeneralSecurityException | ClassNotFoundException | IOException ex) {
                ex.printStackTrace();
            }
        });
        this.addNewFolderButton = addNewFolderButton;
    }

    /**
     * Method which creates the input field for the folder name
     */
    private void setTextField() {
        JFXTextField folderNameTextField = new JFXTextField();
        folderNameTextField.setId("folderNameTextField");
        this.folderNameTextField = folderNameTextField;
    }

    /**
     * Method which creates the dropdown combo box containing all potential folder types. These being web password,
     * database password, credit/debit card, passport and uploaded documents
     */
    private void setComboBox() {
        List<String> types = Arrays.asList(
                "WebPassword",
                "DatabasePassword",
                "CreditCard",
                "Document");
        ObservableList<String> oTypes = FXCollections.observableList(types);
        ComboBox<String> folderTypeComboBox = new ComboBox<>(oTypes);
        folderTypeComboBox.setId("folderTypeComboBox");
        Insets insets = new Insets(20, 0, 0, 0);
        VBox.setMargin(folderTypeComboBox, insets);
        this.folderTypeComboBox = folderTypeComboBox;
    }

    @Override
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

    @Override
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

    /**
     * Method which performs validation checks on the user's inputs and calls the addFolder method if validation
     * passes to add a new folder to the password database
     * @throws GeneralSecurityException Throws a LoginException if the user attempts to call this method whilst not
     * logged in
     * @throws ClassNotFoundException Throws a ClassNotFoundException if the PasswordEntryFolder class cannot be found
     */
    @FXML
    public void confirmAndAddNewFolder() throws GeneralSecurityException, ClassNotFoundException, IOException {
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

    /**
     * Method for adding a new folder to the database. Only called once validation has passed.
     * @throws LoginException Throws LoginException if the user attempts to call whilst not logged in
     * @throws ClassNotFoundException Throws ClassNotFoundException if the PasswordEntryFolder class cannot be found
     */
    private void addNewFolder() throws LoginException, ClassNotFoundException, IOException {
        PasswordEntryFolder newFolder = new PasswordEntryFolder(folderTypeComboBox.getSelectionModel().getSelectedItem(),
                folderNameTextField.getText());
        StoredPassSQLQueries.addNewPasswordFolderToDb(newFolder);
        PasswordManagerApp.getPasswordHomeController().populatePasswordFolders();
        setFolderTypeEmptyFlag(false);
        setFolderNameEmptyFlag(false);
        PasswordHomeController.getStage().close();
    }

    /**
     * Retrieves the flag for when the folder name is not set
     * @return true if not set, else false
     */
    public boolean isFolderNameEmptyFlag() {
        return folderNameEmptyFlag;
    }

    /**
     * Sets the flag for when the folder name is not set
     * @param folderNameEmptyFlag true if not set, else false
     */
    public void setFolderNameEmptyFlag(boolean folderNameEmptyFlag) {
        this.folderNameEmptyFlag = folderNameEmptyFlag;
    }

    /**
     * Retrieves the flag for when the folder type is not set
     * @return true if not set, else false
     */
    public boolean isFolderTypeEmptyFlag() {
        return folderTypeEmptyFlag;
    }

    /**
     * Sets the flag for when the folder type is not set
     * @param folderTypeEmptyFlag true if not set, else false
     */
    public void setFolderTypeEmptyFlag(boolean folderTypeEmptyFlag) {
        this.folderTypeEmptyFlag = folderTypeEmptyFlag;
    }
}
