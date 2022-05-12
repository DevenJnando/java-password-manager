package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Authentication.LoginAuthentication;
import com.jamesd.passwordmanager.Authentication.RegisterUser;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.DAO.StorageAccountManager;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.Passwords.StoredPassDbKey;
import com.jamesd.passwordmanager.Models.Users.RecognisedUserDevice;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.*;
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

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private JFXTextField phoneNumberRegisterField = new JFXTextField();
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

    private TwoFactorAuthenticationController twoFactorAuthenticationController;
    private StoredPassDbKey storedPassDbKey;

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
            List<HashMap<String, String>> recognisedDevices = DeviceIdentifierUtil.addDeviceToRecognisedList();
            MasterSQLQueries.addUserToDb(usernameRegisterField.getText(), emailRegisterField.getText(),
                    phoneNumberRegisterField.getText(), encryptedMasterPass, recognisedDevices);
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
    @FXML
    public void onLoginButtonClick() throws GeneralSecurityException, IOException, SQLException {
        LoginAuthentication authentication = new LoginAuthentication(this);

        //TODO: Make this dynamically detect whether user is using E-mail or Username for login.
        if(authentication.login("username")) {
            PasswordManagerApp.setLoggedInUser(authentication.getUser());
            logger.info("User " + authentication.getUser().getUsername() + " set as current logged in user.");
            if(PasswordManagerApp.getLoggedInUser().isTwoFactorEnabled()) {
                String deviceMacAddress = DeviceIdentifierUtil.getMacAddress();
                List<HashMap<String, String>> recognisedDeviceData = PasswordManagerApp.getLoggedInUser().getRecognisedDevices();
                List<RecognisedUserDevice> recognisedUserDevices = getRecognisedDevicesList(recognisedDeviceData);
                if(recognisedUserDevices.size() < recognisedDeviceData.size()) {
                    List<HashMap<String, String>> updatedRecognisedDevices = updatedRecognisedDevices(recognisedUserDevices);
                    PasswordManagerApp.getLoggedInUser().setRecognisedDevices(updatedRecognisedDevices);
                    MasterSQLQueries.updateUserInDb(PasswordManagerApp.getLoggedInUser());
                }
                handleTwoFactorAuthentication(currentDeviceIsRecognised(recognisedUserDevices, deviceMacAddress),
                        recognisedUserDevices, deviceMacAddress);
            } else {
                completeLogin();
            }
        }
    }

    /**
     * Method which obtains the key to unlock the database containing user passwords. Only called if the user's login
     * attempt is successful.
     * @param storedPassKeys List of keys for the user's password database (should only ever contain one key)
     * @param loginId Either the User's username or email address
     * @param loginMethod The method which the user has chosen to login using (username, or email address)
     * @throws GeneralSecurityException Throws a GeneralSecurityException if the decryption key is incorrect, or if
     * the master password decryption process fails
     * @throws IOException Throws an IO exception if the response from CosmosDB cannot be read
     * @throws SQLException Throws an SQLException if the CosmosDB query has an incorrect formatting/syntax
     */
    private void unlockStoredPassDb(List<StoredPassDbKey> storedPassKeys, String loginId, String loginMethod)
            throws GeneralSecurityException,
            IOException,
            SQLException {
        for(StoredPassDbKey entry : storedPassKeys) {
            EncryptDecryptPasswordsUtil.initialise(entry.getDecryptionKey());
            entry.decryptMasterPassword();
            storedPassDbKey = entry;
            if(loginMethod.equals("username")) {
                StoredPassSQLQueries.initialiseWithUsername(entry.getDecryptedPassword(), loginId);
            }
            else if(loginMethod.equals("email")) {
                StoredPassSQLQueries.initialiseWithEmail(entry.getDecryptedPassword(), loginId);
            }
            else {
                throw new LoginException("Login method " + loginMethod + " not known. Could not initialise password database.");
            }
        }
    }

    /**
     * Returns a list of all recognised devices for this current user
     * @param recognisedDeviceData List of HashMaps containing all recognised devices
     * @return List of RecognisedUserDevice objects
     */
    private List<RecognisedUserDevice> getRecognisedDevicesList(List<HashMap<String, String>> recognisedDeviceData ) {
        List<RecognisedUserDevice> recognisedUserDevices = new ArrayList<>();
        recognisedDeviceData.forEach(o -> {
            RecognisedUserDevice recognisedUserDevice = new RecognisedUserDevice(o.get("macAddress"), o.get("dateAdded"));
            if(Integer.parseInt(recognisedUserDevice.getDaysInDatabase()) < 30) {
                recognisedUserDevices.add(recognisedUserDevice);
            }
        });
        return recognisedUserDevices;
    }

    /**
     * Returns a boolean which states whether the current device this current user is working on is a recognised device
     * @param recognisedUserDevices List of RecognisedUserDevice objects
     * @param deviceMacAddress MAC address of the current device being worked on
     * @return True if the current device is a recognised one, else false
     */
    private boolean currentDeviceIsRecognised(List<RecognisedUserDevice> recognisedUserDevices, String deviceMacAddress) {
        boolean match = false;
        for(RecognisedUserDevice device : recognisedUserDevices) {
            if(device.getMacAddress().contentEquals(deviceMacAddress)) {
                match = true;
            }
        }
        return match;
    }

    /**
     * Removes any devices which have been stored as "recognised" for longer than 30 days, and updates this in the
     * database
     * @param recognisedUserDevices List of RecognisedUserDevice objects
     * @return List of updated HashMaps containing the latest recognised device data
     */
    private List<HashMap<String, String>> updatedRecognisedDevices(List<RecognisedUserDevice> recognisedUserDevices) {
        List<HashMap<String, String>> updatedRecognisedDevices = new ArrayList<>();
        for(RecognisedUserDevice device : recognisedUserDevices) {
            HashMap<String, String> deviceData = new HashMap<>();
            deviceData.put("macAddress", device.getMacAddress());
            deviceData.put("dateAdded", device.getDateAdded());
            deviceData.put("daysInDatabase", device.getDaysInDatabase());
            updatedRecognisedDevices.add(deviceData);
        }
        return updatedRecognisedDevices;
    }

    private void completeLogin() throws SQLException, GeneralSecurityException, IOException {
        MasterSQLQueries.initialiseStoredPassKey();
        List<StoredPassDbKey> keys = MasterSQLQueries.queryEncryptedStoredPassKey();
        unlockStoredPassDb(keys, getUsernameEmailLoginField().getText(),"username");
        StorageAccountManager.connect(EncryptDecryptPasswordsUtil.decryptPassword(storedPassDbKey.getEncryptedStorage()));
        redirectToPasswordsHome();
        logger.info("Switched context to PasswordHomeController.");
    }

    /**
     * Handler for two-factor authentication. Redirects the user to the home menu if two-factor authentication is successful
     * and bombs out otherwise giving the user an error message.
     * @param deviceIsRecognised Flag which states if the device is recognised or not. If recognised, there is no need
     *                           for two-factor authentication
     * @throws IOException Throws IOException if either the password home menu, or the two-factor authentication modal
     * cannot be loaded.
     */
    private void handleTwoFactorAuthentication(boolean deviceIsRecognised, List<RecognisedUserDevice> recognisedUserDevices,
                                                String deviceMacAddress) throws IOException, SQLException, GeneralSecurityException {
        if(deviceIsRecognised) {
            completeLogin();
        } else {
            loadTwoFactorModal();
            if(this.twoFactorAuthenticationController.isVerified()) {
                loginStatusLabel.setText("Two-factor authentication verified. Logging you in...");
                recognisedUserDevices.add(new RecognisedUserDevice(deviceMacAddress, LocalDate.now().toString()));
                List<HashMap<String, String>> updatedRecognisedDevices = updatedRecognisedDevices(recognisedUserDevices);
                PasswordManagerApp.getLoggedInUser().setRecognisedDevices(updatedRecognisedDevices);
                MasterSQLQueries.updateUserInDb(PasswordManagerApp.getLoggedInUser());
                completeLogin();
            } else {
                PasswordManagerApp.setLoggedInUser(null);
                getLoginStatusLabel().setText("Two factor authentication failed...please try again.");
                logger.info("User could not be verified. Logged in user cleared.");
            }
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

    public void loadTwoFactorModal() throws IOException {
        Stage twoFactorAuthStage = new Stage();
        FXMLLoader twoFactorAuthLoader = new FXMLLoader(TwoFactorAuthenticationController.class
                .getResource("/com/jamesd/passwordmanager/views/two-factor-auth-view.fxml"));
        AnchorPane twoFactorAuthAnchorPane = twoFactorAuthLoader.load();
        this.twoFactorAuthenticationController = twoFactorAuthLoader.getController();
        Scene twoFactorAuthScene = new Scene(twoFactorAuthAnchorPane);
        twoFactorAuthStage.setScene(twoFactorAuthScene);
        twoFactorAuthStage.setTitle("Two Factor Authentication");
        twoFactorAuthStage.initOwner(PasswordManagerApp.getMainStage());
        twoFactorAuthStage.initModality(Modality.APPLICATION_MODAL);
        stage = twoFactorAuthStage;
        stage.showAndWait();
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
