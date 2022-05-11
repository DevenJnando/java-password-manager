package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Utils.TwoFactorVerificationUtil;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class which performs a two-factor authentication check on an unrecognised device
 */
public class TwoFactorAuthenticationController implements Initializable {

    @FXML
    private JFXTextField verificationCodeField = new JFXTextField();
    @FXML
    private Label incorrectCodeLabel = new Label();
    @FXML
    private Button verifyButton;
    @FXML
    private Button cancelButton;

    private boolean isVerified = false;

    public TwoFactorAuthenticationController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTextFormatters();
        if(checkForPhoneNumber()) {
            sendVerificationCode();
        }
    }

    private boolean checkForPhoneNumber() {
        return !PasswordManagerApp.getLoggedInUser().getPhoneNumber().contentEquals("")
                && PasswordManagerApp.getLoggedInUser().getPhoneNumber() != null;
    }

    private void sendVerificationCode() {
        TwoFactorVerificationUtil.sendVerificationCode(PasswordManagerApp.getLoggedInUser().getPhoneNumber());
    }

    public void setTextFormatters() {
        TextFormatter<String> digitLimit = PasswordCreateUtil.createTextNumberFormatter(6);
        verificationCodeField.setTextFormatter(digitLimit);
    }

    @FXML
    public void verifyLogin() {
        setIsVerified(TwoFactorVerificationUtil.verify(PasswordManagerApp.getLoggedInUser().getPhoneNumber(), verificationCodeField.getText()));
        if(isVerified) {
            LoginController.getStage().close();
        } else {
            incorrectCodeLabel.setText("Incorrect code entered, try again.");
            incorrectCodeLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    public void cancelLogin() {
        LoginController.getStage().close();
    }

    public boolean isVerified() {
        return this.isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }


}
