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

public abstract class ModifyPasswordController extends PasswordController {

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

    protected final String PASSWORD_TOO_WEAK_ID = "passwordTooWeakLabel";
    protected final String PASSWORD_MISMATCH_ID = "passwordsNotMatchLabel";
    protected final String PASSWORD_TOO_WEAK_ERROR_MSG = "Password is too weak!";
    protected final String PASSWORD_MISMATCH_ERROR_MSG = "Passwords do not match!";

    protected Boolean passwordIsAcceptable = false;
    protected boolean mismatchedPasswordsFlag = false;
    protected Toggler passwordToggler = new Toggler("hiddenPasswordField", "visiblePasswordField");
    protected Toggler confirmPasswordToggler = new Toggler("hiddenConfirmPasswordField", "visibleConfirmPasswordField");

    protected static Logger logger = LoggerFactory.getLogger(ModifyPasswordController.class);

    public ModifyPasswordController() {
        super();
    }

    protected boolean getMismatchedPasswordsFlag() {
        return mismatchedPasswordsFlag;
    }

    protected void setMismatchedPasswordsFlag(boolean mismatched) {
        this.mismatchedPasswordsFlag = mismatched;
    }

    public Boolean getPasswordIsAcceptableFlag() {
        return passwordIsAcceptable;
    }

    public void setPasswordIsAcceptableFlag(Boolean passwordIsAcceptable) {
        this.passwordIsAcceptable = passwordIsAcceptable;
    }

    protected Boolean strengthChecker(String newValue, Label passwordLabel, String initialText) {
        return PasswordCreateUtil.checkPasswordStrength(newValue, passwordLabel, initialText);
    }

    protected void attachStrengthListener(String labelMessage) {
        hiddenPasswordField.textProperty().addListener((obs, oldValue, newValue) -> {
            passwordIsAcceptable = strengthChecker(newValue, passwordLabel, labelMessage);
        });
        visiblePasswordField.textProperty().addListener((obs, oldValue, newValue) -> {
            passwordIsAcceptable = strengthChecker(newValue, passwordLabel, labelMessage);
        });
    }


    protected void copyToClipboard(String textToBeCopied) {
        StringSelection selection = new StringSelection(textToBeCopied);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

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
