package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import javax.security.auth.login.LoginException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddDatabasePasswordController extends NewPasswordController implements Initializable {

    @FXML
    VBox passwordVbox = new VBox();
    @FXML
    JFXTextField passwordName;
    @FXML
    JFXTextField hostnameField;
    @FXML
    JFXTextField databaseName;
    @FXML
    JFXTextField databaseUsername;
    @FXML
    Button confirmNewPasswordButton;

    private Boolean folderNotSelectedFlag = false;
    private Boolean missingPasswordNameFlag = false;
    private Boolean missingHostnameFlag = false;
    private Boolean missingDatabaseNameFlag = false;
    private Boolean missingDatabaseUsernameFlag = false;

    private final String PASSWORD_FOLDER_NOT_SELECTED_ID = "passwordFolderNotSelected";
    private final String PASSWORD_NAME_EMPTY_ID = "passwordNameEmptyLabel";
    private final String HOSTNAME_EMPTY_ID = "hostnameEmptyLabel";
    private final String DATABASE_NAME_EMPTY_ID = "databaseNameEmptyLabel";
    private final String DATABASE_USERNAME_EMPTY_ID = "databaseUsernameEmptyLabel";
    private final String PASSWORD_FOLDER_NOT_SELECTED_ERROR_MSG = "Please select a folder to save this password in.";
    private final String PASSWORD_NAME_EMPTY_ERROR_MSG = "Please enter the name of this password.";
    private final String HOSTNAME_EMPTY_ERROR_MSG = "Please enter the hostname of this database.";
    private final String DATABASE_NAME_EMPTY_ERROR_MSG = "Please enter the name of this database.";
    private final String DATABASE_USERNAME_EMPTY_ERROR_MSG = "Please enter the username this password belongs to.";

    public AddDatabasePasswordController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFormatters();
        setIcons();
        attachStrengthListener("Enter Password: ");
    }

    @Override
    protected Boolean hasErroneousFields() {
        boolean erroneousFields = false;
        if(PasswordManagerApp.getPasswordHomeController().getBaseAddPasswordController().getSelectedFolder() == null) {
            setErrorLabel(PASSWORD_FOLDER_NOT_SELECTED_ID, PASSWORD_FOLDER_NOT_SELECTED_ERROR_MSG, passwordVbox);
            setFolderNotSelectedFlag(true);
            erroneousFields = true;
        } if(passwordName.getText().isEmpty()) {
            setErrorLabel(PASSWORD_NAME_EMPTY_ID, PASSWORD_NAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingPasswordNameFlag(true);
            erroneousFields = true;
            logger.error("Password name is empty");
        } if(hostnameField.getText().isEmpty()) {
            setErrorLabel(HOSTNAME_EMPTY_ID, HOSTNAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingHostnameFlag(true);
            erroneousFields = true;
            logger.error("Hostname is missing.");
        } if(databaseName.getText().isEmpty()) {
            setErrorLabel(DATABASE_NAME_EMPTY_ID, DATABASE_NAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingDatabaseNameFlag(true);
            erroneousFields = true;
            logger.error("Database name is missing.");
        } if(databaseUsername.getText().isEmpty()) {
            setErrorLabel(DATABASE_USERNAME_EMPTY_ID, DATABASE_USERNAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingDatabaseUsernameFlag(true);
            erroneousFields = true;
            logger.error("Database username is missing.");
        } if(!hiddenPasswordField.getText().equals(hiddenConfirmPasswordField.getText())
                || !visiblePasswordField.getText().equals(visibleConfirmPasswordField.getText())) {
            setErrorLabel(PASSWORD_MISMATCH_ID, PASSWORD_MISMATCH_ERROR_MSG, passwordVbox);
            setMismatchedPasswordsFlag(true);
            erroneousFields = true;
            logger.error("Passwords do not match.");
        }
        return erroneousFields;
    }

    @Override
    public void addNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException {
        String currentDate = LocalDate.now().toString();
        String hashedPassword = "";
        if(confirmPasswordToggler.getShowPassword()) {
            hashedPassword = EncryptDecryptPasswordsUtil.encryptPassword(visibleConfirmPasswordField.getText());
        } else {
            hashedPassword = EncryptDecryptPasswordsUtil.encryptPassword(hiddenConfirmPasswordField.getText());
        }
        StoredPassSQLQueries.addNewDatabasePasswordToDb(PasswordManagerApp.getPasswordHomeController()
                        .getBaseAddPasswordController().getSelectedFolder(),
                passwordName.getText(),
                hostnameField.getText(),
                PasswordManagerApp.getLoggedInUser().getUsername(),
                databaseName.getText(),
                databaseUsername.getText(),
                currentDate,
                hashedPassword);
        setMissingDatabaseUsernameFlag(false);
        setMismatchedPasswordsFlag(false);
        PasswordHomeController.getStage().close();
        PasswordManagerApp.getPasswordHomeController().viewNewlyAddedPassword();
    }

    @Override
    public void confirmAndAddNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException {
        if (PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();

            if (hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            }
            else if(PasswordManagerApp.getPasswordHomeController().getBaseAddPasswordController().getSelectedFolder() != null
                    && !passwordName.getText().isEmpty()
                    && !hostnameField.getText().isEmpty()
                    && !databaseName.getText().isEmpty()
                    && !databaseUsername.getText().isEmpty()
                    && (hiddenPasswordField.getText().equals(hiddenConfirmPasswordField.getText()))
                    || visiblePasswordField.getText().equals(visibleConfirmPasswordField.getText())){
                if(getPasswordIsAcceptableFlag()) {
                    addNewPassword();
                } else {
                    setErrorLabel(PASSWORD_TOO_WEAK_ID, PASSWORD_TOO_WEAK_ERROR_MSG, passwordVbox);
                    setPasswordIsAcceptableFlag(false);
                    logger.error("Password is too weak.");
                }
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @Override
    public void checkAndResetLabels() {
        if(retrieveNode(PASSWORD_TOO_WEAK_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_TOO_WEAK_ID, passwordVbox);
        }
        if(getFolderNotSelectedFlag() || retrieveNode(PASSWORD_FOLDER_NOT_SELECTED_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_FOLDER_NOT_SELECTED_ID, passwordVbox);
            setFolderNotSelectedFlag(false);
        }
        if(getMismatchedPasswordsFlag() || retrieveNode(PASSWORD_MISMATCH_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_MISMATCH_ID, passwordVbox);
            setMismatchedPasswordsFlag(false);
        }
        if(isMissingPasswordNameFlag() || retrieveNode(PASSWORD_NAME_EMPTY_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_NAME_EMPTY_ID, passwordVbox);
            setMissingPasswordNameFlag(false);
        }
        if(getMissingHostnameFlag() || retrieveNode(HOSTNAME_EMPTY_ID, passwordVbox) != null) {
            resetLabel(HOSTNAME_EMPTY_ID, passwordVbox);
            setMissingHostnameFlag(false);
        }
        if(getMissingDatabaseNameFlag() || retrieveNode(DATABASE_NAME_EMPTY_ID, passwordVbox) != null) {
            resetLabel(DATABASE_NAME_EMPTY_ID, passwordVbox);
            setMissingDatabaseNameFlag(false);
        }
        if(getMissingDatabaseUsernameFlag() || retrieveNode(DATABASE_USERNAME_EMPTY_ID, passwordVbox) != null) {
            resetLabel(DATABASE_USERNAME_EMPTY_ID, passwordVbox);
            setMissingDatabaseUsernameFlag(false);
        }
    }

    @Override
    public void setTextFormatters() {
        TextFormatter<String> textFormatter1 = PasswordCreateUtil.createTextFormatter(32);
        TextFormatter<String> textFormatter2 = PasswordCreateUtil.createTextFormatter(32);
        TextFormatter<String> textFormatter3 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter1 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter2 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter3 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter4 = PasswordCreateUtil.createTextFormatter(24);
        hostnameField.setTextFormatter(textFormatter1);
        databaseName.setTextFormatter(textFormatter2);
        databaseUsername.setTextFormatter(textFormatter3);
        hiddenPasswordField.setTextFormatter(passwordFormatter1);
        hiddenConfirmPasswordField.setTextFormatter(passwordFormatter2);
        visiblePasswordField.setTextFormatter(passwordFormatter3);
        visibleConfirmPasswordField.setTextFormatter(passwordFormatter4);
    }

    private boolean getFolderNotSelectedFlag() {
        return folderNotSelectedFlag;
    }

    private boolean isMissingPasswordNameFlag() {
        return missingPasswordNameFlag;
    }

    private boolean getMissingHostnameFlag() {
        return missingHostnameFlag;
    }

    private boolean getMissingDatabaseNameFlag() {
        return missingDatabaseNameFlag;
    }

    private boolean getMissingDatabaseUsernameFlag() {
        return missingDatabaseUsernameFlag;
    }

    private void setFolderNotSelectedFlag(boolean folderNotSelectedFlag) {
        this.folderNotSelectedFlag = folderNotSelectedFlag;
    }

    private void setMissingPasswordNameFlag(boolean missingPasswordNameFlag) {
        this.missingPasswordNameFlag = missingPasswordNameFlag;
    }

    private void setMissingHostnameFlag(boolean missingHostnameFlag) {
        this.missingHostnameFlag = missingHostnameFlag;
    }

    private void setMissingDatabaseNameFlag(boolean missingDatabaseNameFlag) {
        this.missingDatabaseNameFlag = missingDatabaseNameFlag;
    }

    private void setMissingDatabaseUsernameFlag(boolean missingDatabaseUsernameFlag) {
        this.missingDatabaseUsernameFlag = missingDatabaseUsernameFlag;
    }
}
