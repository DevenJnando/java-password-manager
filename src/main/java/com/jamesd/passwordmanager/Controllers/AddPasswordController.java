package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AddPasswordController implements Initializable {

    public static Logger logger = LoggerFactory.getLogger(AddPasswordController.class);

    @FXML
    VBox newPasswordVbox;
    @FXML
    JFXTextField passwordName;
    @FXML
    JFXTextField urlField;
    @FXML
    JFXTextField siteUsername;
    @FXML
    JFXTextField newPasswordField;
    @FXML
    JFXTextField confirmNewPasswordField;
    @FXML
    Label passwordLabel;
    @FXML
    Button confirmNewPasswordButton;

    private boolean mismatchedPasswordsFlag = false;
    private boolean missingPasswordNameFlag = false;
    private boolean missingUrlFlag = false;
    private boolean missingUsernameFlag = false;
    private boolean passwordIsAcceptable = false;

    private final String PASSWORD_TOO_WEAK_ID = "passwordTooWeakLabel";
    private final String PASSWORD_MISMATCH_ID = "passwordsNotMatchLabel";
    private final String PASSWORD_NAME_EMPTY_ID = "passwordNameEmptyLabel";
    private final String URL_EMPTY_ID = "urlFieldEmptyLabel";
    private final String SITE_USERNAME_ID = "siteUsernameEmpty";
    private final String PASSWORD_TOO_WEAK_ERROR_MSG = "Password is too weak!";
    private final String PASSWORD_MISMATCH_ERROR_MSG = "Passwords do not match!";
    private final String PASSWORD_NAME_EMPTY_ERROR_MSG = "Please enter the name of this password.";
    private final String URL_EMPTY_ERROR_MSG = "Please enter the URL this password belongs to.";
    private final String SITE_USERNAME_ERROR_MSG = "Please enter the username/email address for this website.";

    public AddPasswordController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFormatters();
        this.newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            Integer strength = PasswordCreateUtil.passwordStrength(newValue);
            if(isBetween(strength, 0, 4)) {
                this.passwordLabel.setText("Enter Password: (Too weak!)");
                this.passwordLabel.setTextFill(Color.DARKRED);
                this.passwordIsAcceptable = false;
            }
            else if(isBetween(strength, 4, 8)) {
                this.passwordLabel.setText("Enter Password: (Still too weak...)");
                this.passwordLabel.setTextFill(Color.RED);
                this.passwordIsAcceptable = false;
            }
            else if(isBetween(strength, 8, 12)) {
                this.passwordLabel.setText("Enter Password: (Medium)");
                this.passwordLabel.setTextFill(Color.YELLOW);
                this.passwordIsAcceptable = true;
            }
            else if(isBetween(strength, 12, 15)) {
                this.passwordLabel.setText("Enter Password: (Strong)");
                this.passwordLabel.setTextFill(Color.GREEN);
                this.passwordIsAcceptable = true;
            }
            else if(strength == 16) {
                this.passwordLabel.setText("Enter Password: (Very strong)");
                this.passwordLabel.setTextFill(Color.DARKGREEN);
                this.passwordIsAcceptable = true;
            }
        });
    }

    public void setTextFormatters() {
        TextFormatter<String> textFormatter1 = new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() > 32) {
                return null;
            } else {
                return change;
            }
        });
        TextFormatter<String> textFormatter2 = new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() > 32) {
                return null;
            } else {
                return change;
            }
        });
        TextFormatter<String> passwordFormatter1 = new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() > 16) {
                return null;
            } else {
                return change;
            }
        });
        TextFormatter<String> passwordFormatter2 = new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() > 16) {
                return null;
            } else {
                return change;
            }
        });
        this.siteUsername.setTextFormatter(textFormatter1);
        this.urlField.setTextFormatter(textFormatter2);
        this.newPasswordField.setTextFormatter(passwordFormatter1);
        this.confirmNewPasswordField.setTextFormatter(passwordFormatter2);
    }

    public boolean getPasswordIsAcceptableFlag() {
        return this.passwordIsAcceptable;
    }

    public boolean getMismatchedPasswordsFlag() {
        return this.mismatchedPasswordsFlag;
    }

    public boolean getMissingUrlFlag() {
        return this.missingUrlFlag;
    }

    public boolean getMissingUsernameFlag() { return this.missingUsernameFlag; }

    public void setPasswordIsAcceptableFlag(boolean acceptable) {
        this.passwordIsAcceptable = acceptable;
    }

    public void setMismatchedPasswordsFlag(boolean mismatched) {
        this.mismatchedPasswordsFlag = mismatched;
    }


    public boolean isMissingPasswordNameFlag() {
        return missingPasswordNameFlag;
    }

    public void setMissingPasswordNameFlag(boolean missingPasswordNameFlag) {
        this.missingPasswordNameFlag = missingPasswordNameFlag;
    }

    public void setMissingUrlFlag(boolean missingUrl) {
        this.missingUrlFlag = missingUrl;
    }

    public void setMissingUsernameFlag(boolean missingUsername) { this.missingUsernameFlag = missingUsername; }


    private static boolean isBetween (int x, int bottom, int top) {
        return bottom <= x && x <= top;
    }

    public void generateNewPassword() {
        String generatedString = PasswordCreateUtil.generatePassword();
        this.newPasswordField.setText(generatedString);
        this.confirmNewPasswordField.setText(generatedString);
    }

    public <T> T retrieveNode(String id) {
        T node = null;
        try {
            node = (T) this.newPasswordVbox.getChildren()
                    .stream()
                    .filter(o -> o.getId().equals(id))
                    .collect(Collectors.toList()).get(0);
        } catch(IndexOutOfBoundsException e) {
            logger.info("Label " + id + " is not present. Nothing to do here.");
        }
        return node;
    }

    public <T> void resetLabel(String id) {
        T toRemove = retrieveNode(id);
        this.newPasswordVbox.getChildren().remove(toRemove);
    }

    public void setErrorLabel(String id, String errorMessage) {
        Label errorLabel = new Label();
        errorLabel.setId(id);
        errorLabel.setText(errorMessage);
        errorLabel.setTextFill(Color.RED);
        this.newPasswordVbox.getChildren().add(errorLabel);
    }

    private void checkAndResetLabels() {
        if(retrieveNode(PASSWORD_TOO_WEAK_ID) != null) {
            resetLabel(PASSWORD_TOO_WEAK_ID);
        }
        if(getMismatchedPasswordsFlag() || retrieveNode(PASSWORD_MISMATCH_ID) != null) {
            resetLabel(PASSWORD_MISMATCH_ID);
            setMismatchedPasswordsFlag(false);
        }
        if(isMissingPasswordNameFlag() || retrieveNode(PASSWORD_NAME_EMPTY_ID) != null) {
            resetLabel(PASSWORD_NAME_EMPTY_ID);
            setMissingPasswordNameFlag(false);
        }
        if(getMissingUrlFlag() || retrieveNode(URL_EMPTY_ID) != null) {
            resetLabel(URL_EMPTY_ID);
            setMissingUrlFlag(false);
        }
        if(getMissingUsernameFlag() || retrieveNode(SITE_USERNAME_ID) != null) {
            resetLabel(SITE_USERNAME_ID);
            setMissingUsernameFlag(false);
        }
    }

    private boolean hasErroneousFields() {
        boolean erroneousFields = false;

        if (this.passwordName.getText().isEmpty()) {
            setErrorLabel(PASSWORD_NAME_EMPTY_ID, PASSWORD_NAME_EMPTY_ERROR_MSG);
            setMissingPasswordNameFlag(true);
            erroneousFields = true;
            logger.error("Password name is empty");
        } if (this.urlField.getText().isEmpty()) {
            setErrorLabel(URL_EMPTY_ID, URL_EMPTY_ERROR_MSG);
            setMissingUrlFlag(true);
            erroneousFields = true;
            logger.error("Website url is missing.");
        } if (this.siteUsername.getText().isEmpty()) {
            setErrorLabel(SITE_USERNAME_ID, SITE_USERNAME_ERROR_MSG);
            setMissingUsernameFlag(true);
            erroneousFields = true;
            logger.error("Username is missing.");
        } if (!this.newPasswordField.getText().equals(this.confirmNewPasswordField.getText())) {
            setErrorLabel(PASSWORD_MISMATCH_ID, PASSWORD_MISMATCH_ERROR_MSG);
            setMismatchedPasswordsFlag(true);
            erroneousFields = true;
            logger.error("Passwords " + this.newPasswordField.getText() + " and "
                    + this.confirmNewPasswordField.getText() + " do not match.");
        }
        return erroneousFields;
    }

    public void confirmAndAddNewPassword() throws GeneralSecurityException, UnsupportedEncodingException {
        if (PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();

            if (hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            }
            else if(!this.passwordName.getText().isEmpty()
            && !this.urlField.getText().isEmpty()
            && !this.siteUsername.getText().isEmpty()
            && this.newPasswordField.getText().equals(this.confirmNewPasswordField.getText())){

                if(getPasswordIsAcceptableFlag()) {
                    addNewPassword();
                } else {
                    setErrorLabel(PASSWORD_TOO_WEAK_ID, PASSWORD_TOO_WEAK_ERROR_MSG);
                    setPasswordIsAcceptableFlag(false);
                    logger.error("Password is too weak.");
                }
            }
        } else {
                throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    public void addNewPassword() throws GeneralSecurityException, UnsupportedEncodingException {
        String currentDate = LocalDate.now().toString();
        String hashedPassword = EncryptDecryptPasswordsUtil.encryptPassword(this.confirmNewPasswordField.getText());
        StoredPassSQLQueries.addNewPasswordToDb(passwordName.getText(),
                urlField.getText(),
                PasswordManagerApp.getLoggedInUser().getUsername(),
                siteUsername.getText(),
                currentDate,
                hashedPassword);
        setMissingUrlFlag(false);
        setMismatchedPasswordsFlag(false);
        PasswordHomeController.setLoadedPasswords(null);
        PasswordHomeController.getStage().close();
    }
}
