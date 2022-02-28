package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Models.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private JFXButton backButton;
    @FXML
    private CustomPasswordField hidePasswordText;
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
    private Button savePasswordButton;
    @FXML
    private Button deletePasswordButton;
    @FXML
    private JFXDrawer menuDrawer;
    @FXML
    private VBox menuContent;

    private static WebsitePasswordEntry entry = new WebsitePasswordEntry();
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

    public void onHamburgerClick() {
        if(getMenuDrawer().isOpened()){
            getMenuDrawer().close();
            logger.info("Closed menu drawer.");
        } else {
            getMenuDrawer().open();
            logger.info("Opened menu drawer");
        }
    }

    public JFXDrawer getMenuDrawer() {
        return this.menuDrawer;
    }

    public static Stage getStage() {
        return stage;
    }

    public VBox getMenuContent() {
        return this.menuContent;
    }

    public void setPasswordEntry(WebsitePasswordEntry entry) {
        PasswordDetailsController.entry = entry;
    }

    public WebsitePasswordEntry getPasswordEntry() {
        return entry;
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
                passwordShow.setText(getPasswordEntry().getDecryptedPassword());
                passwordShow.setId("showPasswordText");
                passwordShow.setRight(visiblePasswordIcon);
                passwordShow.setOnAction(this::togglePassword);
                passwordShow.setOnMouseClicked(this::togglePassword);
                passwordShow.setOnMousePressed(this::togglePassword);
                VBox.setMargin(passwordShow, insets);
                passwordShow.setCursor(Cursor.HAND);
                passwordVbox.getChildren().add(passwordShow);
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
                passwordHide.setText(getPasswordEntry().getDecryptedPassword());
                passwordHide.setId("hidePasswordText");
                passwordHide.setRight(hiddenPasswordIcon);
                passwordHide.setOnAction(this::togglePassword);
                passwordHide.setOnMouseClicked(this::togglePassword);
                passwordHide.setOnMousePressed(this::togglePassword);
                VBox.setMargin(passwordHide, insets);
                passwordHide.setCursor(Cursor.HAND);
                passwordVbox.getChildren().add(passwordHide);
                showPassword = true;
            }
        }

    }

    public void showSavedLabel() {
        savedLabel.setText("Saved!");
        savedLabel.setTextFill(Color.GREEN);
    }

    public void populatePasswordLayout() throws LoginException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException,
            BadPaddingException, InvalidKeyException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Text backIcon = GlyphsDude.createIcon(FontAwesomeIcon.BACKWARD);
            backButton.setGraphic(backIcon);
            if(getPasswordEntry()!= null) {
                getPasswordEntry().setDecryptedPassword(EncryptDecryptPasswordsUtil.decryptPassword(getPasswordEntry().getEncryptedPassword()));
                passwordNameField.setText(getPasswordEntry().getPasswordName());
                websiteUrlField.setText(getPasswordEntry().getSiteUrl());
                displayUsernameField.setText(getPasswordEntry().getPasswordUsername());
                Text hiddenPasswordIcon = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
                hidePasswordText.setText(getPasswordEntry().getDecryptedPassword());
                hidePasswordText.setRight(hiddenPasswordIcon);
                hidePasswordText.setCursor(Cursor.HAND);
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    public void generateNewPassword() {
        String generatedString = PasswordCreateUtil.generatePassword();
        WebsitePasswordEntry entry = getPasswordEntry();
        if(passwordIsHidden()) {
            CustomPasswordField password = (CustomPasswordField) passwordVbox.getChildren().get(1);
            password.setText(generatedString);
            entry.setDecryptedPassword(password.getText());
            entry.setDateSet(LocalDate.now().toString());
            setPasswordEntry(entry);
            passwordVbox.getChildren().set(1, password);
        } else {
            CustomTextField password = (CustomTextField) passwordVbox.getChildren().get(1);
            password.setText(generatedString);
            entry.setDecryptedPassword(password.getText());
            entry.setDateSet(LocalDate.now().toString());
            setPasswordEntry(entry);
            passwordVbox.getChildren().set(1, password);
        }
    }

    public void updatePassword() throws GeneralSecurityException, IOException, ClassNotFoundException, InterruptedException {
        Node password = passwordVbox.getChildren().get(1);
        updatePassword(password);
        showSavedLabel();
    }

    public <T> void updatePassword(T password) throws GeneralSecurityException, IOException, ClassNotFoundException {
        WebsitePasswordEntry entry = getPasswordEntry();
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
        WebsitePasswordEntry entry = getPasswordEntry();
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

    public void backToPasswordsList() throws LoginException, IOException {
        PasswordManagerApp.loadPasswordHomeView();
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
