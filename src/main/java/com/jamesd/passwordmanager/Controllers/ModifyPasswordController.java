package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public abstract class ModifyPasswordController extends PasswordController {

    @FXML
    CustomPasswordField hiddenNewPasswordField = new CustomPasswordField();
    @FXML
    CustomPasswordField hiddenConfirmNewPasswordField = new CustomPasswordField();
    @FXML
    Label passwordLabel = new Label();
    @FXML
    CustomTextField visibleNewPasswordField = new CustomTextField();
    @FXML
    CustomTextField visibleConfirmNewPasswordField = new CustomTextField();

    protected final String PASSWORD_TOO_WEAK_ID = "passwordTooWeakLabel";
    protected final String PASSWORD_MISMATCH_ID = "passwordsNotMatchLabel";
    protected final String PASSWORD_TOO_WEAK_ERROR_MSG = "Password is too weak!";
    protected final String PASSWORD_MISMATCH_ERROR_MSG = "Passwords do not match!";

    protected Boolean passwordIsAcceptable = false;
    protected boolean mismatchedPasswordsFlag = false;
    protected Toggler newPasswordToggler = new Toggler("hiddenNewPasswordField", "visibleNewPasswordField");
    protected Toggler confirmNewPasswordToggler = new Toggler("hiddenConfirmNewPasswordField", "visibleConfirmNewPasswordField");

    protected static Logger logger = LoggerFactory.getLogger(ModifyPasswordController.class);

    public ModifyPasswordController() {
        super();
    }

    protected abstract void setTextFormatters();

    protected abstract void checkAndResetLabels();

    protected abstract Boolean hasErroneousFields();

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
        hiddenNewPasswordField.textProperty().addListener((obs, oldValue, newValue) -> {
            passwordIsAcceptable = strengthChecker(newValue, passwordLabel, labelMessage);
        });
        visibleNewPasswordField.textProperty().addListener((obs, oldValue, newValue) -> {
            passwordIsAcceptable = strengthChecker(newValue, passwordLabel, labelMessage);
        });
    }

    @FXML
    protected void generateNewPassword() {
        String generatedString = PasswordCreateUtil.generatePassword();
        if(confirmNewPasswordToggler.getShowPassword()){
            visibleNewPasswordField.setText(generatedString);
            visibleConfirmNewPasswordField.setText(generatedString);
        } else {
            hiddenNewPasswordField.setText(generatedString);
            hiddenConfirmNewPasswordField.setText(generatedString);
        }
    }

    public <T> T retrieveNode(String id, VBox passwordVbox) {
        T node = null;
        try {
            node = (T) passwordVbox.getChildren()
                    .stream()
                    .filter(o -> o.getId().equals(id))
                    .collect(Collectors.toList()).get(0);
        } catch(IndexOutOfBoundsException e) {
            logger.info("Label " + id + " is not present. Nothing to do here.");
        }
        return node;
    }

    public <T> void resetLabel(String id, VBox passwordVbox) {
        T toRemove = retrieveNode(id, passwordVbox);
        passwordVbox.getChildren().remove(toRemove);
    }

    public void setErrorLabel(String id, String errorMessage, VBox passwordVbox) {
        Label errorLabel = new Label();
        errorLabel.setId(id);
        errorLabel.setText(errorMessage);
        errorLabel.setTextFill(Color.RED);
        passwordVbox.getChildren().add(errorLabel);
    }
}
