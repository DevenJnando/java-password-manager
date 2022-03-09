package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
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

    private Boolean missingPasswordNameFlag = false;
    private Boolean missingUrlFlag = false;
    private Boolean missingUsernameFlag = false;

    private final String PASSWORD_NAME_EMPTY_ID = "passwordNameEmptyLabel";
    private final String URL_EMPTY_ID = "urlFieldEmptyLabel";
    private final String SITE_USERNAME_ID = "siteUsernameEmpty";
    private final String PASSWORD_NAME_EMPTY_ERROR_MSG = "Please enter the name of this password.";
    private final String URL_EMPTY_ERROR_MSG = "Please enter the URL this password belongs to.";
    private final String SITE_USERNAME_ERROR_MSG = "Please enter the username/email address for this website.";


    public AddWebsitePasswordController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFormatters();
        setIcons();
        attachStrengthListener("Enter Password: ");
    }

    @Override
    public void setIcons() {
        Text eye1 = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text eye2 = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text eyeSlash1 = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
        Text eyeSlash2 = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
        visiblePasswordField.setRight(eyeSlash1);
        visiblePasswordField.getRight().setCursor(Cursor.HAND);
        visibleConfirmPasswordField.setRight(eyeSlash2);
        visibleConfirmPasswordField.getRight().setCursor(Cursor.HAND);
        hiddenPasswordField.setRight(eye1);
        hiddenPasswordField.getRight().setCursor(Cursor.HAND);
        hiddenConfirmPasswordField.setRight(eye2);
        hiddenConfirmPasswordField.getRight().setCursor(Cursor.HAND);
        visiblePasswordField.getRight().setOnMouseClicked(this::togglePassword);
        visiblePasswordField.getRight().setOnMousePressed(this::togglePassword);
        visibleConfirmPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        visibleConfirmPasswordField.getRight().setOnMousePressed(this::togglePassword);
        hiddenPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        hiddenPasswordField.getRight().setOnMousePressed(this::togglePassword);
        hiddenConfirmPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        hiddenConfirmPasswordField.getRight().setOnMousePressed(this::togglePassword);
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

    @Override
    public void togglePassword(Event event) {
        try {
            Class<?> customTextFieldClass = Class.forName("org.controlsfx.control.textfield.CustomTextField");
            Class<?> customPasswordFieldClass = Class.forName("org.controlsfx.control.textfield.CustomPasswordField");
            Object passwordState = passwordToggler.togglePassword(passwordVbox);
            Object confirmPasswordState = confirmPasswordToggler.togglePassword(passwordVbox);
            if(customTextFieldClass.isInstance(passwordState)
                    && customTextFieldClass.isInstance(confirmPasswordState)) {
                CustomTextField passwordShow = (CustomTextField) passwordState;
                CustomTextField confirmPasswordShow = (CustomTextField) confirmPasswordState;
                visiblePasswordField = passwordShow;
                visibleConfirmPasswordField = confirmPasswordShow;
                setTextFormatters();
                setIcons();
                attachStrengthListener("Enter Password: ");
                passwordVbox.getChildren().set(7, visiblePasswordField);
                passwordVbox.getChildren().set(9, visibleConfirmPasswordField);
            } else if(customPasswordFieldClass.isInstance(passwordState)
                    && customPasswordFieldClass.isInstance(confirmPasswordState)) {
                CustomPasswordField passwordHide = (CustomPasswordField) passwordState;
                CustomPasswordField confirmPasswordHide = (CustomPasswordField) confirmPasswordState;
                hiddenPasswordField = passwordHide;
                hiddenConfirmPasswordField = confirmPasswordHide;
                setTextFormatters();
                setIcons();
                attachStrengthListener("Enter Password: ");
                passwordVbox.getChildren().set(7, hiddenPasswordField);
                passwordVbox.getChildren().set(9, hiddenConfirmPasswordField);
            } else {
                throw new ClassCastException("Cannot cast object of type " + passwordState.getClass() + " to type " +
                        CustomTextField.class + " or type " + CustomPasswordField.class);
            }
        } catch(NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("Toggler failed...");
        }
    }

    public boolean getMissingUrlFlag() {
        return missingUrlFlag;
    }

    public boolean getMissingUsernameFlag() { return missingUsernameFlag; }

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

    protected void checkAndResetLabels() {
        if(retrieveNode(PASSWORD_TOO_WEAK_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_TOO_WEAK_ID, passwordVbox);
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
        if(getMissingUsernameFlag() || retrieveNode(SITE_USERNAME_ID, passwordVbox) != null) {
            resetLabel(SITE_USERNAME_ID, passwordVbox);
            setMissingUsernameFlag(false);
        }
    }

    protected Boolean hasErroneousFields() {
        boolean erroneousFields = false;
        if (passwordName.getText().isEmpty()) {
            setErrorLabel(PASSWORD_NAME_EMPTY_ID, PASSWORD_NAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingPasswordNameFlag(true);
            erroneousFields = true;
            logger.error("Password name is empty");
        } if (urlField.getText().isEmpty()) {
            setErrorLabel(URL_EMPTY_ID, URL_EMPTY_ERROR_MSG, passwordVbox);
            setMissingUrlFlag(true);
            erroneousFields = true;
            logger.error("Website url is missing.");
        } if (siteUsername.getText().isEmpty()) {
            setErrorLabel(SITE_USERNAME_ID, SITE_USERNAME_ERROR_MSG, passwordVbox);
            setMissingUsernameFlag(true);
            erroneousFields = true;
            logger.error("Username is missing.");
        } if (!hiddenPasswordField.getText().equals(hiddenConfirmPasswordField.getText())
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
            else if(!passwordName.getText().isEmpty()
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
        StoredPassSQLQueries.addNewPasswordToDb(PasswordHomeController.getBaseAddPasswordController().getSelectedFolder(),
                passwordName.getText(),
                urlField.getText(),
                PasswordManagerApp.getLoggedInUser().getUsername(),
                siteUsername.getText(),
                currentDate,
                hashedPassword);
        setMissingUrlFlag(false);
        setMismatchedPasswordsFlag(false);
        //PasswordHomeController.setLoadedPasswords(null);
        PasswordHomeController.getStage().close();
    }
}
