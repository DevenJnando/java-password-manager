package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller responsible for displaying all details for a website password entry to the user, as well as updating or
 * changing any fields to the password entry in the database
 */
public class WebPasswordDetailsController extends BasePasswordDetailsController<WebsitePasswordEntryWrapper> implements Initializable{

    /**
     * FXML fields
     */
    @FXML
    private JFXTextField websiteUrlField = new JFXTextField();
    @FXML
    private JFXTextField displayUsernameField = new JFXTextField();
    @FXML
    private JFXButton copyWebsiteUrlButton = new JFXButton();
    @FXML
    private JFXButton copyUsernameButton = new JFXButton();

    private static Logger logger = LoggerFactory.getLogger(WebPasswordDetailsController.class);

    /**
     * Default constructor
     */
    public WebPasswordDetailsController() {

    }

    /**
     * Initialize method which sets text formatters for all input fields, sets icons for copy and password buttons
     * and prevents the password from being altered, unless a new one is generated by the user
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTextFormatters();
        setPasswordFormatters();
        setIcons();
        setPasswordIcons();
        visiblePasswordField.setEditable(false);
        hiddenPasswordField.setEditable(false);
    }


    @Override
    public void setTextFormatters() {
        TextFormatter<String> textFormatter1 = PasswordCreateUtil.createTextFormatter(32);
        TextFormatter<String> textFormatter2 = PasswordCreateUtil.createTextFormatter(32);
        displayUsernameField.setTextFormatter(textFormatter1);
        websiteUrlField.setTextFormatter(textFormatter2);
    }

    @Override
    public void setIcons() {
        Text copy1 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy2 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        copyWebsiteUrlButton.setGraphic(copy1);
        copyWebsiteUrlButton.setCursor(Cursor.HAND);
        copyUsernameButton.setGraphic(copy2);
        copyUsernameButton.setCursor(Cursor.HAND);
    }

    /**
     * Clears all fields
     */
    public void clear() {
        logoHbox.getChildren().clear();
        passwordNameField.clear();
        websiteUrlField.clear();
        displayUsernameField.clear();
        hiddenPasswordField.clear();
        if(passwordToggler.getShowPassword()) {
            visiblePasswordField.clear();
            } else {
            hiddenPasswordField.clear();
        }
    }

    /**
     * Obtains all details from the selected WebsitePasswordEntryWrapper object, populates all fields
     * and displays them to the user
     * @throws GeneralSecurityException Throws GeneralSecurityException if the encrypted password cannot be decrypted,
     * or if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the encrypted/decrypted password cannot be read
     */
    public void populatePasswordLayout() throws GeneralSecurityException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            setPasswordIcons();
            setTextFormatters();
            detailsPane.setDisable(false);
            getEntryWrapper().getWebsitePasswordEntry().setDecryptedPassword
                    (EncryptDecryptPasswordsUtil.decryptPassword
                            (getEntryWrapper().getWebsitePasswordEntry().getEncryptedPassword()));
            ImageView logo = new ImageView(getEntryWrapper().getFavicon().getImage());
            logo.setFitWidth(128);
            logo.setFitHeight(128);
            logoHbox.getChildren().add(logo);
            passwordNameField.setText(getEntryWrapper().getWebsitePasswordEntry().getPasswordName());
            websiteUrlField.setText(getEntryWrapper().getWebsitePasswordEntry().getSiteUrl());
            displayUsernameField.setText(getEntryWrapper().getWebsitePasswordEntry().getPasswordUsername());
            hiddenPasswordField.setText(getEntryWrapper().getWebsitePasswordEntry().getDecryptedPassword());
            visiblePasswordField.setText(getEntryWrapper().getWebsitePasswordEntry().getDecryptedPassword());
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @Override
    protected void checkAndResetLabels() {
        logger.info("Method not currently used.");
    }

    @Override
    protected Boolean hasErroneousFields() {
        logger.info("Method not currently used.");
        return null;
    }

    /**
     * Temporarily sets the visible/hidden password fields to editable, generates a new password, sets it in the
     * WebsitePasswordEntry object, displays it to the user and disables editing to the visible/hidden password fields
     * again
     */
    public void generateNewPasswordAndUpdate() {
        visiblePasswordField.setEditable(true);
        hiddenPasswordField.setEditable(true);
        generateNewPassword();
        WebsitePasswordEntryWrapper wrapper = getEntryWrapper();
        if(passwordToggler.getShowPassword()) {
            wrapper.getWebsitePasswordEntry().setDecryptedPassword(visiblePasswordField.getText());
            wrapper.getWebsitePasswordEntry().setDateSet(LocalDate.now().toString());
            setEntryWrapper(wrapper);
            passwordVbox.getChildren().set(1, visiblePasswordField);
        } else {
            wrapper.getWebsitePasswordEntry().setDecryptedPassword(hiddenPasswordField.getText());
            wrapper.getWebsitePasswordEntry().setDateSet(LocalDate.now().toString());
            setEntryWrapper(wrapper);
            passwordVbox.getChildren().set(1, hiddenPasswordField);
        }
        visiblePasswordField.setEditable(false);
        hiddenPasswordField.setEditable(false);
    }

    /**
     * Triggered by the "save changes" button. Retrieves either a CustomTextField object or a CustomPasswordField object
     * depending on whether the password is hidden or visible and calls the method which updates the password entry in the
     * database
     * @throws GeneralSecurityException Throws GeneralSecurityException if the plaintext password cannot be encrypted
     * @throws IOException Throws IOException if the plaintext password cannot be read
     * @throws ClassNotFoundException Throws ClassNotFoundException if the CustomTextField or CustomPasswordField
     * classes cannot be found
     */
    @FXML
    public void updatePassword() throws GeneralSecurityException, IOException, ClassNotFoundException {
        Node password = passwordVbox.getChildren().get(1);
        updatePassword(password);
        showSavedLabel();
    }

    /**
     * Updates the website password entry with the text in all input fields and then saves the changes in the password
     * database
     * @param password CustomTextField if visible or CustomPasswordField if hidden
     * @param <T> Actual type of the password object (Should only ever be CustomTextField or CustomPasswordField)
     * @throws GeneralSecurityException Throws GeneralSecurityException if the plaintext password cannot be encrypted
     * @throws IOException Throws IOException if the plaintext password cannot be read
     * @throws ClassNotFoundException Throws ClassNotFoundException if the CustomTextField or CustomPasswordField
     * classes cannot be found
     */
    public <T> void updatePassword(T password) throws GeneralSecurityException, IOException, ClassNotFoundException {
        PasswordEntryFolder parentFolder = getParentFolder();
        WebsitePasswordEntry entry = getEntryWrapper().getWebsitePasswordEntry();
        if(password instanceof CustomTextField) {
            entry.setEncryptedPassword(EncryptDecryptPasswordsUtil.encryptPassword(((CustomTextField) password).getText()));
        } else if(password instanceof CustomPasswordField) {
            entry.setEncryptedPassword(EncryptDecryptPasswordsUtil.encryptPassword(((CustomPasswordField) password).getText()));
        } else {
            throw new ClassNotFoundException("Cannot cast password to type " + password.getClass());
        }
        entry.setPasswordName(passwordNameField.getText());
        entry.setSiteUrl(websiteUrlField.getText());
        entry.setMasterUsername(PasswordManagerApp.getLoggedInUser().getUsername());
        entry.setPasswordUsername(displayUsernameField.getText());
        entry.setDecryptedPassword(null);
        StoredPassSQLQueries.updateWebsitePasswordInDb(entry, parentFolder);
        PasswordManagerApp.getPasswordHomeController().populateWebsiteEntryPasswords(parentFolder);
    }

    /**
     * Copies the website URL to the clipboard
     */
    public void copyWebsiteUrlButton() {
        copyToClipboard(websiteUrlField.getText());
    }

    /**
     * Copies the username to the clipboard
     */
    public void copyUsernameButton() {
        copyToClipboard(displayUsernameField.getText());
    }
}
