package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.CreditCardValidator;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

/**
 * Controller responsible for displaying all details for a credit/debit card entry to the user, as well as updating or
 * changing any fields to the card entry in the database
 */
public class CreditDebitCardDetailsController extends BasePasswordDetailsController<CreditDebitCardEntryWrapper> implements Initializable {

    /**
     * FXML fields
     */
    @FXML
    private JFXTextField expiryMonthField = new JFXTextField();
    @FXML
    private JFXTextField expiryYearField = new JFXTextField();
    @FXML
    private JFXTextField securityNoField = new JFXTextField();
    @FXML
    private JFXTextField accountNoField = new JFXTextField();
    @FXML
    private JFXTextField sortCodeField = new JFXTextField();
    @FXML
    private JFXButton copyExpiryDateButton = new JFXButton();
    @FXML
    private JFXButton copySecurityNoButton = new JFXButton();
    @FXML
    private JFXButton copyAccountNoButton = new JFXButton();
    @FXML
    private JFXButton copySortCodeButton = new JFXButton();

    private static Logger logger = LoggerFactory.getLogger(CreditDebitCardDetailsController.class);

    /**
     * Validation flags
     */
    private Boolean missingPasswordNameFlag = false;
    private Boolean missingCreditDebitCardNoFlag = false;
    private Boolean missingExpiryDateMonth = false;
    private Boolean expiryMonthTooShort = false;
    private Boolean missingExpiryDateYear = false;
    private Boolean expiryYearTooShort = false;

    /**
     * IDs of error labels and error messages they should display
     */
    private final String PASSWORD_NAME_EMPTY_ID = "passwordNameEmptyLabel";
    private final String CREDIT_DEBIT_CARD_NOT_VALID_ID = "creditDebitCardNotValidLabel";
    private final String CREDIT_DEBIT_CARD_NO_EMPTY_ID = "creditDebitCardNoEmptyLabel";
    private final String EXPIRY_MONTH_NOT_SET_ID = "expiryMonthNotSetLabel";
    private final String EXPIRY_MONTH_TOO_SHORT_ID = "expiryMonthTooShortLabel";
    private final String EXPIRY_YEAR_NOT_SET_ID = "expiryYearNotSetLabel";
    private final String EXPIRY_YEAR_TOO_SHORT_ID = "expiryYearTooShortLabel";
    private final String PASSWORD_NAME_EMPTY_ERROR_MSG = "Please enter the name of this password.";
    private final String CREDIT_DEBIT_CARD_NOT_VALID_ERROR_MSG = "Credit/Debit card number is not valid.";
    private final String CREDIT_DEBIT_CARD_NO_EMPTY_ERROR_MSG = "Please enter the credit/debit card number.";
    private final String EXPIRY_MONTH_NOT_SET_MSG = "Expiry date partially filled - please fill in the month";
    private final String EXPIRY_MONTH_TOO_SHORT_MSG = "Expiry month is too short.";
    private final String EXPIRY_YEAR_NOT_SET_MSG = "Expiry date partially filled - please fill in the year.";
    private final String EXPIRY_YEAR_TOO_SHORT_MSG = "Expiry year is too short.";

    /**
     * Default constructor
     */
    public CreditDebitCardDetailsController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFormatters();
        setCardNumberFormatters();
        setIcons();
        setCreditDebitCardIcons();
    }

    @Override
    protected void checkAndResetLabels() {
        if(retrieveNode(CREDIT_DEBIT_CARD_NOT_VALID_ID, passwordVbox) != null) {
            resetLabel(CREDIT_DEBIT_CARD_NOT_VALID_ID, passwordVbox);
        }
        if(getMismatchedPasswordsFlag() || retrieveNode(PASSWORD_MISMATCH_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_MISMATCH_ID, passwordVbox);
            setMismatchedPasswordsFlag(false);
        }
        if(isMissingPasswordNameFlag() || retrieveNode(PASSWORD_NAME_EMPTY_ID, passwordVbox) != null) {
            resetLabel(PASSWORD_NAME_EMPTY_ID, passwordVbox);
            setMissingPasswordNameFlag(false);
        }
        if(getMissingCreditDebitCardNoFlag() || retrieveNode(CREDIT_DEBIT_CARD_NO_EMPTY_ID, passwordVbox) != null) {
            resetLabel(CREDIT_DEBIT_CARD_NO_EMPTY_ID, passwordVbox);
            setMissingCreditDebitCardNoFlag(false);
        }
        if(getMissingExpiryDateMonth() || retrieveNode(EXPIRY_MONTH_NOT_SET_ID, passwordVbox) != null) {
            resetLabel(EXPIRY_MONTH_NOT_SET_ID, passwordVbox);
            setMissingExpiryDateMonth(false);
        }
        if(getExpiryMonthTooShort() || retrieveNode(EXPIRY_MONTH_TOO_SHORT_ID, passwordVbox) != null) {
            resetLabel(EXPIRY_MONTH_NOT_SET_ID, passwordVbox);
            setExpiryMonthTooShort(false);
        }
        if(getMissingExpiryDateYear() || retrieveNode(EXPIRY_YEAR_NOT_SET_ID, passwordVbox) != null) {
            resetLabel(EXPIRY_YEAR_NOT_SET_ID, passwordVbox);
            setMissingExpiryDateYear(false);
        }
        if(getExpiryYearTooShort() || retrieveNode(EXPIRY_YEAR_TOO_SHORT_ID, passwordVbox) != null) {
            resetLabel(EXPIRY_YEAR_TOO_SHORT_ID, passwordVbox);
            setExpiryYearTooShort(false);
        }
    }

    @Override
    protected Boolean hasErroneousFields() {
        boolean erroneousFields = false;
        if(passwordNameField.getText().isEmpty()) {
            setErrorLabel(PASSWORD_NAME_EMPTY_ID, PASSWORD_NAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingPasswordNameFlag(true);
            erroneousFields = true;
            logger.error("Password name is empty");
        } if(visiblePasswordField.getText().isEmpty() && hiddenPasswordField.getText().isEmpty()) {
            setErrorLabel(CREDIT_DEBIT_CARD_NO_EMPTY_ID, CREDIT_DEBIT_CARD_NO_EMPTY_ERROR_MSG, passwordVbox);
            setMissingCreditDebitCardNoFlag(true);
            erroneousFields = true;
            logger.error("Credit/Debit card number is missing.");
        } if(!expiryMonthField.getText().isEmpty()) {
            if(!expiryYearField.getText().isEmpty()) {
                if(expiryMonthField.getText().length() < 2 ||
                        (Double.parseDouble(expiryMonthField.getText()) < 01 ||
                                Double.parseDouble(expiryMonthField.getText()) > 12)) {
                    setErrorLabel(EXPIRY_MONTH_TOO_SHORT_ID, EXPIRY_MONTH_TOO_SHORT_MSG, passwordVbox);
                    setExpiryMonthTooShort(true);
                    erroneousFields = true;
                }
            } else {
                setErrorLabel(EXPIRY_YEAR_NOT_SET_ID, EXPIRY_YEAR_NOT_SET_MSG, passwordVbox);
                setMissingExpiryDateYear(true);
                erroneousFields = true;
                logger.error("Expiry date partially filled - year missing");
            }
        } if(!expiryYearField.getText().isEmpty()) {
            if(!expiryMonthField.getText().isEmpty()) {
                if(expiryYearField.getText().length() < 2) {
                    setErrorLabel(EXPIRY_YEAR_TOO_SHORT_ID, EXPIRY_YEAR_TOO_SHORT_MSG, passwordVbox);
                    setExpiryYearTooShort(true);
                    erroneousFields = true;
                }
            } else {
                setErrorLabel(EXPIRY_MONTH_NOT_SET_ID, EXPIRY_MONTH_NOT_SET_MSG, passwordVbox);
                setMissingExpiryDateMonth(true);
                erroneousFields = true;
                logger.error("Expiry date partially filled - month missing");
            }
        }
        return erroneousFields;
    }

    @Override
    public void setIcons() {
        Text copy1 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy2 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy3 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy4 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        copyExpiryDateButton.setGraphic(copy1);
        copyExpiryDateButton.setCursor(Cursor.HAND);
        copySecurityNoButton.setGraphic(copy2);
        copySecurityNoButton.setCursor(Cursor.HAND);
        copyAccountNoButton.setGraphic(copy3);
        copyAccountNoButton.setCursor(Cursor.HAND);
        copySortCodeButton.setGraphic(copy4);
        copySortCodeButton.setCursor(Cursor.HAND);
    }

    /**
     * Methods for settings icons for credit/debit card numbers only
     */
    protected void setCreditDebitCardIcons() {
        Text hiddenPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text shownPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
        Text copy1 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy2 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text save = GlyphsDude.createIcon(FontAwesomeIcon.SAVE, "1.5em");
        Text delete = GlyphsDude.createIcon(FontAwesomeIcon.TRASH, "1.5em");
        copyPasswordNameButton.setGraphic(copy1);
        copyPasswordNameButton.setCursor(Cursor.HAND);
        copyPasswordButton.setGraphic(copy2);
        copyPasswordButton.setCursor(Cursor.HAND);
        savePasswordButton.setGraphic(save);
        savePasswordButton.setCursor(Cursor.HAND);
        deletePasswordButton.setGraphic(delete);
        deletePasswordButton.setCursor(Cursor.HAND);
        hiddenPasswordField.setRight(hiddenPasswordIcon);
        hiddenPasswordField.getRight().setCursor(Cursor.HAND);
        visiblePasswordField.setRight(shownPasswordIcon);
        visiblePasswordField.getRight().setCursor(Cursor.HAND);
        visiblePasswordField.getRight().setOnMouseClicked(this::toggleCreditCardNumber);
        visiblePasswordField.getRight().setOnMousePressed(this::toggleCreditCardNumber);
        hiddenPasswordField.getRight().setOnMouseClicked(this::toggleCreditCardNumber);
        hiddenPasswordField.getRight().setOnMousePressed(this::toggleCreditCardNumber);
    }

    /**
     * Clears all fields
     */
    public void clear() {
        logoHbox.getChildren().clear();
        passwordNameField.clear();
        expiryMonthField.clear();
        expiryYearField.clear();
        securityNoField.clear();
        accountNoField.clear();
        sortCodeField.clear();
        hiddenPasswordField.clear();
        if(passwordToggler.getShowPassword()) {
            visiblePasswordField.clear();
        } else {
            hiddenPasswordField.clear();
        }
    }


    /**
     * Toggler specifically for a credit/debit card number - uses number formatters and keeps the card number
     * fields editable
     * @param event Event which triggers the toggle
     */
    public void toggleCreditCardNumber(Event event) {
        try {
            // Defines the classes for a CustomTextField and CustomPasswordField
            Class<?> customTextFieldClass = Class.forName("org.controlsfx.control.textfield.CustomTextField");
            Class<?> customPasswordFieldClass = Class.forName("org.controlsfx.control.textfield.CustomPasswordField");

            // Toggles the password using the passwordToggler object and retrieves either a CustomTextField object
            // or a CustomPasswordField object
            Object passwordState = passwordToggler.togglePassword(passwordVbox, getEntryWrapper());

            // If the new state of the password is "visible", the visible password is formatted, has its icons added
            // and is set as the new password field in the passwordVbox
            if(customTextFieldClass.isInstance(passwordState)) {
                CustomTextField passwordShow = (CustomTextField) passwordState;
                Insets insets = new Insets(10, 0, 0, 0);
                VBox.setMargin(passwordShow, insets);
                visiblePasswordField = passwordShow;
                setCreditDebitCardIcons();
                setCardNumberFormatters();
                passwordVbox.getChildren().set(1, visiblePasswordField);
            }

            // If the new state of the password is "hidden", the hidden password is formatted, has its icons added
            // and is set as the new password field in the passwordVbox
            else if(customPasswordFieldClass.isInstance(passwordState)) {
                CustomPasswordField passwordHide = (CustomPasswordField) passwordState;
                Insets insets = new Insets(10, 0, 0, 0);
                VBox.setMargin(passwordHide, insets);
                hiddenPasswordField = passwordHide;
                setCreditDebitCardIcons();
                setCardNumberFormatters();
                passwordVbox.getChildren().set(1, hiddenPasswordField);
            }

            // Throws a ClassCastException if the toggled password is not of either a CustomTextField or a
            // CustomPasswordField type
            else {
                throw new ClassCastException("Cannot cast object of type " + passwordState.getClass() + " to type " +
                        CustomTextField.class + " or type " + CustomPasswordField.class);
            }
        }

        // Catches a NoSuchMethodException, InvocationTargetException or IllegalAccessException if the password cannot
        // be toggled, or a ClassNotFoundException if the CustomTextField or CustomPasswordField classes cannot be found
        catch(NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("Toggler failed...");
        }
    }

    @Override
    public void setTextFormatters() {
        TextFormatter<String> textFormatter1 = PasswordCreateUtil.createTextNumberFormatter(2);
        TextFormatter<String> textFormatter2 = PasswordCreateUtil.createTextNumberFormatter(2);
        TextFormatter<String> textFormatter3 = PasswordCreateUtil.createTextNumberFormatter(3);
        TextFormatter<String> textFormatter4 = PasswordCreateUtil.createTextNumberFormatter(8);
        TextFormatter<String> textFormatter5 = PasswordCreateUtil.createTextNumberFormatter(6);
        expiryMonthField.setTextFormatter(textFormatter1);
        expiryYearField.setTextFormatter(textFormatter2);
        securityNoField.setTextFormatter(textFormatter3);
        accountNoField.setTextFormatter(textFormatter4);
        sortCodeField.setTextFormatter(textFormatter5);
    }

    @Override
    public void loadDeletePasswordModal() throws IOException {
        Stage deletePasswordStage = new Stage();
        FXMLLoader deletePasswordLoader = new FXMLLoader(BasePasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/delete-credit-debit-card-modal.fxml"));
        AnchorPane deletePasswordPane = deletePasswordLoader.load();
        Scene deletePasswordScene = new Scene(deletePasswordPane);
        CreditDebitCardDetailsController controller = deletePasswordLoader.getController();
        controller.setEntryWrapper(getEntryWrapper());
        controller.setParentFolder(getParentFolder());
        deletePasswordStage.setScene(deletePasswordScene);
        deletePasswordStage.setTitle("Delete Password");
        deletePasswordStage.initOwner(PasswordManagerApp.getMainStage());
        deletePasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = deletePasswordStage;
        stage.showAndWait();
    }

    /**
     * Obtains all details from the selected CreditDebitCardEntryWrapper object, populates all fields
     * and displays them to the user
     * @throws GeneralSecurityException Throws GeneralSecurityException if the encrypted card number cannot be decrypted,
     * or if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the encrypted/decrypted card number cannot be read
     */
    public void populatePasswordLayout() throws GeneralSecurityException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            detailsPane.setDisable(false);
            getEntryWrapper().getCreditDebitCardEntry().setDecryptedPassword
                    (EncryptDecryptPasswordsUtil.decryptPassword
                            (getEntryWrapper().getCreditDebitCardEntry().getCardNumber()));
            ImageView logo = new ImageView(getEntryWrapper().getFavicon().getImage());
            logo.setFitWidth(128);
            logo.setFitHeight(128);
            logoHbox.getChildren().add(logo);
            passwordNameField.setText(getEntryWrapper().getCreditDebitCardEntry().getPasswordName());
            if(!getEntryWrapper().getCreditDebitCardEntry().getExpiryDate().isEmpty()) {
                expiryMonthField.setText(getEntryWrapper().getCreditDebitCardEntry().getExpiryDate().split("-")[0]);
                expiryYearField.setText(getEntryWrapper().getCreditDebitCardEntry().getExpiryDate().split("-")[1]);
            } else {
                expiryMonthField.setText("");
                expiryYearField.setText("");
            }
            if(!getEntryWrapper().getCreditDebitCardEntry().getSecurityCode().isEmpty()) {
                securityNoField.setText(EncryptDecryptPasswordsUtil.decryptPassword
                        (getEntryWrapper().getCreditDebitCardEntry().getSecurityCode()));
            }
            if(!getEntryWrapper().getCreditDebitCardEntry().getAccountNumber().isEmpty()) {
                accountNoField.setText(EncryptDecryptPasswordsUtil.decryptPassword
                        (getEntryWrapper().getCreditDebitCardEntry().getAccountNumber()));
            }
            if(!getEntryWrapper().getCreditDebitCardEntry().getSortCode().isEmpty()) {
                sortCodeField.setText(EncryptDecryptPasswordsUtil.decryptPassword
                        (getEntryWrapper().getCreditDebitCardEntry().getSortCode()));
            }
            hiddenPasswordField.setText(getEntryWrapper().getCreditDebitCardEntry().getDecryptedPassword());
            visiblePasswordField.setText(getEntryWrapper().getCreditDebitCardEntry().getDecryptedPassword());
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Triggered by the "save changes" button. Retrieves either a CustomTextField object or a CustomPasswordField object
     * depending on whether the card number is hidden or visible and calls the method which updates the card entry in the
     * database
     * @throws GeneralSecurityException Throws GeneralSecurityException if the plaintext password cannot be encrypted
     * @throws IOException Throws IOException if the plaintext password cannot be read
     * @throws ClassNotFoundException Throws ClassNotFoundException if the CustomTextField or CustomPasswordField
     * classes cannot be found
     */
    @FXML
    public void updatePassword() throws GeneralSecurityException, IOException, ClassNotFoundException {
        if (PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();
            if(hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            } else if(!passwordNameField.getText().isEmpty()
                    && (!visiblePasswordField.getText().isEmpty()
                    || !hiddenPasswordField.getText().isEmpty())) {
                CreditDebitCardEntry entry = getEntryWrapper().getCreditDebitCardEntry();
                if(passwordToggler.getShowPassword()) {
                    if(CreditCardValidator.checkCardValidity(visiblePasswordField.getText(), entry.getCardType())) {
                        executeUpdate();
                        showSavedLabel();
                    } else {
                        setErrorLabel(CREDIT_DEBIT_CARD_NOT_VALID_ID, CREDIT_DEBIT_CARD_NOT_VALID_ERROR_MSG, passwordVbox);
                        logger.error("Credit/Debit card number not valid.");
                    }
                } else {
                    if(CreditCardValidator.checkCardValidity(hiddenPasswordField.getText(), entry.getCardType())) {
                        executeUpdate();
                        showSavedLabel();
                    } else {
                        setErrorLabel(CREDIT_DEBIT_CARD_NOT_VALID_ID, CREDIT_DEBIT_CARD_NOT_VALID_ERROR_MSG, passwordVbox);
                        logger.error("Credit/Debit card number not valid.");
                    }
                }
            }
        }
    }

    /**
     * Updates the credit/debit card password entry with the text in all input fields and then saves the changes in the password
     * database
     * @throws GeneralSecurityException Throws GeneralSecurityException if the plaintext password cannot be encrypted
     * @throws IOException Throws IOException if the plaintext password cannot be read
     * @throws ClassNotFoundException Throws ClassNotFoundException if the CustomTextField or CustomPasswordField
     * classes cannot be found
     */
    public void executeUpdate() throws GeneralSecurityException, IOException, ClassNotFoundException {
        PasswordEntryFolder parentFolder = getParentFolder();
        CreditDebitCardEntry entry = getEntryWrapper().getCreditDebitCardEntry();
        entry.setCardNumber(EncryptDecryptPasswordsUtil.encryptPassword(entry.getDecryptedPassword()));
        entry.setPasswordName(passwordNameField.getText());
        entry.setExpiryDate(expiryMonthField.getText() + "-" + expiryYearField.getText());
        entry.setMasterUsername(PasswordManagerApp.getLoggedInUser().getUsername());
        if(!securityNoField.getText().isEmpty()) {
            entry.setSecurityCode(EncryptDecryptPasswordsUtil.encryptPassword(securityNoField.getText()));
        } else {
            entry.setSecurityCode("");
        }
        if(!accountNoField.getText().isEmpty()) {
            entry.setAccountNumber(EncryptDecryptPasswordsUtil.encryptPassword(accountNoField.getText()));
        } else {
            entry.setAccountNumber("");
        }
        if(!sortCodeField.getText().isEmpty()) {
            entry.setSortCode(EncryptDecryptPasswordsUtil.encryptPassword(sortCodeField.getText()));
        } else {
            entry.setSortCode("");
        }
        entry.setDecryptedPassword(null);
        StoredPassSQLQueries.updateCreditDebitCardInDb(entry, parentFolder);
        entry.setDecryptedPassword(EncryptDecryptPasswordsUtil.decryptPassword(
                (getEntryWrapper().getCreditDebitCardEntry().getCardNumber())));
        PasswordManagerApp.getPasswordHomeController().populateCreditDebitCardEntryPasswords(parentFolder);
    }

    /**
     * Retrieves the flag which checks if the password name has not been set
     * @return True if the password name has not been set, else false
     */
    public Boolean isMissingPasswordNameFlag() {
        return missingPasswordNameFlag;
    }

    /**
     * Retrieves the flag which checks if the card number has not been set
     * @return True if the password name has not been set, else false
     */
    public Boolean getMissingCreditDebitCardNoFlag() {
        return missingCreditDebitCardNoFlag;
    }

    /**
     * Retrieves the flag which checks if the expiry month has not been set (only if the year has been filled in)
     * @return True if the month has not been set, else false
     */
    public Boolean getMissingExpiryDateMonth() {
        return missingExpiryDateMonth;
    }

    /**
     * Retrieves the flag which checks if an entered expiry month is too short
     * @return True if the entered expiry month is too short, else false
     */
    public Boolean getExpiryMonthTooShort() {
        return expiryMonthTooShort;
    }

    /**
     * Retrieves the flag which checks if the expiry year has not been set (only if the month has been filled in)
     * @return True if the year has not been set, else false
     */
    public Boolean getMissingExpiryDateYear() {
        return missingExpiryDateYear;
    }

    /**
     * Retrieves the flag which checks if an entered expiry year is too short
     * @return True if the entered year is too short, else false
     */
    public Boolean getExpiryYearTooShort() {
        return expiryYearTooShort;
    }

    /**
     * Sets the flag checking if the password name has not been set
     * @param missingPasswordNameFlag True if the password name has not been set, else false
     */
    public void setMissingPasswordNameFlag(Boolean missingPasswordNameFlag) {
        this.missingPasswordNameFlag = missingPasswordNameFlag;
    }

    /**
     * Sets the flag checking if the card number has not been set
     * @param missingCreditDebitCardNoFlag True if the card number has not been set, else false
     */
    public void setMissingCreditDebitCardNoFlag(Boolean missingCreditDebitCardNoFlag) {
        this.missingCreditDebitCardNoFlag = missingCreditDebitCardNoFlag;
    }

    /**
     * Sets the flag checking if the expiry month has not been set (only if the year has been set)
     * @param missingExpiryDateMonth True if the month has not been set, else false
     */
    public void setMissingExpiryDateMonth(Boolean missingExpiryDateMonth) {
        this.missingExpiryDateMonth = missingExpiryDateMonth;
    }

    /**
     * Sets the flag checking if the entered expiry month is too short
     * @param expiryMonthTooShort True if the month is too short, else false
     */
    public void setExpiryMonthTooShort(Boolean expiryMonthTooShort) {
        this.expiryMonthTooShort = expiryMonthTooShort;
    }

    /**
     * Sets the flag checking if the expiry year has not been set (only if the expiry month has been set)
     * @param missingExpiryDateYear True if the year has not been set, else false
     */
    public void setMissingExpiryDateYear(Boolean missingExpiryDateYear) {
        this.missingExpiryDateYear = missingExpiryDateYear;
    }

    /**
     * Sets the flag checking if the entered expiry year is too short
     * @param expiryYearTooShort True if the year is too short, else false
     */
    public void setExpiryYearTooShort(Boolean expiryYearTooShort) {
        this.expiryYearTooShort = expiryYearTooShort;
    }

    /**
     * Copies the full expiry date to the clipboard
     */
    public void copyExpiryDateButton() {
        copyToClipboard(expiryMonthField.getText() + "/" + expiryYearField.getText());
    }

    /**
     * Copies the CCV number to the clipboard
     */
    public void copySecurityNoButton() {
        copyToClipboard(securityNoField.getText());
    }

    /**
     * Copies the account number to the clipboard
     */
    public void copyAccountNoButton() {
        copyToClipboard(accountNoField.getText());
    }

    /**
     * Copies the sort code to the clipboard
     */
    public void copySortCodeButton() {
        copyToClipboard(sortCodeField.getText());
    }

}
