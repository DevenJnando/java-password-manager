package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StorageAccountManager;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.Models.Passwords.DocumentEntry;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Utils.TransitionUtil;
import com.jamesd.passwordmanager.Wrappers.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstract class for populating password entry details.
 * Contains common fields methods for all password details classes
 */
public abstract class BasePasswordDetailsController<T extends BaseWrapper> extends ModifyPasswordController {

    /**
     * FXML fields
     */
    @FXML
    VBox passwordVbox = new VBox();
    @FXML
    protected BorderPane detailsPane;
    @FXML
    protected Label savedLabel = new Label();
    @FXML
    protected JFXTextField passwordNameField = new JFXTextField();
    @FXML
    protected HBox logoHbox = new HBox();
    @FXML
    protected Button savePasswordButton = new Button();
    @FXML
    protected Button deletePasswordButton = new Button();
    @FXML
    protected JFXButton copyPasswordNameButton = new JFXButton();
    @FXML
    protected JFXButton copyPasswordButton = new JFXButton();
    @FXML
    protected JFXButton generateNewPasswordButton = new JFXButton();


    protected PasswordEntryFolder parentFolder = new PasswordEntryFolder();
    protected T wrapper = null;
    protected static Stage stage;

    /**
     * Default constructor
     */
    public BasePasswordDetailsController() {

    }

    /**
     * Method for setting icons for password input fields only
     */
    protected void setPasswordIcons() {
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
        generateNewPasswordButton.setCursor(Cursor.HAND);
        savePasswordButton.setGraphic(save);
        savePasswordButton.setCursor(Cursor.HAND);
        deletePasswordButton.setGraphic(delete);
        deletePasswordButton.setCursor(Cursor.HAND);
        hiddenPasswordField.setRight(hiddenPasswordIcon);
        hiddenPasswordField.getRight().setCursor(Cursor.HAND);
        visiblePasswordField.setRight(shownPasswordIcon);
        visiblePasswordField.getRight().setCursor(Cursor.HAND);
        visiblePasswordField.getRight().setOnMouseClicked(this::togglePassword);
        visiblePasswordField.getRight().setOnMousePressed(this::togglePassword);
        hiddenPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        hiddenPasswordField.getRight().setOnMousePressed(this::togglePassword);
    }

    /**
     * Method for setting text formatters for password input fields only
     */
    protected void setPasswordFormatters() {
        TextFormatter<String> passwordFormatter1 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter2 = PasswordCreateUtil.createTextFormatter(24);
        hiddenPasswordField.setTextFormatter(passwordFormatter1);
        visiblePasswordField.setTextFormatter(passwordFormatter2);
    }

    /**
     * Method for setting text formatters for card numbers only
     */
    protected void setCardNumberFormatters() {
        TextFormatter<String> cardNumberFormatter1 = PasswordCreateUtil.createTextNumberFormatter(16);
        TextFormatter<String> cardNumberFormatter2 = PasswordCreateUtil.createTextNumberFormatter(16);
        hiddenPasswordField.setTextFormatter(cardNumberFormatter1);
        visiblePasswordField.setTextFormatter(cardNumberFormatter2);
    }

    /**
     * Loads the "delete single password" modal. From here, a selected password entry can be deleted.
     * @throws IOException Throws an IOException if the "delete single password" modal cannot be loaded
     */
    protected abstract void loadDeletePasswordModal() throws IOException;

    /**
     * Displays the "Saved!" label to alert the user that their attempt to save changes to their password entry has
     * been successful.
     */
    protected void showSavedLabel() {
        FadeTransition fader = TransitionUtil.createFader(savedLabel);
        SequentialTransition fade = new SequentialTransition(savedLabel, fader);
        savedLabel.setText("Saved!");
        savedLabel.setTextFill(Color.GREEN);
        fade.play();
    }

    /**
     * Disables the details pane
     */
    protected void disable() {
        detailsPane.setDisable(true);
    }

    /**
     * Retrieves the wrapper object for the specified password entry
     * @return Returns a wrapper which extends the BaseWrapper class
     */
    protected T getEntryWrapper() {
        return wrapper;
    }

    /**
     * Sets the wrapper object for the specified password entry
     * @param wrapper The wrapper for the password entry which extends the BaseWrapper class
     */
    protected void setEntryWrapper(T wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Calls the loadDeletePasswordModal method upon click
     * @throws LoginException Throws LoginException if the user calls this method whilst not logged in
     * @throws IOException Throws IOException if the "delete single password" modal cannot be loaded
     */
    @FXML
    public void deletePassword() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadDeletePasswordModal();
            logger.info("Loaded delete passwords modal.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @Override
    public void togglePassword(Event event){
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
                passwordShow.setEditable(false);
                visiblePasswordField = passwordShow;
                setPasswordIcons();
                setTextFormatters();
                passwordVbox.getChildren().set(1, visiblePasswordField);
            }

            // If the new state of the password is "hidden", the hidden password is formatted, has its icons added
            // and is set as the new password field in the passwordVbox
            else if(customPasswordFieldClass.isInstance(passwordState)) {
                CustomPasswordField passwordHide = (CustomPasswordField) passwordState;
                Insets insets = new Insets(10, 0, 0, 0);
                VBox.setMargin(passwordHide, insets);
                passwordHide.setEditable(false);
                hiddenPasswordField = passwordHide;
                setPasswordIcons();
                setTextFormatters();
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

    /**
     * Closes the "delete single password" modal without deleting anything
     */
    @FXML
    protected void cancelDelete() {
        getStage().close();
    }

    /**
     * Called when the user confirms their decision to delete a password entry. Checks which type of entry should be
     * deleted and then removes the entry using the appropriate method.
     * @throws ClassNotFoundException Throws a ClassNotFoundException if the WebsitePasswordEntryWrapper or the
     * DatabasePasswordEntryWrapper classes cannot be found
     */
    @FXML
    protected void confirmDelete() throws ClassNotFoundException {
        if(getEntryWrapper() instanceof WebsitePasswordEntryWrapper) {
            WebsitePasswordEntryWrapper websitePasswordEntryWrapper = (WebsitePasswordEntryWrapper) getEntryWrapper();
            WebsitePasswordEntry entry = websitePasswordEntryWrapper.getWebsitePasswordEntry();
            deleteWebsitePasswordEntry(entry);
        }
        if(getEntryWrapper() instanceof DatabasePasswordEntryWrapper) {
            DatabasePasswordEntryWrapper databasePasswordEntryWrapper = (DatabasePasswordEntryWrapper) getEntryWrapper();
            DatabasePasswordEntry entry = databasePasswordEntryWrapper.getDatabasePasswordEntry();
            deleteDatabasePasswordEntry(entry);
        }
        if(getEntryWrapper() instanceof CreditDebitCardEntryWrapper) {
            CreditDebitCardEntryWrapper creditDebitCardEntryWrapper = (CreditDebitCardEntryWrapper) getEntryWrapper();
            CreditDebitCardEntry entry = creditDebitCardEntryWrapper.getCreditDebitCardEntry();
            deleteCreditDebitCardEntry(entry);
        }
        if(getEntryWrapper() instanceof DocumentWrapper) {
            DocumentWrapper documentWrapper = (DocumentWrapper) getEntryWrapper();
            DocumentEntry entry = documentWrapper.getDocumentEntry();
            deleteDocumentEntry(entry);
        }
    }

    /**
     * Deletes a WebsitePasswordEntry from its parent folder in the password database
     * @param entry The selected WebsitePasswordEntry to be deleted
     * @throws ClassNotFoundException Throws a ClassNotFoundException if the WebsitePasswordEntry class cannot be found
     */
    private void deleteWebsitePasswordEntry(WebsitePasswordEntry entry) throws ClassNotFoundException {
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteSingleEntry(entry, getParentFolder());

        // Details screen is cleared once the password is deleted
        PasswordManagerApp.getPasswordDetailsController().setNoDetailsLoaded();
        // List of website password entries in the parent folder is repopulated once the password is deleted
        PasswordManagerApp.getPasswordHomeController().populateWebsiteEntryPasswords(getParentFolder());
    }

    /**
     * Deletes a DatabasePasswordEntry from its parent folder in the password database
     * @param entry The selected DatabasePasswordEntry to be deleted
     * @throws ClassNotFoundException
     */
    private void deleteDatabasePasswordEntry(DatabasePasswordEntry entry) throws ClassNotFoundException {
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteSingleEntry(entry, getParentFolder());

        // Details screen is cleared once the password is deleted
        PasswordManagerApp.getPasswordDetailsController().setNoDetailsLoaded();
        // List of database password entries in the parent folder is repopulated once the password is deleted
        PasswordManagerApp.getPasswordHomeController().populateDatabaseEntryPasswords(getParentFolder());
    }

    /**
     * Deletes a CreditDebitCardEntry from its parent folder in the password database
     * @param entry The selected CreditDebitCardEntry to be deleted
     * @throws ClassNotFoundException
     */
    private void deleteCreditDebitCardEntry(CreditDebitCardEntry entry) throws ClassNotFoundException {
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteSingleEntry(entry, getParentFolder());

        // Details screen is cleared once the password is deleted
        PasswordManagerApp.getPasswordDetailsController().setNoDetailsLoaded();
        // List of database password entries in the parent folder is repopulated once the password is deleted
        PasswordManagerApp.getPasswordHomeController().populateCreditDebitCardEntryPasswords(getParentFolder());
    }

    /**
     * Deletes a DocumentEntry from its parent folder in the password database
     * @param entry The selected DocumentEntry to be deleted
     * @throws ClassNotFoundException
     */
    private void deleteDocumentEntry(DocumentEntry entry) throws ClassNotFoundException {
        StorageAccountManager.deleteBlob(getParentFolder().getPasswordFolder() + "/" + entry.getPasswordName());
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteSingleEntry(entry, getParentFolder());

        // Details screen is cleared once the password is deleted
        PasswordManagerApp.getPasswordDetailsController().setNoDetailsLoaded();
        // List of database password entries in the parent folder is repopulated once the password is deleted
        PasswordManagerApp.getPasswordHomeController().populateDocumentEntryPasswords(getParentFolder());
    }

    /**
     * Copies the password name to the clipboard
     */
    @FXML
    protected void copyPasswordNameButton() {
        copyToClipboard(passwordNameField.getText());
    }

    /**
     * Copies the password to the clipboard
     */
    @FXML
    protected void copyPasswordToClipboard() {
        // Determines if the password is visible or hidden
        if(passwordToggler.getShowPassword()) {
            CustomTextField password = (CustomTextField) passwordVbox.getChildren().get(1);
            copyToClipboard(password.getText());
        } else {
            CustomPasswordField password = (CustomPasswordField) passwordVbox.getChildren().get(1);
            copyToClipboard(password.getText());
        }
    }

    /**
     * Retrieves the stage containing any modals for this class
     * @return the Stage object where any modals will be stored
     */
    protected static Stage getStage() {
        return stage;
    }

    /**
     * Sets the parent folder where password entries will be loaded from
     * @param folder selected PasswordEntryFolder object
     */
    public void setParentFolder(PasswordEntryFolder folder) {
        parentFolder = folder;
    }

    /**
     * Retrieves the parent folder where password entries will be laoded from
     * @return selected PasswordEntryFolder object
     */
    public PasswordEntryFolder getParentFolder() {
        return parentFolder;
    }
}
