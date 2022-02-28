package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Authentication.LoginAuthentication;
import com.jamesd.passwordmanager.Authentication.RegisterUser;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.HashMasterPasswordUtil;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class LoginController {

    @FXML
    private TabPane loginRegisterTabPane;

    @FXML
    private JFXTextField usernameRegisterField;
    @FXML
    private JFXTextField emailRegisterField;
    @FXML
    private JFXPasswordField passwordRegisterField;
    @FXML
    private JFXPasswordField confirmPasswordRegisterField;
    @FXML
    private Label usernameRegisterError;
    @FXML
    private Label emailRegisterError;
    @FXML
    private Label passwordRegisterError;
    @FXML
    private Label confirmPasswordRegisterError;

    @FXML
    private JFXTextField usernameEmailLoginField;
    @FXML
    private JFXPasswordField passwordLoginField;
    @FXML
    private Label loginStatusLabel;
    @FXML
    private ImageView loginLogo;

    private static Stage stage;
    protected static Logger logger = LoggerFactory.getLogger(LoginController.class);

    public LoginController() {

    }

    private void loadUserAddedModal() throws IOException, LoginException {
        Stage userRegisteredStage = new Stage();
        AnchorPane userRegisteredPane = FXMLLoader
                .load(LoginController.class.getResource("/com/jamesd/passwordmanager/views/user-registered.fxml"));
        Scene userRegisteredScene = new Scene(userRegisteredPane);
        userRegisteredStage.setScene(userRegisteredScene);
        userRegisteredStage.setTitle("User Registered");
        userRegisteredStage.initOwner(PasswordManagerApp.getMainStage());
        userRegisteredStage.initModality(Modality.APPLICATION_MODAL);
        stage = userRegisteredStage;
        stage.showAndWait();
        loginRegisterTabPane.getSelectionModel().select(loginRegisterTabPane.getTabs().get(0));
    }

    public void onRegisterClick() throws LoginException, IOException {
        RegisterUser registerUser = new RegisterUser(this);
        if(registerUser.validateFields()) {
            String encryptedMasterPass = HashMasterPasswordUtil.hashPassword(passwordRegisterField.getText());
            MasterSQLQueries.addUserToDb(usernameRegisterField.getText(), emailRegisterField.getText(),
                    encryptedMasterPass);
            logger.info("User " + usernameRegisterField.getText() + " successfully added to database.");
            loadUserAddedModal();
        }
    }

    public void onLoginButtonClick() throws InvalidAlgorithmParameterException, SQLException, LoginException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException,
            BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        LoginAuthentication authentication = new LoginAuthentication(this);

        //TODO: Make this dynamically detect whether user is using E-mail or Username for login.
        if(authentication.login("username")) {
            PasswordManagerApp.setLoggedInUser(authentication.getLoggedInUser());
            logger.info("User " + authentication.getLoggedInUser().getUsername() + " set as current logged in user.");
            redirectToPasswordsHome();
            logger.info("Switched context to PasswordHomeController.");
        }
    }

    public void resetErrorFields() {
        getUsernameRegisterError().setText("");
        getEmailRegisterError().setText("");
        getPasswordRegisterError().setText("");
        getConfirmPasswordRegisterError().setText("");
    }

    public void closeRegisteredMessage() {
        LoginController.getStage().close();
    }

    public void setRegisterIcons() {
        TabPane pane = (TabPane) PasswordManagerApp.getRootLayout().getCenter();
        StackPane stackPane = (StackPane) pane.getTabs()
                .stream()
                .filter(o -> o.getId().equals("registerTab"))
                .collect(Collectors.toList()).get(0)
                .getContent();
        VBox vbox = (VBox) stackPane.getChildren()
                .stream()
                .filter(o -> o.getId().equals("registerVbox"))
                .collect(Collectors.toList()).get(0);
        Label usernameLabel = (Label) vbox.getChildren()
                .stream()
                .filter(o -> o.getId().equals("registerUsername"))
                .collect(Collectors.toList()).get(0);
        Label emailLabel = (Label) vbox.getChildren()
                .stream()
                .filter(o -> o.getId().equals("registerEmail"))
                .collect(Collectors.toList()).get(0);
        Label passwordField = (Label) vbox.getChildren()
                .stream()
                .filter(o -> o.getId().equals("registerPassword"))
                .collect(Collectors.toList()).get(0);
        Label confirmPasswordField = (Label) vbox.getChildren()
                .stream()
                .filter(o -> o.getId().equals("registerConfirmPassword"))
                .collect(Collectors.toList()).get(0);
        Text user = GlyphsDude.createIcon(FontAwesomeIcon.USER, "1.5em");
        Text email = GlyphsDude.createIcon(FontAwesomeIcon.ENVELOPE, "1.5em");
        Text lock1 = GlyphsDude.createIcon(FontAwesomeIcon.LOCK, "1.5em");
        Text lock2 = GlyphsDude.createIcon(FontAwesomeIcon.LOCK, "1.5em");

        usernameLabel.setGraphic(user);
        emailLabel.setGraphic(email);
        passwordField.setGraphic(lock1);
        confirmPasswordField.setGraphic(lock2);
    }

    public void setLoginIcons() {
        TabPane pane = (TabPane) PasswordManagerApp.getRootLayout().getCenter();
        StackPane stackPane = (StackPane) pane.getTabs()
                .stream()
                .filter(o -> o.getId().equals("loginTab"))
                .collect(Collectors.toList()).get(0)
                .getContent();
        VBox vbox = (VBox) stackPane.getChildren()
                .stream()
                .filter(o -> o.getId().equals("loginVbox"))
                .collect(Collectors.toList()).get(0);
        Label usernameEmailLabel = (Label) vbox.getChildren()
                .stream()
                .filter(o -> o.getId().equals("loginTabUsernameEmail"))
                .collect(Collectors.toList()).get(0);
        Label passwordField = (Label) vbox.getChildren()
                .stream()
                .filter(o -> o.getId().equals("loginTabPassword"))
                .collect(Collectors.toList()).get(0);
        Text user = GlyphsDude.createIcon(FontAwesomeIcon.USER, "1.5em");
        Text lock = GlyphsDude.createIcon(FontAwesomeIcon.LOCK, "1.5em");
        
        usernameEmailLabel.setGraphic(user);
        passwordField.setGraphic(lock);
    }

    public void redirectToPasswordsHome() throws IOException, LoginException {
        PasswordManagerApp.loadPasswordHomeView();
    }

    public static Stage getStage() {
        return stage;
    }

    public TextField getUsernameEmailLoginField() {
        return this.usernameEmailLoginField;
    }

    public TextField getPasswordLoginField() {
        return this.passwordLoginField;
    }

    public Label getLoginStatusLabel() {
        return this.loginStatusLabel;
    }

    public void setLoginStatusLabel(Label loginStatusLabel) {
        this.loginStatusLabel = loginStatusLabel;
    }

    public Boolean usernameRegisterIsEmpty() {
        return usernameRegisterField.getText().isEmpty();
    }

    public Boolean emailRegisterIsEmpty() {
        return emailRegisterField.getText().isEmpty();
    }

    public Boolean passwordRegisterIsEmpty() {
        return passwordRegisterField.getText().isEmpty();
    }

    public Boolean confirmPasswordRegisterMatches() {
        return passwordRegisterField.getText().equals(confirmPasswordRegisterField.getText());
    }

    public Label getUsernameRegisterError() {
        return usernameRegisterError;
    }

    public void setUsernameRegisterError(Label usernameRegisterError) {
        this.usernameRegisterError = usernameRegisterError;
    }

    public Label getEmailRegisterError() {
        return emailRegisterError;
    }

    public void setEmailRegisterError(Label emailRegisterError) {
        this.emailRegisterError = emailRegisterError;
    }

    public Label getPasswordRegisterError() {
        return passwordRegisterError;
    }

    public void setPasswordRegisterError(Label passwordRegisterError) {
        this.passwordRegisterError = passwordRegisterError;
    }

    public Label getConfirmPasswordRegisterError() {
        return confirmPasswordRegisterError;
    }

    public void setConfirmPasswordRegisterError(Label confirmPasswordRegisterError) {
        this.confirmPasswordRegisterError = confirmPasswordRegisterError;
    }

    public JFXTextField getUsernameRegisterField() {
        return this.usernameRegisterField;
    }

    public void setUsernameRegisterField(JFXTextField usernameRegisterField) {
        this.usernameRegisterField = usernameRegisterField;
    }

    public JFXTextField getEmailRegisterField() {
        return this.emailRegisterField;
    }

    public void setEmailRegisterField(JFXTextField emailRegisterField) {
        this.emailRegisterField = emailRegisterField;
    }

    public JFXPasswordField getPasswordRegisterField() {
        return this.passwordRegisterField;
    }

    public void setPasswordRegisterField(JFXPasswordField confirmPasswordRegisterField) {
        this.confirmPasswordRegisterField = confirmPasswordRegisterField;
    }

    public JFXPasswordField getConfirmPasswordRegisterField() {
        return this.confirmPasswordRegisterField;
    }

    public void setConfirmPasswordRegisterField(JFXPasswordField confirmPasswordRegisterField) {
        this.confirmPasswordRegisterField = confirmPasswordRegisterField;
    }

    public TabPane getLoginRegisterTabPane() {
        return loginRegisterTabPane;
    }

    public void setLoginRegisterTabPane(TabPane loginRegisterTabPane) {
        this.loginRegisterTabPane = loginRegisterTabPane;
    }

}
