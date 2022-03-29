package com.jamesd.passwordmanager;

import com.jamesd.passwordmanager.Controllers.BaseDetailsController;
import com.jamesd.passwordmanager.Controllers.PasswordHomeController;
import com.jamesd.passwordmanager.Controllers.PreferencesController;
import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.Utils.PropertiesUtil;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.Users.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
    // Abstract add/modification/deletion of passwords for more generic code - IN PROGRESS...
    // User preferences - reminder timings, change master password, two-factor settings - IN PROGRESS...
    // Check for insecurities or breaches
    // Consider having a more organised hierarchy e.g. users > root password folder > work passwords > Google - IN PROGRESS...
    // Consider allowing storage of other kinds of credential e.g. database passwords, credit cards, passports,
    // sensitive documents etc. - IN PROGRESS...
    // Implement query timeouts
    // Allow dynamic login for either email or username

    private static Stage mainStage;
    private static BorderPane rootLayout;
    private static User loggedInUser;

    private static PasswordHomeController passwordHomeController;
    private static BaseDetailsController passwordDetailsController;
    private static PreferencesController preferencesController;

    private static Logger logger = LoggerFactory.getLogger(PasswordManagerApp.class);

    @Override
    public void start(Stage stage) throws IOException {
        PropertiesUtil.initialise();
        passwordHomeController = new PasswordHomeController();
        passwordDetailsController = new BaseDetailsController();
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
        FXMLLoader passwordDetailsLoader = new FXMLLoader(BaseDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/base-details-view.fxml"));
        FXMLLoader sideBarLoader = new FXMLLoader(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml"));
        BorderPane homePane = passwordHomeLoader.load();
        VBox detailsVbox = passwordDetailsLoader.load();
        passwordHomeController = passwordHomeLoader.getController();
        passwordDetailsController = passwordDetailsLoader.getController();
        homePane.setLeft(sideBarLoader.load());
        homePane.setCenter(detailsVbox);
        rootLayout.setTop(null);
        rootLayout.setCenter(homePane);
    }

    public static void loadPreferencesView() throws IOException{
        FXMLLoader preferencesLoader = new FXMLLoader(PreferencesController.class
                .getResource("/com/jamesd/passwordmanager/views/preferences.fxml"));
        FXMLLoader sideBarLoader = new FXMLLoader(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml"));
        BorderPane preferencesPane = preferencesLoader.load();
        preferencesController = preferencesLoader.getController();
        preferencesPane.setLeft(sideBarLoader.load());
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

    public static BaseDetailsController getPasswordDetailsController() {
        return passwordDetailsController;
    }

    public static PreferencesController getPreferencesController() {
        return preferencesController;
    }

    public static void main(String[] args) {
        launch();
    }
}