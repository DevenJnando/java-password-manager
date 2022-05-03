package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.CreditCardValidator;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;

import javax.security.auth.login.LoginException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Class which is responsible for adding a new Credit/Debit card entry to the password database. Once validation passes,
 * the credit/debit card entry should be added to the specified folder in the user's CosmosDB container.
 */
public class AddCreditDebitCardController extends NewPasswordController implements Initializable {

    /**
     * FXML fields
     */
    @FXML
    VBox passwordVbox = new VBox();
    @FXML
    JFXTextField passwordName;
    @FXML
    CustomTextField visibleCreditDebitCardNoField = new CustomTextField();
    @FXML
    CustomPasswordField hiddenCreditDebitCardNoField = new CustomPasswordField();
    @FXML
    ChoiceBox<String> cardTypeField;
    @FXML
    JFXTextField expiryDateMonthField;
    @FXML
    JFXTextField expiryDateYearField;
    @FXML
    JFXTextField securityCodeField;
    @FXML
    JFXTextField accountNoField;
    @FXML
    JFXTextField sortCodeField;
    @FXML
    Button confirmNewPasswordButton;

    /**
     * Validation flags
     */
    private Boolean folderNotSelectedFlag = false;
    private Boolean missingPasswordNameFlag = false;
    private Boolean missingCreditDebitCardNoFlag = false;
    private Boolean missingExpiryDateMonth = false;
    private Boolean expiryMonthTooShort = false;
    private Boolean missingExpiryDateYear = false;
    private Boolean expiryYearTooShort = false;

    /**
     * IDs of error labels and error messages they should display
     */
    private final String PASSWORD_FOLDER_NOT_SELECTED_ID = "passwordFolderNotSelected";
    private final String PASSWORD_NAME_EMPTY_ID = "passwordNameEmptyLabel";
    private final String CREDIT_DEBIT_CARD_NOT_VALID_ID = "creditDebitCardNotValidLabel";
    private final String CREDIT_DEBIT_CARD_NO_EMPTY_ID = "creditDebitCardNoEmptyLabel";
    private final String EXPIRY_MONTH_NOT_SET_ID = "expiryMonthNotSetLabel";
    private final String EXPIRY_MONTH_TOO_SHORT_ID = "expiryMonthTooShortLabel";
    private final String EXPIRY_YEAR_NOT_SET_ID = "expiryYearNotSetLabel";
    private final String EXPIRY_YEAR_TOO_SHORT_ID = "expiryYearTooShortLabel";
    private final String PASSWORD_FOLDER_NOT_SELECTED_ERROR_MSG = "Please select a folder to save this password in.";
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
    public AddCreditDebitCardController() {
        passwordToggler = new Toggler("hiddenCreditDebitCardNoField", "visibleCreditDebitCardNoField");
        confirmPasswordToggler = new Toggler("hiddenCreditDebitCardNoField", "visibleCreditDebitCardNoField");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPasswordToggler(new Toggler("hiddenCreditDebitCardNoField", "visibleCreditDebitCardNoField"));
        setConfirmPasswordToggler(new Toggler("hiddenCreditDebitCardNoField", "visibleCreditDebitCardNoField"));
        setTextFormatters();
        setIcons();
        populateCardChoiceBox();
    }

    @Override
    public void setIcons() {
        Text eye1 = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text eyeSlash1 = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
        visibleCreditDebitCardNoField.setRight(eyeSlash1);
        visibleCreditDebitCardNoField.getRight().setCursor(Cursor.HAND);
        hiddenCreditDebitCardNoField.setRight(eye1);
        hiddenCreditDebitCardNoField.getRight().setCursor(Cursor.HAND);
        visibleCreditDebitCardNoField.getRight().setOnMouseClicked(this::toggleCreditCardNumber);
        visibleCreditDebitCardNoField.getRight().setOnMousePressed(this::toggleCreditCardNumber);
        hiddenCreditDebitCardNoField.getRight().setOnMouseClicked(this::toggleCreditCardNumber);
        hiddenCreditDebitCardNoField.getRight().setOnMousePressed(this::toggleCreditCardNumber);
    }

    /**
     * Populates the "Card type" choice box. It only has three kinds of card which can currently be selected - MasterCard,
     * Visa and American Express.
     */
    public void populateCardChoiceBox() {
        ArrayList<String> options = new ArrayList<>();
        options.add("MasterCard");
        options.add("Visa");
        options.add("American Express");
        ObservableList<String> oOptions = FXCollections.observableList(options);
        cardTypeField.setItems(oOptions);
        cardTypeField.getSelectionModel().select(0);
    }

    public void toggleCreditCardNumber(Event event) {
        try {
            Class<?> customTextFieldClass = Class.forName("org.controlsfx.control.textfield.CustomTextField");
            Class<?> customPasswordFieldClass = Class.forName("org.controlsfx.control.textfield.CustomPasswordField");
            Object passwordState = passwordToggler.togglePassword(passwordVbox);
            if(customTextFieldClass.isInstance(passwordState)) {
                CustomTextField passwordShow = (CustomTextField) passwordState;
                visibleCreditDebitCardNoField = passwordShow;
                setTextFormatters();
                setIcons();
                passwordVbox.getChildren().set(5, visibleCreditDebitCardNoField);
            } else if(customPasswordFieldClass.isInstance(passwordState)) {
                CustomPasswordField passwordHide = (CustomPasswordField) passwordState;
                hiddenCreditDebitCardNoField = passwordHide;
                setTextFormatters();
                setIcons();
                passwordVbox.getChildren().set(5, hiddenCreditDebitCardNoField);
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

    @Override
    protected void checkAndResetLabels() {
        if(retrieveNode(CREDIT_DEBIT_CARD_NOT_VALID_ID, passwordVbox) != null) {
            resetLabel(CREDIT_DEBIT_CARD_NOT_VALID_ID, passwordVbox);
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
        if(PasswordManagerApp.getPasswordHomeController().getBaseAddPasswordController().getSelectedFolder() == null) {
            setErrorLabel(PASSWORD_FOLDER_NOT_SELECTED_ID, PASSWORD_FOLDER_NOT_SELECTED_ERROR_MSG, passwordVbox);
            setFolderNotSelectedFlag(true);
            erroneousFields = true;
        } if(passwordName.getText().isEmpty()) {
            setErrorLabel(PASSWORD_NAME_EMPTY_ID, PASSWORD_NAME_EMPTY_ERROR_MSG, passwordVbox);
            setMissingPasswordNameFlag(true);
            erroneousFields = true;
            logger.error("Password name is empty");
        } if(visibleCreditDebitCardNoField.getText().isEmpty() && hiddenCreditDebitCardNoField.getText().isEmpty()) {
            setErrorLabel(CREDIT_DEBIT_CARD_NO_EMPTY_ID, CREDIT_DEBIT_CARD_NO_EMPTY_ERROR_MSG, passwordVbox);
            setMissingCreditDebitCardNoFlag(true);
            erroneousFields = true;
            logger.error("Credit/Debit card number is missing.");
        } if(!expiryDateMonthField.getText().isEmpty()) {
            if(!expiryDateYearField.getText().isEmpty()) {
                if(expiryDateMonthField.getText().length() < 2 ||
                        (Double.parseDouble(expiryDateMonthField.getText()) < 01 ||
                                Double.parseDouble(expiryDateMonthField.getText()) > 12)) {
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
        } if(!expiryDateYearField.getText().isEmpty()) {
            if(!expiryDateMonthField.getText().isEmpty()) {
                if(expiryDateYearField.getText().length() < 2) {
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
    protected void addNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException {
        String currentDate = LocalDate.now().toString();
        String hashedCardNumber = "";
        String hashedSecurityCode = "";
        String hashedAccountNumber = "";
        String hashedSortCode = "";

        String fullExpiryDate = !expiryDateMonthField.getText().isEmpty() ?
                expiryDateMonthField.getText() + "-" + expiryDateYearField.getText() :
                "";
        if(passwordToggler.getShowPassword()) {
            hashedCardNumber = EncryptDecryptPasswordsUtil.encryptPassword(visibleCreditDebitCardNoField.getText());
        } else {
            hashedCardNumber = EncryptDecryptPasswordsUtil.encryptPassword(hiddenCreditDebitCardNoField.getText());
        }

        hashedSecurityCode = !securityCodeField.getText().isEmpty() ?
                EncryptDecryptPasswordsUtil.encryptPassword(securityCodeField.getText()) :
                hashedSecurityCode;
        hashedAccountNumber = !accountNoField.getText().isEmpty() ?
                EncryptDecryptPasswordsUtil.encryptPassword(accountNoField.getText()) :
                hashedAccountNumber;
        hashedSortCode = !sortCodeField.getText().isEmpty() ?
                EncryptDecryptPasswordsUtil.encryptPassword(sortCodeField.getText()) :
                hashedSortCode;

        StoredPassSQLQueries.addNewCreditDebitCardToDb(PasswordManagerApp
                        .getPasswordHomeController()
                        .getBaseAddPasswordController()
                        .getSelectedFolder(),
                passwordName.getText(),
                hashedCardNumber,
                cardTypeField.getSelectionModel().getSelectedItem(),
                fullExpiryDate,
                hashedSecurityCode,
                hashedAccountNumber,
                hashedSortCode,
                PasswordManagerApp.getLoggedInUser().getUsername(),
                currentDate);

        setMissingCreditDebitCardNoFlag(false);
        PasswordHomeController.getStage().close();
        PasswordManagerApp.getPasswordHomeController().viewNewlyAddedPassword();
    }

    @Override
    protected void confirmAndAddNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException {
        if (PasswordManagerApp.getLoggedInUser() != null) {
            checkAndResetLabels();
            if(hasErroneousFields()) {
                logger.info("Erroneous fields are present. Fix them!");
            }
            else if(PasswordManagerApp.getPasswordHomeController().getBaseAddPasswordController().getSelectedFolder() != null
                    && !passwordName.getText().isEmpty()
                    && (!visibleCreditDebitCardNoField.getText().isEmpty()
                    || !hiddenCreditDebitCardNoField.getText().isEmpty())){
                if(passwordToggler.getShowPassword()) {
                    if(CreditCardValidator.checkCardValidity(visibleCreditDebitCardNoField.getText(),
                            cardTypeField.getSelectionModel().getSelectedItem())) {
                        addNewPassword();
                    } else {
                        setErrorLabel(CREDIT_DEBIT_CARD_NOT_VALID_ID, CREDIT_DEBIT_CARD_NOT_VALID_ERROR_MSG, passwordVbox);
                        logger.error("Credit/Debit card number not valid.");
                    }
                } else {
                    if(CreditCardValidator.checkCardValidity(hiddenCreditDebitCardNoField.getText(),
                            cardTypeField.getSelectionModel().getSelectedItem())) {
                        addNewPassword();
                    } else {
                        setErrorLabel(CREDIT_DEBIT_CARD_NOT_VALID_ID, CREDIT_DEBIT_CARD_NOT_VALID_ERROR_MSG, passwordVbox);
                        logger.error("Credit/Debit card number not valid.");
                    }
                }
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @Override
    protected void setTextFormatters() {
        TextFormatter<String> textFormatter1 = PasswordCreateUtil.createTextNumberFormatter(16);
        TextFormatter<String> textFormatter2 = PasswordCreateUtil.createTextNumberFormatter(2);
        TextFormatter<String> textFormatter3 = PasswordCreateUtil.createTextNumberFormatter(2);
        TextFormatter<String> textFormatter4 = PasswordCreateUtil.createTextNumberFormatter(3);
        TextFormatter<String> textFormatter5 = PasswordCreateUtil.createTextNumberFormatter(8);
        TextFormatter<String> textFormatter6 = PasswordCreateUtil.createTextNumberFormatter(6);
        TextFormatter<String> passwordFormatter1 = PasswordCreateUtil.createTextNumberFormatter(16);
        visibleCreditDebitCardNoField.setTextFormatter(textFormatter1);
        hiddenCreditDebitCardNoField.setTextFormatter(passwordFormatter1);
        expiryDateMonthField.setTextFormatter(textFormatter2);
        expiryDateYearField.setTextFormatter(textFormatter3);
        securityCodeField.setTextFormatter(textFormatter4);
        accountNoField.setTextFormatter(textFormatter5);
        sortCodeField.setTextFormatter(textFormatter6);
    }

    /**
     * Retrieves the flag which checks if a folder has been selected or not
     * @return True if no folder has been selected, else false
     */
    public Boolean getFolderNotSelectedFlag() {
        return folderNotSelectedFlag;
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
     * Sets the flag checking if no folder has been selected
     * @param folderNotSelectedFlag True if no folder has been selected, else false
     */
    public void setFolderNotSelectedFlag(Boolean folderNotSelectedFlag) {
        this.folderNotSelectedFlag = folderNotSelectedFlag;
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
}
