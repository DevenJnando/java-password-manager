package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Models.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;

public class PasswordDetailsController {

    @FXML
    private BorderPane detailsPane;
    @FXML
    private VBox passwordVbox;
    @FXML
    private Label savedLabel;
    @FXML
    private JFXTextField passwordNameField;
    @FXML
    private JFXTextField websiteUrlField;
    @FXML
    private JFXTextField displayUsernameField;
    @FXML
    private CustomPasswordField hidePasswordText = new CustomPasswordField();
    @FXML
    private CustomTextField showPasswordText = new CustomTextField();
    @FXML
    private JFXButton copyPasswordNameButton;
    @FXML
    private JFXButton copyWebsiteUrlButton;
    @FXML
    private JFXButton copyUsernameButton;
    @FXML
    private JFXButton copyPasswordButton;
    @FXML
    private JFXButton generateNewPasswordButton;
    @FXML
    private HBox logoHbox;
    @FXML
    private Button savePasswordButton;
    @FXML
    private Button deletePasswordButton;

    private static WebsitePasswordEntryWrapper wrapper = new WebsitePasswordEntryWrapper();
    private boolean showPassword = true;
    private static Stage stage;

    private static Logger logger = LoggerFactory.getLogger(PasswordDetailsController.class);

    public void setIcons() {
        Text copy1 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy2 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy3 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text copy4 = GlyphsDude.createIcon(FontAwesomeIcon.COPY, "1.5em");
        Text save = GlyphsDude.createIcon(FontAwesomeIcon.SAVE, "1.5em");
        Text delete = GlyphsDude.createIcon(FontAwesomeIcon.TRASH, "1.5em");
        copyPasswordNameButton.setGraphic(copy1);
        copyPasswordNameButton.setCursor(Cursor.HAND);
        copyWebsiteUrlButton.setGraphic(copy2);
        copyWebsiteUrlButton.setCursor(Cursor.HAND);
        copyUsernameButton.setGraphic(copy3);
        copyUsernameButton.setCursor(Cursor.HAND);
        copyPasswordButton.setGraphic(copy4);
        copyPasswordButton.setCursor(Cursor.HAND);
        generateNewPasswordButton.setCursor(Cursor.HAND);
        savePasswordButton.setGraphic(save);
        savePasswordButton.setCursor(Cursor.HAND);
        deletePasswordButton.setGraphic(delete);
        deletePasswordButton.setCursor(Cursor.HAND);
    }

    private void loadDeletePasswordModal() throws IOException{
        Stage deletePasswordStage = new Stage();
        FXMLLoader deletePasswordLoader = new FXMLLoader(PasswordHomeController.class
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

    public static Stage getStage() {
        return stage;
    }

    public void setPasswordEntryWrapper(WebsitePasswordEntryWrapper wrapper) {
        PasswordDetailsController.wrapper = wrapper;
    }

    public WebsitePasswordEntryWrapper getPasswordEntryWrapper() {
        return wrapper;
    }

    public void togglePassword(Event event) {
        if(showPassword) {
            List<Node> filteredChildren = passwordVbox.getChildren().stream()
                    .filter(o -> o.getId().equals("hidePasswordText"))
                    .collect(Collectors.toList());
            if(!filteredChildren.isEmpty()) {
                CustomPasswordField toBeRemoved = (CustomPasswordField) filteredChildren.get(0);
                passwordVbox.getChildren().remove(toBeRemoved);
                Insets insets = new Insets(10, 0, 0, 0);
                Text visiblePasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
                CustomTextField passwordShow = new CustomTextField();
                passwordShow.setEditable(true);
                passwordShow.setText(getPasswordEntryWrapper().getWebsitePasswordEntry().getDecryptedPassword());
                passwordShow.setId("showPasswordText");
                passwordShow.setRight(visiblePasswordIcon);
                passwordShow.setOnAction(this::togglePassword);
                passwordShow.setOnMouseClicked(this::togglePassword);
                passwordShow.setOnMousePressed(this::togglePassword);
                VBox.setMargin(passwordShow, insets);
                passwordShow.setCursor(Cursor.HAND);
                passwordVbox.getChildren().add(passwordShow);
                showPasswordText = passwordShow;
                showPassword = false;
            }
        } else {
            List<Node> filteredChildren = passwordVbox.getChildren().stream()
                    .filter(o -> o.getId().equals("showPasswordText"))
                    .collect(Collectors.toList());
            if(!filteredChildren.isEmpty()) {
                CustomTextField toBeRemoved = (CustomTextField) filteredChildren.get(0);
                passwordVbox.getChildren().remove(toBeRemoved);
                Insets insets = new Insets(10, 0, 0, 0);
                Text hiddenPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
                CustomPasswordField passwordHide = new CustomPasswordField();
                passwordHide.setEditable(true);
                passwordHide.setText(getPasswordEntryWrapper().getWebsitePasswordEntry().getDecryptedPassword());
                passwordHide.setId("hidePasswordText");
                passwordHide.setRight(hiddenPasswordIcon);
                passwordHide.setOnAction(this::togglePassword);
                passwordHide.setOnMouseClicked(this::togglePassword);
                passwordHide.setOnMousePressed(this::togglePassword);
                VBox.setMargin(passwordHide, insets);
                passwordHide.setCursor(Cursor.HAND);
                passwordVbox.getChildren().add(passwordHide);
                hidePasswordText = passwordHide;
                showPassword = true;
            }
        }

    }

    public void showSavedLabel() {
        FadeTransition fader = createFader(savedLabel);
        SequentialTransition blinkThenFade = new SequentialTransition(
                savedLabel,
                fader
        );
        savedLabel.setText("Saved!");
        savedLabel.setTextFill(Color.GREEN);
        blinkThenFade.play();
    }

    private FadeTransition createFader(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        return fade;
    }

    public void disable() {
        detailsPane.setDisable(true);
    }

    public void clear() {
        logoHbox.getChildren().clear();
        passwordNameField.clear();
        websiteUrlField.clear();
        displayUsernameField.clear();
        hidePasswordText.clear();
        if(showPassword) {
            List<Node> filteredChildren = passwordVbox.getChildren().stream()
                    .filter(o -> o.getId().equals("hidePasswordText"))
                    .collect(Collectors.toList());
                if(!filteredChildren.isEmpty()) {
                    CustomPasswordField toBeCleared = (CustomPasswordField) filteredChildren.get(0);
                    toBeCleared.clear();
                }
            } else {
            List<Node> filteredChildren = passwordVbox.getChildren().stream()
                    .filter(o -> o.getId().equals("showPasswordText"))
                    .collect(Collectors.toList());
            if (!filteredChildren.isEmpty()) {
                CustomTextField toBeCleared = (CustomTextField) filteredChildren.get(0);
                toBeCleared.clear();
            }
        }
    }

    public void populatePasswordLayout() throws LoginException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException,
            BadPaddingException, InvalidKeyException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            if(getPasswordEntryWrapper()!= null) {
                detailsPane.setDisable(false);
                getPasswordEntryWrapper().getWebsitePasswordEntry().setDecryptedPassword
                        (EncryptDecryptPasswordsUtil.decryptPassword
                                (getPasswordEntryWrapper().getWebsitePasswordEntry().getEncryptedPassword()));
                ImageView logo = new ImageView(getPasswordEntryWrapper().getFavicon().getImage());
                logo.setFitWidth(128);
                logo.setFitHeight(128);
                logoHbox.getChildren().add(logo);
                passwordNameField.setText(getPasswordEntryWrapper().getWebsitePasswordEntry().getPasswordName());
                websiteUrlField.setText(getPasswordEntryWrapper().getWebsitePasswordEntry().getSiteUrl());
                displayUsernameField.setText(getPasswordEntryWrapper().getWebsitePasswordEntry().getPasswordUsername());
                Text hiddenPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
                hidePasswordText.setText(getPasswordEntryWrapper().getWebsitePasswordEntry().getDecryptedPassword());
                hidePasswordText.setRight(hiddenPasswordIcon);
                hidePasswordText.setCursor(Cursor.HAND);
                showPasswordText.setText(getPasswordEntryWrapper().getWebsitePasswordEntry().getDecryptedPassword());
                showPasswordText.setRight(hiddenPasswordIcon);
                showPasswordText.setCursor(Cursor.HAND);
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    public void generateNewPassword() {
        String generatedString = PasswordCreateUtil.generatePassword();
        WebsitePasswordEntryWrapper wrapper = getPasswordEntryWrapper();
        if(passwordIsHidden()) {
            CustomPasswordField password = (CustomPasswordField) passwordVbox.getChildren().get(1);
            password.setText(generatedString);
            wrapper.getWebsitePasswordEntry().setDecryptedPassword(password.getText());
            wrapper.getWebsitePasswordEntry().setDateSet(LocalDate.now().toString());
            setPasswordEntryWrapper(wrapper);
            passwordVbox.getChildren().set(1, password);
        } else {
            CustomTextField password = (CustomTextField) passwordVbox.getChildren().get(1);
            password.setText(generatedString);
            wrapper.getWebsitePasswordEntry().setDecryptedPassword(password.getText());
            wrapper.getWebsitePasswordEntry().setDateSet(LocalDate.now().toString());
            setPasswordEntryWrapper(wrapper);
            passwordVbox.getChildren().set(1, password);
        }
    }

    public void updatePassword() throws GeneralSecurityException, IOException, ClassNotFoundException, InterruptedException {
        Node password = passwordVbox.getChildren().get(1);
        updatePassword(password);
        showSavedLabel();
    }

    public <T> void updatePassword(T password) throws GeneralSecurityException, IOException, ClassNotFoundException {
        WebsitePasswordEntry entry = getPasswordEntryWrapper().getWebsitePasswordEntry();
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
        StoredPassSQLQueries.updatePasswordInDb(entry);
        PasswordHomeController.setLoadedPasswords(null);
        PasswordManagerApp.getPasswordHomeController().populatePasswordList();
    }

    public void deletePassword() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
                loadDeletePasswordModal();
                logger.info("Loaded delete passwords modal.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @FXML
    private void confirmDelete() throws IOException, LoginException {
        WebsitePasswordEntry entry = getPasswordEntryWrapper().getWebsitePasswordEntry();
        System.out.println(entry.getPasswordName());
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteSingleEntry(entry);
        PasswordHomeController.setLoadedPasswords(null);
        PasswordManagerApp.loadPasswordHomeView();
    }

    @FXML
    private void cancelDelete() {
        getStage().close();
    }

    public Boolean passwordIsHidden() {
        Boolean hidden = false;
        List<Node> filteredChildren = passwordVbox.getChildren().stream()
                .filter(o -> o.getId().equals("showPasswordText"))
                .collect(Collectors.toList());
        if(filteredChildren.isEmpty()) {
            hidden = true;
        }
        return hidden;
    }

    public void copyPasswordNameButton() {
        copyToClipboard(passwordNameField.getText());
    }

    public void copyWebsiteUrlButton() {
        copyToClipboard(websiteUrlField.getText());
    }

    public void copyUsernameButton() {
        copyToClipboard(displayUsernameField.getText());
    }

    public void copyPasswordToClipboard() {
        if(passwordIsHidden()) {
            CustomPasswordField password = (CustomPasswordField) passwordVbox.getChildren().get(1);
            copyToClipboard(password.getText());
        } else {
            CustomTextField password = (CustomTextField) passwordVbox.getChildren().get(1);
            copyToClipboard(password.getText());
        }
    }

    public void copyToClipboard(String textToBeCopied) {
        StringSelection selection = new StringSelection(textToBeCopied);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }
}
