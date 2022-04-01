package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
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

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller for the details screen for a DatabasePasswordEntry object.
 */
public class DatabasePasswordDetailsController extends BasePasswordDetailsController<DatabasePasswordEntryWrapper> implements Initializable {

    /**
     * FXML fields
     */
    @FXML
    private JFXTextField hostnameField = new JFXTextField();
    @FXML
    private JFXTextField databaseNameField = new JFXTextField();
    @FXML
    private JFXTextField databaseUsernameField = new JFXTextField();
    @FXML
    private JFXButton copyHostnameButton = new JFXButton();
    @FXML
    private JFXButton copyDatabaseNameButton = new JFXButton();
    @FXML
    private JFXButton copyDatabaseUsernameButton = new JFXButton();


    /**
     * Default constructor
     */
    public DatabasePasswordDetailsController() {

    }

    /**
     * Initialize method which sets all text formatters and sets all icons
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
    protected void setTextFormatters() {
        TextFormatter<String> textFormatter1 = PasswordCreateUtil.createTextFormatter(32);
        TextFormatter<String> textFormatter2 = PasswordCreateUtil.createTextFormatter(32);
        TextFormatter<String> textFormatter3 = PasswordCreateUtil.createTextFormatter(24);
        hostnameField.setTextFormatter(textFormatter1);
        databaseNameField.setTextFormatter(textFormatter2);
        databaseUsernameField.setTextFormatter(textFormatter3);
    }

    @Override
    protected void setIcons() {
        Text copy1 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy2 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy3 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        copyHostnameButton.setGraphic(copy1);
        copyHostnameButton.setCursor(Cursor.HAND);
        copyDatabaseNameButton.setGraphic(copy2);
        copyDatabaseNameButton.setCursor(Cursor.HAND);
        copyDatabaseUsernameButton.setGraphic(copy3);
        copyDatabaseUsernameButton.setCursor(Cursor.HAND);
    }

    /**
     * Populates the details screen with the details of the selected DatabasePasswordEntry object for the user to view
     * @throws GeneralSecurityException Throws a LoginException if the user calls this method whilst not logged in and
     * a GeneralSecurityException if the encrypted password retrieved from the database cannot be decrypted
     * @throws IOException Throws an IOException if the encrypted password from the database cannot be read
     */
    public void populatePasswordLayout() throws GeneralSecurityException,
            IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            setPasswordIcons();
            setTextFormatters();
            detailsPane.setDisable(false);
            getEntryWrapper().getDatabasePasswordEntry().setDecryptedPassword
                    (EncryptDecryptPasswordsUtil.decryptPassword
                            (getEntryWrapper().getDatabasePasswordEntry().getEncryptedPassword()));

            ImageView logo = new ImageView(getEntryWrapper().getFavicon().getImage());
            logo.setFitWidth(128);
            logo.setFitHeight(128);
            logoHbox.getChildren().add(logo);

            passwordNameField.setText(getEntryWrapper().getDatabasePasswordEntry().getPasswordName());
            hostnameField.setText(getEntryWrapper().getDatabasePasswordEntry().getHostName());
            databaseNameField.setText(getEntryWrapper().getDatabasePasswordEntry().getDatabaseName());
            databaseUsernameField.setText(getEntryWrapper().getDatabasePasswordEntry().getDatabaseUsername());
            hiddenPasswordField.setText(getEntryWrapper().getDatabasePasswordEntry().getDecryptedPassword());
            visiblePasswordField.setText(getEntryWrapper().getDatabasePasswordEntry().getDecryptedPassword());
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Clears the details screen of all details
     */
    public void clear() {
        logoHbox.getChildren().clear();
        passwordNameField.clear();
        hostnameField.clear();
        databaseNameField.clear();
        databaseUsernameField.clear();
        hiddenPasswordField.clear();
        if(passwordToggler.getShowPassword()) {
            visiblePasswordField.clear();
        } else {
            hiddenPasswordField.clear();
        }
    }

    /**
     * Generates a new random password for the user and displays it on screen
     */
    public void generateNewPasswordAndUpdate() {
        visiblePasswordField.setEditable(true);
        hiddenPasswordField.setEditable(true);
        generateNewPassword();
        DatabasePasswordEntryWrapper wrapper = getEntryWrapper();
        if(passwordToggler.getShowPassword()) {
            wrapper.getDatabasePasswordEntry().setDecryptedPassword(visiblePasswordField.getText());
            wrapper.getDatabasePasswordEntry().setDateSet(LocalDate.now().toString());
            setEntryWrapper(wrapper);
            passwordVbox.getChildren().set(1, visiblePasswordField);
        } else {
            wrapper.getDatabasePasswordEntry().setDecryptedPassword(hiddenPasswordField.getText());
            wrapper.getDatabasePasswordEntry().setDateSet(LocalDate.now().toString());
            setEntryWrapper(wrapper);
            passwordVbox.getChildren().set(1, hiddenPasswordField);
        }
        visiblePasswordField.setEditable(false);
        hiddenPasswordField.setEditable(false);
    }

    /**
     * Calls the updatePassword method with the DatabasePasswordEntry details to be updated in the database and displays
     * a success message to the user on completion
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the plaintext password cannot be encrypted
     * @throws IOException Throws an IOException if the plaintext password cannot be read
     * @throws ClassNotFoundException Throws a ClassNotFoundException if the CustomTextField or CustomPasswordField
     * classes cannot be found
     */
    @FXML
    public void updatePassword() throws GeneralSecurityException, IOException, ClassNotFoundException {
        Node password = passwordVbox.getChildren().get(1);
        updatePassword(password);
        showSavedLabel();
    }

    /**
     * Updates the selected DatabasePasswordEntry password in its parent folder in the database
     * @param password The displayed password field
     * @param <T> The class of the displayed password field (Should only ever be CustomTextField or CustomPasswordField)
     * @throws GeneralSecurityException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public <T> void updatePassword(T password) throws GeneralSecurityException, IOException, ClassNotFoundException {
        PasswordEntryFolder parentFolder = getParentFolder();
        DatabasePasswordEntry entry = getEntryWrapper().getDatabasePasswordEntry();
        if(password instanceof CustomTextField) {
            entry.setEncryptedPassword(EncryptDecryptPasswordsUtil.encryptPassword(((CustomTextField) password).getText()));
        } else if(password instanceof CustomPasswordField) {
            entry.setEncryptedPassword(EncryptDecryptPasswordsUtil.encryptPassword(((CustomPasswordField) password).getText()));
        } else {
            throw new ClassNotFoundException("Cannot cast password to type " + password.getClass());
        }
        entry.setPasswordName(passwordNameField.getText());
        entry.setHostName(hostnameField.getText());
        entry.setMasterUsername(PasswordManagerApp.getLoggedInUser().getUsername());
        entry.setDatabaseName(databaseNameField.getText());
        entry.setDatabaseUsername(databaseUsernameField.getText());
        entry.setDecryptedPassword(null);
        StoredPassSQLQueries.updateDatabasePasswordInDb(entry, parentFolder);
        PasswordManagerApp.getPasswordHomeController().populateDatabaseEntryPasswords(parentFolder);
    }

    @Override
    protected void checkAndResetLabels() {

    }

    @Override
    protected Boolean hasErroneousFields() {
        return null;
    }

    /**
     * Copies the hostname to the clipboard
     */
    public void copyHostnameButton() {
        copyToClipboard(hostnameField.getText());
    }

    /**
     * Copies the database name to the clipboard
     */
    public void copyDatabaseNameButton() {
        copyToClipboard(databaseNameField.getText());
    }

    /**
     * Copies the database username to the clipboard
     */
    public void copyDatabaseUsernameButton() {
        copyToClipboard(databaseUsernameField.getText());
    }
}
