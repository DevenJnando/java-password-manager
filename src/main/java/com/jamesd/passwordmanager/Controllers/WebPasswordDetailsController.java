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

public class WebPasswordDetailsController extends BasePasswordDetailsController<WebsitePasswordEntryWrapper> implements Initializable{

    @FXML
    private JFXTextField websiteUrlField = new JFXTextField();
    @FXML
    private JFXTextField displayUsernameField = new JFXTextField();
    @FXML
    private JFXButton copyWebsiteUrlButton = new JFXButton();
    @FXML
    private JFXButton copyUsernameButton = new JFXButton();

    private static Logger logger = LoggerFactory.getLogger(WebPasswordDetailsController.class);

    public WebPasswordDetailsController() {

    }

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

    public void populatePasswordLayout() throws GeneralSecurityException,
            IOException {
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

    @FXML
    public void updatePassword() throws GeneralSecurityException, IOException, ClassNotFoundException {
        Node password = passwordVbox.getChildren().get(1);
        updatePassword(password);
        showSavedLabel();
    }

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

    public void copyWebsiteUrlButton() {
        copyToClipboard(websiteUrlField.getText());
    }

    public void copyUsernameButton() {
        copyToClipboard(displayUsernameField.getText());
    }
}
