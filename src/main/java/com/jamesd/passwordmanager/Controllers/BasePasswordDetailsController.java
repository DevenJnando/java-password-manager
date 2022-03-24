package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Utils.TransitionUtil;
import com.jamesd.passwordmanager.Wrappers.BaseWrapper;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class BasePasswordDetailsController<T extends BaseWrapper> extends ModifyPasswordController {

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

    protected static PasswordEntryFolder parentFolder = new PasswordEntryFolder();
    protected T wrapper = null;
    protected static Stage stage;

    public BasePasswordDetailsController() {

    }

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

    protected void setPasswordFormatters() {
        TextFormatter<String> passwordFormatter1 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter2 = PasswordCreateUtil.createTextFormatter(24);
        hiddenPasswordField.setTextFormatter(passwordFormatter1);
        visiblePasswordField.setTextFormatter(passwordFormatter2);
    }

    protected void loadDeletePasswordModal() throws IOException {
        Stage deletePasswordStage = new Stage();
        FXMLLoader deletePasswordLoader = new FXMLLoader(BasePasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/delete-single-password-modal.fxml"));
        AnchorPane deletePasswordPane = deletePasswordLoader.load();
        Scene deletePasswordScene = new Scene(deletePasswordPane);
        deletePasswordStage.setScene(deletePasswordScene);
        deletePasswordStage.setTitle("Delete Password");
        deletePasswordStage.initOwner(PasswordManagerApp.getMainStage());
        deletePasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = deletePasswordStage;
        stage.showAndWait();
    }

    protected void showSavedLabel() {
        FadeTransition fader = TransitionUtil.createFader(savedLabel);
        SequentialTransition fade = new SequentialTransition(
                savedLabel,
                fader
        );
        savedLabel.setText("Saved!");
        savedLabel.setTextFill(Color.GREEN);
        fade.play();
    }

    protected void disable() {
        detailsPane.setDisable(true);
    }

    protected T getEntryWrapper() {
        return wrapper;
    }

    protected void setEntryWrapper(T wrapper) {
        this.wrapper = wrapper;
    }

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
            Class<?> customTextFieldClass = Class.forName("org.controlsfx.control.textfield.CustomTextField");
            Class<?> customPasswordFieldClass = Class.forName("org.controlsfx.control.textfield.CustomPasswordField");
            Object passwordState = passwordToggler.togglePassword(passwordVbox, getEntryWrapper());
            if(customTextFieldClass.isInstance(passwordState)) {
                CustomTextField passwordShow = (CustomTextField) passwordState;
                Insets insets = new Insets(10, 0, 0, 0);
                VBox.setMargin(passwordShow, insets);
                passwordShow.setEditable(false);
                visiblePasswordField = passwordShow;
                setPasswordIcons();
                setTextFormatters();
                passwordVbox.getChildren().set(1, visiblePasswordField);
            } else if(customPasswordFieldClass.isInstance(passwordState)) {
                CustomPasswordField passwordHide = (CustomPasswordField) passwordState;
                Insets insets = new Insets(10, 0, 0, 0);
                VBox.setMargin(passwordHide, insets);
                passwordHide.setEditable(false);
                hiddenPasswordField = passwordHide;
                setPasswordIcons();
                setTextFormatters();
                passwordVbox.getChildren().set(1, hiddenPasswordField);
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

    @FXML
    protected void cancelDelete() {
        getStage().close();
    }

    @FXML
    protected void confirmDelete() throws ClassNotFoundException {
        System.out.println(getEntryWrapper());
        if(getEntryWrapper() instanceof WebsitePasswordEntryWrapper) {
            WebsitePasswordEntryWrapper websitePasswordEntryWrapper = (WebsitePasswordEntryWrapper) getEntryWrapper();
            WebsitePasswordEntry entry = websitePasswordEntryWrapper.getWebsitePasswordEntry();
            deleteWebsitePasswordEntry(entry);
        } if(getEntryWrapper() instanceof DatabasePasswordEntryWrapper) {
            DatabasePasswordEntryWrapper databasePasswordEntryWrapper = (DatabasePasswordEntryWrapper) getEntryWrapper();
            DatabasePasswordEntry entry = databasePasswordEntryWrapper.getDatabasePasswordEntry();
            deleteDatabasePasswordEntry(entry);
        }
    }

    private void deleteWebsitePasswordEntry(WebsitePasswordEntry entry) throws ClassNotFoundException {
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteSingleEntry(entry, getParentFolder());
        PasswordManagerApp.getPasswordDetailsController().setNoDetailsLoaded();
        PasswordManagerApp.getPasswordHomeController().populateWebsiteEntryPasswords(getParentFolder());
    }

    private void deleteDatabasePasswordEntry(DatabasePasswordEntry entry) throws ClassNotFoundException {
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteSingleEntry(entry, getParentFolder());
        PasswordManagerApp.getPasswordDetailsController().setNoDetailsLoaded();
        PasswordManagerApp.getPasswordHomeController().populateDatabaseEntryPasswords(getParentFolder());
    }

    @FXML
    protected void copyPasswordNameButton() {
        copyToClipboard(passwordNameField.getText());
    }

    @FXML
    protected void copyPasswordToClipboard() {
        if(passwordToggler.getShowPassword()) {
            CustomTextField password = (CustomTextField) passwordVbox.getChildren().get(1);
            copyToClipboard(password.getText());
        } else {
            CustomPasswordField password = (CustomPasswordField) passwordVbox.getChildren().get(1);
            copyToClipboard(password.getText());
        }
    }

    protected static Stage getStage() {
        return stage;
    }

    protected void setParentFolder(PasswordEntryFolder folder) {
        parentFolder = folder;
    }

    protected PasswordEntryFolder getParentFolder() {
        return parentFolder;
    }
}
