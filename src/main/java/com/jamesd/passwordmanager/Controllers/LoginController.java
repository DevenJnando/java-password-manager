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

/**
 * Controller responsible for performing login functions
 */
public class LoginController implements Initializable {

    /**
     * FXML fields
     */
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

    /**
     * Default constructor
     */
    public LoginController() {

    }

    /**
     * Initialize method which sets the icons and text formatters for all input fields
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setIcons();
        setTextFormatters();
    }

    /**
     * Loads a modal which confirms that a new user has been successfully added to the master database
     * @throws IOException Throws IOException if the "user registered" modal cannot be loaded
     */
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

    /**
     * If registration validation passes, the user given password is one-way encrypted and the new User is added to the
     * master database. The loadUserAddedModal method is then called as confirmation to the user
     * @throws IOException Throws IOException if the "user registered" modal cannot be loaded
     */
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

    /**
     * If login validation passes, the user is set as the currently logged-in user and is redirected to the application
     * homepage
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the decryption key is incorrect, or if
     * the master password decryption process fails
     * @throws IOException Throws an IO exception if the response from CosmosDB cannot be read
     * @throws SQLException Throws an SQLException if the CosmosDB query has an incorrect formatting/syntax
     */
    public void onLoginButtonClick() throws GeneralSecurityException, IOException, SQLException {
        LoginAuthentication authentication = new LoginAuthentication(this);

        //TODO: Make this dynamically detect whether user is using E-mail or Username for login.
        if(authentication.login("username")) {
            PasswordManagerApp.setLoggedInUser(authentication.getLoggedInUser());
            logger.info("User " + authentication.getLoggedInUser().getUsername() + " set as current logged in user.");
            redirectToPasswordsHome();
            logger.info("Switched context to PasswordHomeController.");
        }
    }

    /**
     * Resets all error fields to display nothing
     */
    public void resetErrorFields() {
        getUsernameRegisterError().setText("");
        getEmailRegisterError().setText("");
        getPasswordRegisterError().setText("");
        getConfirmPasswordRegisterError().setText("");
    }

    /**
     * Closes the "user registered" modal
     */
    public void closeRegisteredMessage() {
        LoginController.getStage().close();
    }

    /**
     * Sets all icons for each input field in both the login and register tabs
     */
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

    /**
     * Sets all text formatters for all input fields for both the login and register tabs
     */
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

    /**
     * Upon successful login, this method is called to load the password homepage and redirect the user there
     * @throws IOException Throws IOException if the password home view cannot be loaded
     */
    public void redirectToPasswordsHome() throws IOException {
        PasswordManagerApp.loadPasswordHomeView();
    }

    /**
     * Retrieves the Stage object containing the "user registered" modal
     * @return Stage object
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Retrieves the username/email input field in the login tab
     * @return TextField for the user's username/email
     */
    public TextField getUsernameEmailLoginField() {
        return this.usernameEmailLoginField;
    }

    /**
     * Retrieves the password input field
     * @return TextField containing the user's password
     */
    public TextField getPasswordLoginField() {
        return this.passwordLoginField;
    }

    /**
     * Retrieves the login status Label which should either display nothing, a login error message,
     * or a login success message
     * @return Label displaying login status
     */
    public Label getLoginStatusLabel() {
        return this.loginStatusLabel;
    }

    /**
     * Sets the login status Label which should either display nothing, a login error message,
     * or a login success message
     * @param loginStatusLabel Label displaying login status
     */
    public void setLoginStatusLabel(Label loginStatusLabel) {
        this.loginStatusLabel = loginStatusLabel;
    }

    /**
     * Retrieves the flag which states if the username field is empty in the register tab
     * @return Boolean true if username is empty, else false
     */
    public Boolean usernameRegisterIsEmpty() {
        return usernameRegisterField.getText().isEmpty();
    }

    /**
     * Retrieves the flag which states if the email field is empty in the register tab
     * @return Boolean true if email is empty, else false
     */
    public Boolean emailRegisterIsEmpty() {
        return emailRegisterField.getText().isEmpty();
    }

    /**
     * Retrieves the flag which states if the password field is empty in the register tab
     * @return Boolean true if password is empty, else false
     */
    public Boolean passwordRegisterIsEmpty() {
        return passwordRegisterField.getText().isEmpty();
    }

    /**
     * Retrieves the flag which states if the password and confirm password fields match
     * @return Boolean true if password field matches confirm password field, else false
     */
    public Boolean confirmPasswordRegisterMatches() {
        return passwordRegisterField.getText().equals(confirmPasswordRegisterField.getText());
    }

    /**
     * Retrieves the Label which displays a missing username error message
     * @return Label displaying username empty
     */
    public Label getUsernameRegisterError() {
        return usernameRegisterError;
    }

    /**
     * Sets the Label which displays a missing username error message
     * @param usernameRegisterError Label displaying username empty
     */
    public void setUsernameRegisterError(Label usernameRegisterError) {
        this.usernameRegisterError = usernameRegisterError;
    }
    /**
     * Retrieves the Label which displays a missing email error message
     * @return Label displaying email empty
     */
    public Label getEmailRegisterError() {
        return emailRegisterError;
    }

    /**
     * Sets the Label which displays a missing email error message
     * @param emailRegisterError Label displaying email empty
     */
    public void setEmailRegisterError(Label emailRegisterError) {
        this.emailRegisterError = emailRegisterError;
    }

    /**
     * Retrieves the Label which displays a missing password error message
     * @return Label displaying password empty
     */
    public Label getPasswordRegisterError() {
        return passwordRegisterError;
    }

    /**
     * Sets the Label which displays a missing password error message
     * @param passwordRegisterError Label displaying password empty
     */
    public void setPasswordRegisterError(Label passwordRegisterError) {
        this.passwordRegisterError = passwordRegisterError;
    }

    /**
     * Retrieves the Label which displays a password mismatch error message
     * @return Label displaying password mismatch
     */
    public Label getConfirmPasswordRegisterError() {
        return confirmPasswordRegisterError;
    }

    /**
     * Sets the Label which displays a password mismatch error message
     * @param confirmPasswordRegisterError  Label displaying password mismatch
     */
    public void setConfirmPasswordRegisterError(Label confirmPasswordRegisterError) {
        this.confirmPasswordRegisterError = confirmPasswordRegisterError;
    }

    /**
     * Retrieves the username input field in the register tab
     * @return JFXTextField input field for username
     */
    public JFXTextField getUsernameRegisterField() {
        return this.usernameRegisterField;
    }

    /**
     * Sets the username input field in the register tab
     * @param usernameRegisterField JFXTextField input field for username
     */
    public void setUsernameRegisterField(JFXTextField usernameRegisterField) {
        this.usernameRegisterField = usernameRegisterField;
    }

    /**
     * Retrieves the email input field in the register tab
     * @return JFXTextField input field for email
     */
    public JFXTextField getEmailRegisterField() {
        return this.emailRegisterField;
    }

    /**
     * Sets the email input field in the register tab
     * @param emailRegisterField JFXTextField input field for email
     */
    public void setEmailRegisterField(JFXTextField emailRegisterField) {
        this.emailRegisterField = emailRegisterField;
    }

    /**
     * Retrieves the password field in the register tab
     * @return JFXPasswordField input field for password
     */
    public JFXPasswordField getPasswordRegisterField() {
        return this.passwordRegisterField;
    }

    /**
     * Sets the password field in the register tab
     * @param confirmPasswordRegisterField JFXPasswordField input field for password
     */
    public void setPasswordRegisterField(JFXPasswordField confirmPasswordRegisterField) {
        this.confirmPasswordRegisterField = confirmPasswordRegisterField;
    }

    /**
     * Retrieves the confirm password field in the register tab
     * @return JFXPasswordField input field for confirm password
     */
    public JFXPasswordField getConfirmPasswordRegisterField() {
        return this.confirmPasswordRegisterField;
    }

    /**
     * Sets the confirm password field in the regster tab
     * @param confirmPasswordRegisterField JFXPasswordField input field for confirm password
     */
    public void setConfirmPasswordRegisterField(JFXPasswordField confirmPasswordRegisterField) {
        this.confirmPasswordRegisterField = confirmPasswordRegisterField;
    }

    /**
     * Retrieves the TabPane object containing both the login and register tabs
     * @return TabPane containing register and login tabs
     */
    public TabPane getLoginRegisterTabPane() {
        return loginRegisterTabPane;
    }

    /**
     * Sets the TabPane object containing both the login and register tabs
     * @param loginRegisterTabPane TabPane containing register and login tabs
     */
    public void setLoginRegisterTabPane(TabPane loginRegisterTabPane) {
        this.loginRegisterTabPane = loginRegisterTabPane;
    }
}
