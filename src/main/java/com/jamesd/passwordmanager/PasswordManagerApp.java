package com.jamesd.passwordmanager;

import com.jamesd.passwordmanager.Controllers.LoginController;
import com.jamesd.passwordmanager.Controllers.PasswordDetailsController;
import com.jamesd.passwordmanager.Controllers.PasswordHomeController;
import com.jamesd.passwordmanager.Controllers.PreferencesController;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.DAO.PropertiesUtil;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.User;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import com.jfoenix.controls.JFXDrawer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PasswordManagerApp extends Application {

    //TODO:
    // Password names are a must - DONE!!!!!!
    // Reorganise displayed passwords into tables - website name, url, username, date created - DONE!!!!!!!
    // Password update reminder (compare date created to current date) - DONE!!!!!!!!!
    // Reorganise password details screen, make all fields copyable too - DONE!!!!!!!!
    // Allow for user inputted notes
    // Get logos added - DONE!!!!!!
    // Logout functionality - DONE!!!!!!
    // Delete Passwords - DONE!!!!!
    // Improve frontend:
    //  Merge home and details screens into a single screen - DONE!!!!!!
    // User preferences - reminder timings, change master password, two-factor settings
    // Check for insecurities or breaches
    // Consider having a more organised hierarchy e.g. users > root password folder > work passwords > Google
    // Consider allowing storage of other kinds of credential e.g. certificates, credit cards, passports, sha keys,
    // sensitive documents etc.
    // Implement query timeouts
    // Allow dynamic login for either email or username

    private static Stage mainStage;
    private static BorderPane rootLayout;
    private static User loggedInUser;

    private static PasswordHomeController passwordHomeController;
    private static PasswordDetailsController passwordDetailsController;
    private static PreferencesController preferencesController;

    private static Logger logger = LoggerFactory.getLogger(PasswordManagerApp.class);

    @Override
    public void start(Stage stage) throws IOException {
        PropertiesUtil.initialise();
        passwordHomeController = new PasswordHomeController();
        passwordDetailsController = new PasswordDetailsController();
        preferencesController = new PreferencesController();
        stage.setTitle("DevenJnando Password Manager");
        mainStage = stage;

        // Close connections to CosmosDB clients upon close
        mainStage.setOnCloseRequest(e -> {
            if(StoredPassSQLQueries.getStoredPassClient() != null)
                StoredPassSQLQueries.close();
            MasterSQLQueries.close();
            cleanUp(new File("src/main/resources/com/jamesd/passwordmanager/icons/favicons/"));
        });

        initRootLayout();
    }

    private void cleanUp(File dir) {
        File fileList[] = dir.listFiles();
        for(File file: fileList) {
            if(file.isFile()) {
                file.delete();
            } else {
                cleanUp(file);
            }
        }
    }

    public static void initRootLayout() throws IOException {
        MasterSQLQueries.initialiseUsers();
        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(PasswordManagerApp.class
                .getResource("/com/jamesd/passwordmanager/views/login-register-view.fxml"));
        rootLayout = (BorderPane) loader.load();
        LoginController loginController = loader.getController();
        loginController.setLoginIcons();
        loginController.setRegisterIcons();

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);

        mainStage.setScene(scene);
        mainStage.widthProperty().addListener((obj, oldValue, newValue) -> {
            logger.info("New width value: " + newValue);
        });
        mainStage.heightProperty().addListener((obj, oldValue, newValue) -> {
            logger.info("New height value: " + newValue);
        });
        mainStage.setMinHeight(875);
        mainStage.setMinWidth(1085);
        mainStage.show();
    }

    public static void loadPasswordHomeView() throws IOException {
        FXMLLoader passwordHomeLoader = new FXMLLoader(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/users-passwords.fxml"));
        FXMLLoader passwordDetailsLoader = new FXMLLoader(PasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/password-details.fxml"));
        BorderPane homePane = passwordHomeLoader.load();
        BorderPane detailsPane = passwordDetailsLoader.load();
        passwordHomeController = passwordHomeLoader.getController();
        passwordDetailsController = passwordDetailsLoader.getController();
        homePane.setLeft(FXMLLoader.load(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml")));
        homePane.setCenter(detailsPane);
        rootLayout.setTop(null);
        rootLayout.setCenter(homePane);
    }

    public static void loadPasswordDetailsView(WebsitePasswordEntryWrapper passwordEntry) throws IOException,
            InvalidAlgorithmParameterException, LoginException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        passwordDetailsController.clear();
        passwordDetailsController.setPasswordEntryWrapper(passwordEntry);
        passwordDetailsController.setIcons();
        passwordDetailsController.populatePasswordLayout();
    }

    public static void loadPreferencesView() throws IOException{
        FXMLLoader preferencesLoader = new FXMLLoader(PreferencesController.class
                .getResource("/com/jamesd/passwordmanager/views/preferences.fxml"));
        BorderPane preferencesPane = preferencesLoader.load();
        preferencesController = preferencesLoader.getController();
        preferencesPane.setLeft(FXMLLoader.load(PreferencesController.class
                .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml")));
        rootLayout.setTop(null);
        rootLayout.setCenter(preferencesPane);
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        PasswordManagerApp.loggedInUser = loggedInUser;
    }

    public static PasswordHomeController getPasswordHomeController() { return passwordHomeController; }

    public static PasswordDetailsController getPasswordDetailsController() {
        return passwordDetailsController;
    }

    public static PreferencesController getPreferencesController() {
        return preferencesController;
    }

    public static void main(String[] args) {
        launch();
    }
}