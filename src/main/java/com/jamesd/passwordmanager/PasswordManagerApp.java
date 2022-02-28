package com.jamesd.passwordmanager;

import com.jamesd.passwordmanager.Controllers.LoginController;
import com.jamesd.passwordmanager.Controllers.PasswordDetailsController;
import com.jamesd.passwordmanager.Controllers.PasswordHomeController;
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
    // User preferences - reminder timings, change master password, two-factor settings
    // Check for insecurities or breaches
    // Consider having a more organised hierarchy e.g. users > root password folder > work passwords > Google
    // Consider allowing storage of other kinds of credential e.g. certificates, credit cards, passports, sha keys,
    // sensitive documents etc.
    // Implement query timeouts

    private static Stage mainStage;
    private static BorderPane rootLayout;
    private static User loggedInUser;

    private static PasswordHomeController passwordHomeController;
    private static PasswordDetailsController passwordDetailsController;

    @Override
    public void start(Stage stage) throws IOException {
        PropertiesUtil.initialise();
        passwordHomeController = new PasswordHomeController();
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
        mainStage.show();
    }

    public static void loadPasswordHomeView() throws IOException, LoginException {
        FXMLLoader passwordHomeLoader = new FXMLLoader(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/users-passwords.fxml"));
        BorderPane borderPane = (BorderPane) passwordHomeLoader.load();
        passwordHomeController = passwordHomeLoader.getController();
        JFXDrawer menuContent = (JFXDrawer) borderPane.getLeft();
        menuContent.setSidePane((VBox) FXMLLoader.load(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml")));
        menuContent.close();
        rootLayout.setTop(null);
        rootLayout.setCenter(borderPane);
    }

    public static void loadPasswordDetailsView(WebsitePasswordEntryWrapper passwordEntry) throws IOException,
            InvalidAlgorithmParameterException, LoginException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        FXMLLoader passwordDetailsLoader = new FXMLLoader(PasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/password-details.fxml"));
        BorderPane borderPane = (BorderPane) passwordDetailsLoader.load();
        passwordDetailsController = passwordDetailsLoader.getController();
        passwordDetailsController.setPasswordEntryWrapper(passwordEntry);
        passwordDetailsController.setIcons();
        passwordDetailsController.populatePasswordLayout();
        JFXDrawer menuContent = (JFXDrawer) borderPane.getLeft();
        menuContent.setSidePane((VBox) FXMLLoader.load(PasswordDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml")));
        menuContent.close();
        rootLayout.setCenter(borderPane);
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

    public static void main(String[] args) {
        launch();
    }
}