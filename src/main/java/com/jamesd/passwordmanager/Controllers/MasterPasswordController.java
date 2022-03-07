package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.Models.User;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.HashMasterPasswordUtil;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;

public class MasterPasswordController extends UpdatePasswordController implements Initializable {

    @FXML
    VBox passwordVbox = new VBox();
    @FXML
    Label passwordLabel = new Label();
    @FXML
    CustomPasswordField hiddenOldMasterPassword = new CustomPasswordField();
    @FXML
    CustomTextField visibleOldMasterPassword = new CustomTextField();


    private final String OLD_PASSWORD_MISMATCH_ID = "oldPasswordNotMatchLabel";
    private final String OLD_PASSWORD_MISMATCH_ERROR_MSG = "Old password is incorrect!";

    private boolean mismatchedOldPasswordFlag = false;
    private Toggler oldPasswordToggler = new Toggler("hiddenOldMasterPassword", "visibleOldMasterPassword");
    private static final Logger logger = LoggerFactory.getLogger(MasterPasswordController.class);

    public MasterPasswordController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFormatters();
        setIcons();
        attachStrengthListener("New Master Password: ");
    }

    public boolean getMismatchedOldPasswordFlag() {
        return mismatchedOldPasswordFlag;
    }

    public void setMismatchedOldPasswordFlag(boolean mismatchedOldPasswordFlag) {
        this.mismatchedOldPasswordFlag = mismatchedOldPasswordFlag;
    }

    @Override
    protected void setIcons() {
        Text hiddenOldPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text hiddenNewPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text hiddenConfirmPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text visibleOldPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
        Text visibleNewPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
        Text visibleConfirmPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);

        hiddenOldMasterPassword.setRight(hiddenOldPasswordIcon);
        hiddenOldMasterPassword.getRight().setCursor(Cursor.HAND);
        hiddenOldMasterPassword.getRight().setOnMouseClicked(this::togglePassword);
        hiddenOldMasterPassword.getRight().setOnMousePressed(this::togglePassword);

        hiddenNewPasswordField.setRight(hiddenNewPasswordIcon);
        hiddenNewPasswordField.getRight().setCursor(Cursor.HAND);
        hiddenNewPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        hiddenNewPasswordField.getRight().setOnMousePressed(this::togglePassword);

        hiddenConfirmNewPasswordField.setRight(hiddenConfirmPasswordIcon);
        hiddenConfirmNewPasswordField.getRight().setCursor(Cursor.HAND);
        hiddenConfirmNewPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        hiddenConfirmNewPasswordField.getRight().setOnMousePressed(this::togglePassword);

        visibleOldMasterPassword.setRight(visibleOldPasswordIcon);
        visibleOldMasterPassword.getRight().setCursor(Cursor.HAND);
        visibleNewPasswordField.setRight(visibleNewPasswordIcon);
        visibleNewPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        visibleNewPasswordField.getRight().setOnMousePressed(this::togglePassword);

        visibleConfirmNewPasswordField.setRight(visibleConfirmPasswordIcon);
        visibleConfirmNewPasswordField.getRight().setCursor(Cursor.HAND);
        visibleConfirmNewPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        visibleConfirmNewPasswordField.getRight().setOnMousePressed(this::togglePassword);

    }

    @Override
    protected void setTextFormatters() {
        TextFormatter<String> passwordFormatter1 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter2 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter3 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter4 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter5 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter6 = PasswordCreateUtil.createTextFormatter(24);
        hiddenOldMasterPassword.setTextFormatter(passwordFormatter1);
        hiddenNewPasswordField.setTextFormatter(passwordFormatter2);
        hiddenConfirmNewPasswordField.setTextFormatter(passwordFormatter3);
        visibleOldMasterPassword.setTextFormatter(passwordFormatter4);
        visibleNewPasswordField.setTextFormatter(passwordFormatter5);
        visibleConfirmNewPasswordField.setTextFormatter(passwordFormatter6);
    }

    protected void checkAndResetLabels() {
        if(retrieveNode(PASSWORD_TOO_WEAK_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_TOO_WEAK_ID, passwordVbox);
        }
        if(getMismatchedOldPasswordFlag() || retrieveNode(OLD_PASSWORD_MISMATCH_ID, passwordVbox) != null) {
            resetLabel(OLD_PASSWORD_MISMATCH_ID, passwordVbox);
            setMismatchedOldPasswordFlag(false);
        }
        if(getMismatchedPasswordsFlag() || retrieveNode(PASSWORD_MISMATCH_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_MISMATCH_ID, passwordVbox);
            setMismatchedPasswordsFlag(false);
        }
    }

    protected Boolean hasErroneousFields() {
        boolean erroneousFields = false;
        if(!HashMasterPasswordUtil.checkHashAndUpdate
                (hiddenOldMasterPassword.getText(), PasswordManagerApp.getLoggedInUser())) {
            setErrorLabel(OLD_PASSWORD_MISMATCH_ID, OLD_PASSWORD_MISMATCH_ERROR_MSG, passwordVbox);
            setMismatchedOldPasswordFlag(true);
            erroneousFields = true;
            logger.error("Old password incorrect.");
        }
        if (!hiddenNewPasswordField.getText().equals(hiddenConfirmNewPasswordField.getText())
                || !visibleNewPasswordField.getText().equals(visibleConfirmNewPasswordField.getText())) {
            setErrorLabel(PASSWORD_MISMATCH_ID, PASSWORD_MISMATCH_ERROR_MSG, passwordVbox);
            setMismatchedPasswordsFlag(true);
            erroneousFields = true;
            logger.error("Passwords do not match.");
        }
        return erroneousFields;
    }

    @Override
    public void updatePassword() throws IOException {
        String hashedPassword = "";
        if(confirmNewPasswordToggler.getShowPassword()) {
            hashedPassword = HashMasterPasswordUtil.hashPassword(visibleConfirmNewPasswordField.getText());
        } else {
            hashedPassword = HashMasterPasswordUtil.hashPassword(hiddenConfirmNewPasswordField.getText());
        }
        User user = PasswordManagerApp.getLoggedInUser();
        user.setEncryptedPassword(hashedPassword);
        MasterSQLQueries.initialiseUsers();
        MasterSQLQueries.updateUserInDb(user);
        MasterSQLQueries.close();
        setMismatchedPasswordsFlag(false);
        PreferencesController.getStage().close();
        PasswordManagerApp.getPreferencesController().showSavedLabel(PasswordManagerApp.getPreferencesController().saveUpdatePasswordLabel);
    }

    @Override
    public void confirmAndUpdatePassword() throws GeneralSecurityException, IOException {
        if (PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();

            if (hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            }
            else if((hiddenNewPasswordField.getText().equals(hiddenConfirmNewPasswordField.getText()))
                    || visibleNewPasswordField.getText().equals(visibleConfirmNewPasswordField.getText())){

                if(getPasswordIsAcceptableFlag()) {
                    updatePassword();
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
    public void togglePassword(Event event) {
        try {
            Class<?> customTextFieldClass = Class.forName("org.controlsfx.control.textfield.CustomTextField");
            Class<?> customPasswordFieldClass = Class.forName("org.controlsfx.control.textfield.CustomPasswordField");
            Object oldPasswordState = oldPasswordToggler.togglePassword(passwordVbox);
            Object newPasswordState = newPasswordToggler.togglePassword(passwordVbox);
            Object confirmPasswordState = confirmNewPasswordToggler.togglePassword(passwordVbox);
            if (customTextFieldClass.isInstance(oldPasswordState)
                    && customTextFieldClass.isInstance(newPasswordState)
                    && customTextFieldClass.isInstance(confirmPasswordState)) {
                CustomTextField oldPasswordShow = (CustomTextField) oldPasswordState;
                CustomTextField newPasswordShow = (CustomTextField) newPasswordState;
                CustomTextField confirmPasswordShow = (CustomTextField) confirmPasswordState;
                Insets insets = new Insets(10, 0, 0, 0);
                VBox.setMargin(oldPasswordShow, insets);
                VBox.setMargin(newPasswordShow, insets);
                VBox.setMargin(confirmPasswordShow, insets);
                visibleOldMasterPassword = oldPasswordShow;
                visibleNewPasswordField = newPasswordShow;
                visibleConfirmNewPasswordField = confirmPasswordShow;
                setTextFormatters();
                setIcons();
                attachStrengthListener("New Master Password: ");
                passwordVbox.getChildren().set(1, visibleOldMasterPassword);
                passwordVbox.getChildren().set(3, visibleNewPasswordField);
                passwordVbox.getChildren().set(5, visibleConfirmNewPasswordField);
            } else if(customPasswordFieldClass.isInstance(oldPasswordState)
                    && customPasswordFieldClass.isInstance(newPasswordState)
                    && customPasswordFieldClass.isInstance(confirmPasswordState)) {
                CustomPasswordField oldPasswordHide = (CustomPasswordField) oldPasswordState;
                CustomPasswordField newPasswordHide = (CustomPasswordField) newPasswordState;
                CustomPasswordField confirmPasswordHide = (CustomPasswordField) confirmPasswordState;
                Insets insets = new Insets(10, 0, 0, 0);
                VBox.setMargin(oldPasswordHide, insets);
                VBox.setMargin(newPasswordHide, insets);
                VBox.setMargin(confirmPasswordHide, insets);
                hiddenOldMasterPassword = oldPasswordHide;
                hiddenNewPasswordField = newPasswordHide;
                hiddenConfirmNewPasswordField = confirmPasswordHide;
                setTextFormatters();
                setIcons();
                attachStrengthListener("New Master Password: ");
                passwordVbox.getChildren().set(1, hiddenOldMasterPassword);
                passwordVbox.getChildren().set(3, hiddenNewPasswordField);
                passwordVbox.getChildren().set(5, hiddenConfirmNewPasswordField);
            }
        } catch (ClassNotFoundException
                | InvocationTargetException
                | NoSuchMethodException
                | IllegalAccessException e) {
            e.printStackTrace();
            logger.error("Toggler failed...");
            }
    }
}
