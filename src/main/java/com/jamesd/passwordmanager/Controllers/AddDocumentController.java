package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StorageAccountManager;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddDocumentController extends NewPasswordController implements Initializable {

    public static Logger logger = LoggerFactory.getLogger(AddDocumentController.class);

    /**
     * FXML fields
     */
    @FXML
    VBox passwordVbox = new VBox();
    @FXML
    Label documentNameLabel = new Label();
    @FXML
    JFXTextArea documentDescription;
    @FXML
    Button confirmNewPasswordButton;

    /**
     * Validation flags
     */
    private Boolean folderNotSelectedFlag = false;
    private Boolean missingDocumentNameFlag = false;
    private Boolean fileNotSelectedFlag = false;

    private final Stage fileStage = new Stage();
    private final FileChooser fileChooser = new FileChooser();
    private File selectedFile;
    private String documentName;

    /**
     * IDs of error labels and the error messages they should display
     */
    private final String PASSWORD_FOLDER_NOT_SELECTED_ID = "passwordFolderNotSelected";
    private final String FILE_NOT_SELECTED_ID = "selectedFileEmptyLabel";
    private final String PASSWORD_FOLDER_NOT_SELECTED_ERROR_MSG = "Please select a folder to save this password in.";
    private final String FILE_NOT_SELECTED_ERROR_MSG = "Please select a file document to upload.";

    /**
     * Default constructor
     */
    public AddDocumentController() {

    }

    /**
     * Initialize method which sets text formatters on all input fields and sets icons
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTextFormatters();
        setIcons();
    }

    @Override
    protected void checkAndResetLabels() {
        if(getFolderNotSelectedFlag() || retrieveNode(PASSWORD_FOLDER_NOT_SELECTED_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_FOLDER_NOT_SELECTED_ID, passwordVbox);
            setFolderNotSelectedFlag(false);
        }
        if(getFileNotSelectedFlag() || retrieveNode(FILE_NOT_SELECTED_ID, passwordVbox) != null) {
            resetLabel(FILE_NOT_SELECTED_ID, passwordVbox);
            setFileNotSelectedFlag(false);
        }
    }

    @Override
    protected Boolean hasErroneousFields() {
        boolean erroneousFields = false;
        if(PasswordManagerApp.getSidebarController().getBaseAddPasswordController().getSelectedFolder() == null) {
            setErrorLabel(PASSWORD_FOLDER_NOT_SELECTED_ID, PASSWORD_FOLDER_NOT_SELECTED_ERROR_MSG, passwordVbox);
            setFolderNotSelectedFlag(true);
            erroneousFields = true;
        } if(selectedFile == null) {
            setErrorLabel(FILE_NOT_SELECTED_ID, FILE_NOT_SELECTED_ERROR_MSG, passwordVbox);
            setFileNotSelectedFlag(true);
            erroneousFields = true;
            logger.error("No file selected");
        }
        return erroneousFields;
    }

    @Override
    public void addNewPassword() throws ClassNotFoundException {
        String currentDate = LocalDate.now().toString();
        StorageAccountManager.uploadBlob(selectedFile, PasswordManagerApp.getSidebarController().getSelectedFolder().getPasswordFolder(), documentName);
        StoredPassSQLQueries.addNewDocumentToDb(PasswordManagerApp.getSidebarController()
                        .getBaseAddPasswordController().getSelectedFolder(),
                documentName,
                documentDescription.getText(),
                PasswordManagerApp.getLoggedInUser().getUsername(),
                PasswordManagerApp.getSidebarController().getBaseAddPasswordController()
                        .getSelectedFolder()
                        .getPasswordFolder()
                + "/" + documentName,
                currentDate);
        SidebarController.getStage().close();
        PasswordManagerApp.getPasswordHomeController().viewNewlyAddedPassword();
    }

    @Override
    protected void confirmAndAddNewPassword() throws GeneralSecurityException, ClassNotFoundException {
        if (PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();
            if (hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            }
            else if(PasswordManagerApp.getSidebarController().getBaseAddPasswordController().getSelectedFolder() != null
                    && !documentName.isEmpty()){
                addNewPassword();
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @Override
    public void setTextFormatters() {
        TextFormatter<String> textFormatter = PasswordCreateUtil.createTextFormatter(200);
        documentDescription.setTextFormatter(textFormatter);
    }


    /**
     * Uploads and overwrites the blob file with a newly selected one
     */
    @FXML
    public void selectFile() {
        File file = fileChooser.showOpenDialog(fileStage);
        if(file != null) {
            selectedFile = file;
            documentName = file.getName();
            documentNameLabel.setText(documentName);
        }
    }

    /**
     * Gets the flag which returns true if a folder has not been selected
     * @return True if no folder selected, else false
     */
    public Boolean getFolderNotSelectedFlag() {
        return this.folderNotSelectedFlag;
    }

    /**
     * Sets the flag which returns true if a folder has not been selected
     * @param folderNotSelectedFlag True if no folder selected, else false
     */
    public void setFolderNotSelectedFlag(Boolean folderNotSelectedFlag) {
        this.folderNotSelectedFlag = folderNotSelectedFlag;
    }

    /**
     * Gets the flag which returns true if the file document is not present
     * @return True if no document file has been selected, else false
     */
    public Boolean getFileNotSelectedFlag() {
        return this.fileNotSelectedFlag;
    }

    /**
     * Sets the flag which returns true if the file document is not present
     * @param fileNotSelectedFlag True if no document file has been selected, else false
     */
    public void setFileNotSelectedFlag(Boolean fileNotSelectedFlag) {
        this.fileNotSelectedFlag = fileNotSelectedFlag;
    }

}
