package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StorageAccountManager;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.DocumentEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Wrappers.DocumentWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;

/**
 * Controller for the details screen of a DocumentEntry object
 */
public class DocumentDetailsController extends BasePasswordDetailsController<DocumentWrapper> implements Initializable {

    /**
     * FXML fields
     */
    @FXML
    private JFXTextArea documentDescription = new JFXTextArea();
    @FXML
    private JFXButton downloadBlobButton = new JFXButton();
    @FXML
    private JFXButton uploadBlobButton = new JFXButton();

    /**
     * Validation flag
     */
    private Boolean missingDocumentNameFlag;

    /**
     * IDs of error labels and the error messages they should display
     */
    private final String DOCUMENT_NAME_EMPTY_ID = "passwordNameEmptyLabel";
    private final String DOCUMENT_NAME_EMPTY_ERROR_MSG = "Please enter the name of this password.";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTextFormatters();
        setIcons();
    }

    @Override
    public void loadDeletePasswordModal() throws IOException {
        Stage deletePasswordStage = new Stage();
        FXMLLoader deletePasswordLoader = new FXMLLoader(DocumentDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/delete-document-modal.fxml"));
        AnchorPane deletePasswordPane = deletePasswordLoader.load();
        DocumentDetailsController controller = deletePasswordLoader.getController();
        controller.setEntryWrapper(getEntryWrapper());
        controller.setParentFolder(getParentFolder());
        Scene deletePasswordScene = new Scene(deletePasswordPane);
        deletePasswordStage.setScene(deletePasswordScene);
        deletePasswordStage.setTitle("Delete Password");
        deletePasswordStage.initOwner(PasswordManagerApp.getMainStage());
        deletePasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = deletePasswordStage;
        stage.showAndWait();
    }

    /**
     * Clears the details screen of all details
     */
    public void clear() {
        logoHbox.getChildren().clear();
        passwordNameField.clear();
        documentDescription.clear();
    }

    /**
     * Populates the details screen with the details of the selected DocumentEntry object for the user to view
     * @throws GeneralSecurityException Throws a LoginException if the user calls this method whilst not logged in
     */
    public void populatePasswordLayout() throws LoginException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            setPasswordIcons();
            setTextFormatters();
            detailsPane.setDisable(false);

            ImageView logo = new ImageView(getEntryWrapper().getFavicon().getImage());
            logo.setFitWidth(128);
            logo.setFitHeight(128);
            logoHbox.getChildren().add(logo);

            passwordNameField.setText(getEntryWrapper().getDocumentEntry().getPasswordName());
            documentDescription.setText(getEntryWrapper().getDocumentEntry().getDocumentDescription());
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @Override
    protected void checkAndResetLabels() {
        if(getMissingDocumentNameFlag() || retrieveNode(DOCUMENT_NAME_EMPTY_ID, passwordVbox) != null) {
            resetLabel(DOCUMENT_NAME_EMPTY_ID, passwordVbox);
            setMissingDocumentNameFlag(false);
        }
    }

    @Override
    protected Boolean hasErroneousFields() {
        boolean erroneousFields = false;
        if(passwordNameField.getText().isEmpty()) {
            setErrorLabel(DOCUMENT_NAME_EMPTY_ID, DOCUMENT_NAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingDocumentNameFlag(true);
            erroneousFields = true;
            logger.error("Password name is empty");
        }
        return erroneousFields;
    }

    @Override
    protected void setIcons() {
        logger.error("No icons to be set in this controller.");
    }

    /**
     * Downloads the Document file to the user's default download directory
     * @throws IOException throws IOException if the file cannot be downloaded
     */
    @FXML
    public void downloadBlob() throws IOException {
        StorageAccountManager.downloadBlob(getParentFolder().getPasswordFolder() + "/" + passwordNameField.getText());
    }

    /**
     * Calls the updatePassword method with the DatabasePasswordEntry details to be updated in the database and displays
     * a success message to the user on completion
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the plaintext password cannot be encrypted
     * @throws IOException Throws an IOException if the plaintext password cannot be read
     * @throws ClassNotFoundException Throws a ClassNotFoundException if the CustomTextField or CustomPasswordField
     * classes cannot be found
     */
    @FXML
    public void updatePassword() throws GeneralSecurityException, IOException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();
            if (hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            } else {
                Node password = passwordVbox.getChildren().get(1);
                updatePassword(password);
                showSavedLabel();
            }
        } else {
            logger.error("User is not logged in. Aborting process.");
        }
    }

    /**
     * Updates the selected DatabasePasswordEntry password in its parent folder in the database
     * @param password The displayed password field
     * @param <T> The class of the displayed password field (Should only ever be CustomTextField or CustomPasswordField)
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public <T> void updatePassword(T password) throws GeneralSecurityException, IOException, ClassNotFoundException {
        PasswordEntryFolder parentFolder = getParentFolder();
        DocumentEntry entry = getEntryWrapper().getDocumentEntry();
        if(password instanceof CustomTextField) {
            entry.setEncryptedPassword(EncryptDecryptPasswordsUtil.encryptPassword(((CustomTextField) password).getText()));
        } else if(password instanceof CustomPasswordField) {
            entry.setEncryptedPassword(EncryptDecryptPasswordsUtil.encryptPassword(((CustomPasswordField) password).getText()));
        } else {
            throw new ClassNotFoundException("Cannot cast password to type " + password.getClass());
        }
        entry.setPasswordName(passwordNameField.getText());
        entry.setMasterUsername(PasswordManagerApp.getLoggedInUser().getUsername());
        entry.setDocumentDescription(documentDescription.getText());
        entry.setDocumentStorageReference(parentFolder.getPasswordFolder() + "/" + entry.getPasswordName());
        StoredPassSQLQueries.updateDocumentInDb(entry, parentFolder);
        PasswordManagerApp.getPasswordHomeController().populateDatabaseEntryPasswords(parentFolder);
    }

    @Override
    protected void setTextFormatters() {
        TextFormatter<String> textFormatter = PasswordCreateUtil.createTextFormatter(200);
        documentDescription.setTextFormatter(textFormatter);
    }

    /**
     * Gets the flag which returns true if the name of the document is not present
     * @return True if no document name has been entered, else false
     */
    public Boolean getMissingDocumentNameFlag() {
        return this.missingDocumentNameFlag;
    }

    /**
     * Sets the flag which returns true if the name of the document is not present
     * @param missingDocumentNameFlag True if no document name has been entered, else false
     */
    public void setMissingDocumentNameFlag(Boolean missingDocumentNameFlag) {
        this.missingDocumentNameFlag = missingDocumentNameFlag;
    }
}
