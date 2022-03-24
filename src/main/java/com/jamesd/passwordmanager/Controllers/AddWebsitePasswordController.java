package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddWebsitePasswordController extends NewPasswordController implements Initializable {

    public static Logger logger = LoggerFactory.getLogger(AddWebsitePasswordController.class);

    @FXML
    VBox passwordVbox = new VBox();
    @FXML
    JFXTextField passwordName;
    @FXML
    JFXTextField urlField;
    @FXML
    JFXTextField siteUsername;
    @FXML
    Button confirmNewPasswordButton;

    private Boolean folderNotSelectedFlag = false;
    private Boolean missingPasswordNameFlag = false;
    private Boolean missingUrlFlag = false;
    private Boolean missingUsernameFlag = false;

    private final String PASSWORD_FOLDER_NOT_SELECTED_ID = "passwordFolderNotSelected";
    private final String PASSWORD_NAME_EMPTY_ID = "passwordNameEmptyLabel";
    private final String URL_EMPTY_ID = "urlFieldEmptyLabel";
    private final String SITE_USERNAME_EMPTY_ID = "siteUsernameEmptyLabel";
    private final String PASSWORD_FOLDER_NOT_SELECTED_ERROR_MSG = "Please select a folder to save this password in.";
    private final String PASSWORD_NAME_EMPTY_ERROR_MSG = "Please enter the name of this password.";
    private final String URL_EMPTY_ERROR_MSG = "Please enter the URL this password belongs to.";
    private final String SITE_USERNAME_EMPTY_ERROR_MSG = "Please enter the username/email address for this website.";


    public AddWebsitePasswordController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFormatters();
        setIcons();
        attachStrengthListener("Enter Password: ");
    }

    @Override
    public void setTextFormatters() {
        TextFormatter<String> textFormatter1 = PasswordCreateUtil.createTextFormatter(32);
        TextFormatter<String> textFormatter2 = PasswordCreateUtil.createTextFormatter(32);
        TextFormatter<String> passwordFormatter1 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter2 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter3 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter4 = PasswordCreateUtil.createTextFormatter(24);
        siteUsername.setTextFormatter(textFormatter1);
        urlField.setTextFormatter(textFormatter2);
        hiddenPasswordField.setTextFormatter(passwordFormatter1);
        hiddenConfirmPasswordField.setTextFormatter(passwordFormatter2);
        visiblePasswordField.setTextFormatter(passwordFormatter3);
        visibleConfirmPasswordField.setTextFormatter(passwordFormatter4);
    }

    private boolean getFolderNotSelectedFlag() {
        return folderNotSelectedFlag;
    }

    private boolean getMissingUrlFlag() {
        return missingUrlFlag;
    }

    private boolean getMissingUsernameFlag() { return missingUsernameFlag; }

    private boolean isMissingPasswordNameFlag() {
        return missingPasswordNameFlag;
    }

    private void setFolderNotSelectedFlag(boolean folderNotSelectedFlag) {
        this.folderNotSelectedFlag = folderNotSelectedFlag;
    }

    private void setMissingPasswordNameFlag(boolean missingPasswordNameFlag) {
        this.missingPasswordNameFlag = missingPasswordNameFlag;
    }

    private void setMissingUrlFlag(boolean missingUrl) {
        this.missingUrlFlag = missingUrl;
    }

    private void setMissingUsernameFlag(boolean missingUsername) { this.missingUsernameFlag = missingUsername; }

    @Override
    protected void checkAndResetLabels() {
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
        if(getMissingUrlFlag() || retrieveNode(URL_EMPTY_ID, passwordVbox) != null) {
            resetLabel(URL_EMPTY_ID, passwordVbox);
            setMissingUrlFlag(false);
        }
        if(getMissingUsernameFlag() || retrieveNode(SITE_USERNAME_EMPTY_ID, passwordVbox) != null) {
            resetLabel(SITE_USERNAME_EMPTY_ID, passwordVbox);
            setMissingUsernameFlag(false);
        }
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
        } if(urlField.getText().isEmpty()) {
            setErrorLabel(URL_EMPTY_ID, URL_EMPTY_ERROR_MSG, passwordVbox);
            setMissingUrlFlag(true);
            erroneousFields = true;
            logger.error("Website url is missing.");
        } if(siteUsername.getText().isEmpty()) {
            setErrorLabel(SITE_USERNAME_EMPTY_ID, SITE_USERNAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingUsernameFlag(true);
            erroneousFields = true;
            logger.error("Username is missing.");
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
    public void confirmAndAddNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException {
        if (PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();
            if (hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            }
            else if(PasswordManagerApp.getPasswordHomeController().getBaseAddPasswordController().getSelectedFolder() != null
            && !passwordName.getText().isEmpty()
            && !urlField.getText().isEmpty()
            && !siteUsername.getText().isEmpty()
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
    public void addNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException {
        String currentDate = LocalDate.now().toString();
        String hashedPassword = "";
        if(confirmPasswordToggler.getShowPassword()) {
           hashedPassword = EncryptDecryptPasswordsUtil.encryptPassword(visibleConfirmPasswordField.getText());
        } else {
            hashedPassword = EncryptDecryptPasswordsUtil.encryptPassword(hiddenConfirmPasswordField.getText());
        }
        StoredPassSQLQueries.addNewWebsitePasswordToDb(PasswordManagerApp.getPasswordHomeController()
                        .getBaseAddPasswordController().getSelectedFolder(),
                passwordName.getText(),
                urlField.getText(),
                PasswordManagerApp.getLoggedInUser().getUsername(),
                siteUsername.getText(),
                currentDate,
                hashedPassword);
        setMissingUrlFlag(false);
        setMismatchedPasswordsFlag(false);
        PasswordHomeController.getStage().close();
        PasswordManagerApp.getPasswordHomeController().viewNewlyAddedPassword();
    }
}
