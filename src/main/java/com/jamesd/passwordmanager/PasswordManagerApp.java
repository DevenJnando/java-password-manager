package com.jamesd.passwordmanager;

import com.jamesd.passwordmanager.Controllers.BaseDetailsController;
import com.jamesd.passwordmanager.Controllers.BreachCheckController;
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

/**
 * Entry point for the application
 */
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
    // #################################################################################################################
    // Improve frontend:
    //  Merge home and details screens into a single screen - DONE!!!!!!
    //  Fix that damn sidebar!!!!
    //  The sidebar should really have its own controller instead of being nastily coupled to the PasswordHomeController
    // #################################################################################################################
    // Abstract add/modification/deletion of passwords for more generic code - DONE!!!!!!
    // User preferences - reminder timings, change master password, two-factor settings - DONE!!!!!!
    // Check for insecurities or breaches - DONE!!!!!!!
    // Consider having a more organised hierarchy e.g. users > root password folder > work passwords > Google - DONE!!!!!!!
    // Consider allowing storage of other kinds of credential e.g. database passwords, credit cards,
    // sensitive documents etc. - DONE!!!!!
    // Implement query timeouts - In progress...
    // Allow dynamic login for either email or username

    /**
     * Main Stage object
     */
    private static Stage mainStage;
    /**
     * Borderpane containing the initial layout
     */
    private static BorderPane rootLayout;
    /**
     * Currently logged-in user
     */
    private static User loggedInUser;

    private static PasswordHomeController passwordHomeController;
    private static BaseDetailsController passwordDetailsController;
    private static PreferencesController preferencesController;
    private static BreachCheckController breachCheckController;

    private static Logger logger = LoggerFactory.getLogger(PasswordManagerApp.class);

    /**
     * Start method to the application. Initialises database properties, controllers and the initial BorderPane layout
     * @param stage Stage to populate
     * @throws IOException Throws IOException if the "login" view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        PropertiesUtil.initialise();
        passwordHomeController = new PasswordHomeController();
        passwordDetailsController = new BaseDetailsController();
        preferencesController = new PreferencesController();
        breachCheckController = new BreachCheckController();
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

    /**
     * Cleans up all loose files and logos which need removed after application is closed
     * @param dir
     */
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

    /**
     * Loads the "login" screen and sets listeners on the size of the mainStage field. Also adds a minimum height and
     * width property to the mainStage field
     * @throws IOException Throws IOException if "login" view cannot be loaded
     */
    public static void initRootLayout() throws IOException {
        MasterSQLQueries.initialiseUsers();
        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(PasswordManagerApp.class
                .getResource("/com/jamesd/passwordmanager/views/login-register-view.fxml"));
        rootLayout = loader.load();

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

    /**
     * Loads the "Password home" view and switches context to the PasswordHomeController
     * @throws IOException Throws IOException if the "Password home" cannot be loaded
     */
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

    /**
     * Loads the "user preferences" view and switches context to the PreferencesController
     * @throws IOException Throws IOException if the "user preferences" view cannot be loaded
     */
    public static void loadPreferencesView() throws IOException {
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

    /**
     * Loads the "breach checker" view and switches context to the BreachCheckController
     * @throws IOException Throws IOException if the "breach checker" view cannot be loaded
     */
    public static void loadBreachCheckerView() throws IOException {
        FXMLLoader breachCheckerLoader = new FXMLLoader(BreachCheckController.class
                .getResource("/com/jamesd/passwordmanager/views/check-for-breaches.fxml"));
        FXMLLoader sideBarLoader = new FXMLLoader(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml"));
        BorderPane breachCheckerPane = breachCheckerLoader.load();
        breachCheckController = breachCheckerLoader.getController();
        breachCheckerPane.setLeft(sideBarLoader.load());
        rootLayout.setTop(null);
        rootLayout.setCenter(breachCheckerPane);
    }

    /**
     * Retrieves the mainStage Stage field
     * @return Stage object
     */
    public static Stage getMainStage() {
        return mainStage;
    }

    /**
     * Retrieves the rootLayout BorderPane field
     * @return BorderPane object
     */
    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    /**
     * Sets the rootLayout BorderPane field
     * @param root BorderPane object to be set
     */
    public static void setRootLayout(BorderPane root) {
        rootLayout = root;
    }

    /**
     * Retrieves the currently logged-in User object
     * @return User object currently logged-in
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Sets the curretnly logged-in User object
     * @param loggedInUser User object currently logged-in
     */
    public static void setLoggedInUser(User loggedInUser) {
        PasswordManagerApp.loggedInUser = loggedInUser;
    }

    /**
     * Retrieves the PasswordHomeController
     * @return PasswordHomeController object
     */
    public static PasswordHomeController getPasswordHomeController() { return passwordHomeController; }

    /**
     * Retrieves the BaseDetailsController object
     * @return BaseDetailsController object
     */
    public static BaseDetailsController getPasswordDetailsController() {
        return passwordDetailsController;
    }

    /**
     * Retrieves the PreferencesController object
     * @return PreferencesController object
     */
    public static PreferencesController getPreferencesController() {
        return preferencesController;
    }

    /**
     * Entry point to the application
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}