package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Utils.TwoFactorVerificationUtil;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    private boolean isVerified = false;

    /**
     * Default constructor
     */
    public TwoFactorAuthenticationController() {

    }

    /**
     * Sets the text formatter for the phone number and sends the user a verification code if a valid phone number is
     * present
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTextFormatter();
        if(checkForPhoneNumber()) {
            sendVerificationCode();
        }
    }

    /**
     * Checks if the current user has a valid phone number
     * @return True if valid phone number is present, else false
     */
    private boolean checkForPhoneNumber() {
        return !PasswordManagerApp.getLoggedInUser().getPhoneNumber().contentEquals("")
                && PasswordManagerApp.getLoggedInUser().getPhoneNumber() != null;
    }

    /**
     * Sends a verification code to the user's phone number
     */
    private void sendVerificationCode() {
        TwoFactorVerificationUtil.sendVerificationCode(PasswordManagerApp.getLoggedInUser().getPhoneNumber());
    }

    /**
     * Allows the user to only input numbers, and for the length to be no longer than 6 characters
     */
    public void setTextFormatter() {
        TextFormatter<String> digitLimit = PasswordCreateUtil.createTextNumberFormatter(6);
        verificationCodeField.setTextFormatter(digitLimit);
    }

    /**
     * Compares the user's input with the actual verification code which was sent to the user's phone. If they match,
     * the user is verified and if not the user must try again.
     */
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

    /**
     * Cancels the verification process and sends the user back to the main login screen.
     */
    @FXML
    public void cancelLogin() {
        LoginController.getStage().close();
    }

    /**
     * Flag for if the user has passed verification or not
     * @return True if user passed verification, else false
     */
    public boolean isVerified() {
        return this.isVerified;
    }

    /**
     * Sets the flag for if the user has passed verification or not
     * @param isVerified True if user passed verification, else false
     */
    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }


}
