package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Abstract controller which contains common fields and methods for any controller which modifies a password entry
 */
public abstract class ModifyPasswordController extends PasswordController {

    /**
     * FXML fields
     */
    @FXML
    CustomPasswordField hiddenPasswordField = new CustomPasswordField();
    @FXML
    CustomPasswordField hiddenConfirmPasswordField = new CustomPasswordField();
    @FXML
    Label passwordLabel = new Label();
    @FXML
    CustomTextField visiblePasswordField = new CustomTextField();
    @FXML
    CustomTextField visibleConfirmPasswordField = new CustomTextField();

    /**
     * Validation flags, and error messages and their error label IDs
     */
    protected final String PASSWORD_TOO_WEAK_ID = "passwordTooWeakLabel";
    protected final String PASSWORD_MISMATCH_ID = "passwordsNotMatchLabel";
    protected final String PASSWORD_TOO_WEAK_ERROR_MSG = "Password is too weak!";
    protected final String PASSWORD_MISMATCH_ERROR_MSG = "Passwords do not match!";
    protected Boolean passwordIsAcceptable = false;
    protected boolean mismatchedPasswordsFlag = false;

    /**
     * Password togglers
     */
    protected Toggler passwordToggler = new Toggler("hiddenPasswordField", "visiblePasswordField");
    protected Toggler confirmPasswordToggler = new Toggler("hiddenConfirmPasswordField", "visibleConfirmPasswordField");

    protected static Logger logger = LoggerFactory.getLogger(ModifyPasswordController.class);

    /**
     * Default constructor
     */
    public ModifyPasswordController() {
        super();
    }

    /**
     * Retrieves the flag for when the password and confirm password fields are mismatched
     * @return Boolean true if password field does not match confirm password field, else false
     */
    protected boolean getMismatchedPasswordsFlag() {
        return mismatchedPasswordsFlag;
    }

    /**
     * Sets the flag for when the password and confirm password fields are mismatched
     * @param mismatched Boolean true if password field does not match confirm password field, else false
     */
    protected void setMismatchedPasswordsFlag(boolean mismatched) {
        this.mismatchedPasswordsFlag = mismatched;
    }

    /**
     * Retrieves the flag for if a password is sufficiently strong to be used
     * @return Boolean true if password strength is satisfactory, else false
     */
    public Boolean getPasswordIsAcceptableFlag() {
        return passwordIsAcceptable;
    }

    /**
     * Sets the flag for if a password is sufficiently strong to be used
     * @param passwordIsAcceptable Boolean true if password strength is satisfactory, else false
     */
    public void setPasswordIsAcceptableFlag(Boolean passwordIsAcceptable) {
        this.passwordIsAcceptable = passwordIsAcceptable;
    }

    /**
     * Calls the checkPasswordStrength method in the PasswordCreateUtil utility class. It checks the overall strength
     * of the password and returns a Boolean with true indicating the strength is satisfactory and false indicating
     * the password is not strong enough
     * @param newValue String value of the current password input by the user
     * @param passwordLabel Label which feeds back the strength of the password back to the user
     * @param initialText String which has the initial message of the passwordLabel e.g. "Enter password: "
     * @return Boolean true if password is strong enough, else false
     */
    protected Boolean strengthChecker(String newValue, Label passwordLabel, String initialText) {
        return PasswordCreateUtil.checkPasswordStrength(newValue, passwordLabel, initialText);
    }

    /**
     * Attaches the strength listener to both the hidden and visible password fields.
     * @param labelMessage String value of the initial message which should be displayed in the passwordLabel field
     */
    protected void attachStrengthListener(String labelMessage) {
        hiddenPasswordField.textProperty().addListener((obs, oldValue, newValue) -> {
            passwordIsAcceptable = strengthChecker(newValue, passwordLabel, labelMessage);
        });
        visiblePasswordField.textProperty().addListener((obs, oldValue, newValue) -> {
            passwordIsAcceptable = strengthChecker(newValue, passwordLabel, labelMessage);
        });
    }


    /**
     * Copies a String value to the clipboard
     * @param textToBeCopied String value of what should be copied
     */
    protected void copyToClipboard(String textToBeCopied) {
        StringSelection selection = new StringSelection(textToBeCopied);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    /**
     * Generates a new password and applies it to either the hidden or visible password field - whichever one is
     * currently toggled
     */
    @FXML
    protected void generateNewPassword() {
        String generatedString = PasswordCreateUtil.generatePassword();
        if(passwordToggler.getShowPassword()){
            visiblePasswordField.setText(generatedString);
            visibleConfirmPasswordField.setText(generatedString);
        } else {
            hiddenPasswordField.setText(generatedString);
            hiddenConfirmPasswordField.setText(generatedString);
        }
    }
}
