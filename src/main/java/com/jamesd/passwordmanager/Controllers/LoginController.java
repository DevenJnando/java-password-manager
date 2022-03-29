package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Authentication.LoginAuthentication;
import com.jamesd.passwordmanager.Authentication.RegisterUser;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.HashMasterPasswordUtil;
import com.jamesd.passwordmanager.Utils.PasswordCreateUtil;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TabPane loginRegisterTabPane;
    @FXML
    private JFXTextField usernameRegisterField = new JFXTextField();
    @FXML
    private JFXTextField emailRegisterField = new JFXTextField();
    @FXML
    private JFXPasswordField passwordRegisterField = new JFXPasswordField();
    @FXML
    private JFXPasswordField confirmPasswordRegisterField = new JFXPasswordField();
    @FXML
    private Label loginTabUsernameEmail = new Label();
    @FXML
    private Label loginTabPassword = new Label();
    @FXML
    private Label registerUsername = new Label();
    @FXML
    private Label registerEmail = new Label();
    @FXML
    private Label registerPassword = new Label();
    @FXML
    private Label registerConfirmPassword = new Label();
    @FXML
    private Label usernameRegisterError;
    @FXML
    private Label emailRegisterError;
    @FXML
    private Label passwordRegisterError;
    @FXML
    private Label confirmPasswordRegisterError;

    @FXML
    private JFXTextField usernameEmailLoginField = new JFXTextField();
    @FXML
    private JFXPasswordField passwordLoginField = new JFXPasswordField();
    @FXML
    private Label loginStatusLabel;
    @FXML
    private ImageView loginLogo;

    private static Stage stage;
    protected static Logger logger = LoggerFactory.getLogger(LoginController.class);

    public LoginController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setIcons();
        setTextFormatters();
    }

    private void loadUserAddedModal() throws IOException {
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

    public void onRegisterClick() throws IOException {
        RegisterUser registerUser = new RegisterUser(this);
        if(registerUser.validateFields()) {
            String encryptedMasterPass = HashMasterPasswordUtil.hashPassword(passwordRegisterField.getText());
            MasterSQLQueries.addUserToDb(usernameRegisterField.getText(), emailRegisterField.getText(),
                    encryptedMasterPass);
            logger.info("User " + usernameRegisterField.getText() + " successfully added to database.");
            loadUserAddedModal();
        }
    }

    public void onLoginButtonClick() throws GeneralSecurityException, SQLException,
            IOException {
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

    public void setIcons() {
        Text user1 = GlyphsDude.createIcon(FontAwesomeIcon.USER, "1.5em");
        Text user2 = GlyphsDude.createIcon(FontAwesomeIcon.USER, "1.5em");
        Text email = GlyphsDude.createIcon(FontAwesomeIcon.ENVELOPE, "1.5em");
        Text lock1 = GlyphsDude.createIcon(FontAwesomeIcon.LOCK, "1.5em");
        Text lock2 = GlyphsDude.createIcon(FontAwesomeIcon.LOCK, "1.5em");
        Text lock3 = GlyphsDude.createIcon(FontAwesomeIcon.LOCK, "1.5em");
        
        loginTabUsernameEmail.setGraphic(user1);
        loginTabPassword.setGraphic(lock1);
        registerUsername.setGraphic(user2);
        registerEmail.setGraphic(email);
        registerPassword.setGraphic(lock2);
        registerConfirmPassword.setGraphic(lock3);
    }

    public void setTextFormatters() {
        TextFormatter<String> textFormatter1 = PasswordCreateUtil.createTextFormatter(32);
        TextFormatter<String> textFormatter2 = PasswordCreateUtil.createTextFormatter(64);
        TextFormatter<String> textFormatter3 = PasswordCreateUtil.createTextFormatter(64);
        TextFormatter<String> passwordFormatter1 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter2 = PasswordCreateUtil.createTextFormatter(24);
        TextFormatter<String> passwordFormatter3 = PasswordCreateUtil.createTextFormatter(24);
        usernameRegisterField.setTextFormatter(textFormatter1);
        emailRegisterField.setTextFormatter(textFormatter2);
        passwordRegisterField.setTextFormatter(passwordFormatter1);
        confirmPasswordRegisterField.setTextFormatter(passwordFormatter2);

        usernameEmailLoginField.setTextFormatter(textFormatter3);
        passwordLoginField.setTextFormatter(passwordFormatter3);
    }

    public void redirectToPasswordsHome() throws IOException {
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
