package com.jamesd.passwordmanager;

import com.jamesd.passwordmanager.Controllers.*;
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
    //  Fix that damn sidebar!!!! - In progress...
    //  The sidebar should really have its own controller instead of being nastily coupled to the PasswordHomeController - DONE!!!!!!
    // #################################################################################################################
    // Abstract add/modification/deletion of passwords for more generic code - DONE!!!!!!
    // User preferences - reminder timings, change master password, two-factor settings - DONE!!!!!!
    // Check for insecurities or breaches - DONE!!!!!!!
    // Consider having a more organised hierarchy e.g. users > root password folder > work passwords > Google - DONE!!!!!!!
    // Consider allowing storage of other kinds of credential e.g. database passwords, credit cards,
    // sensitive documents etc. - DONE!!!!!
    // Allow dynamic login for either email or username - DONE!!!!!!

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
    private static SidebarController sidebarController;
    private static BaseDetailsController passwordDetailsController;
    private static PreferencesController preferencesController;
    private static BreachCheckController breachCheckController;
    private static int centerSet;

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
        sidebarController = new SidebarController();
        passwordDetailsController = new BaseDetailsController();
        preferencesController = new PreferencesController();
        breachCheckController = new BreachCheckController();
        centerSet = 0;
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
    public static void loadSuccessfulLoginView() throws IOException {
        rootLayout.setTop(null);
        setTableAsBottom();
        setSidebarAsLeft();
        setDetailsAsCenter();
    }

    public static void setTableAsBottom() throws IOException {
        FXMLLoader passwordHomeLoader = new FXMLLoader(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/users-passwords.fxml"));
        VBox bottomPane = passwordHomeLoader.load();
        passwordHomeController = passwordHomeLoader.getController();
        rootLayout.setBottom(bottomPane);
    }

    public static void setSidebarAsLeft() throws IOException {
        FXMLLoader sidebarLoader = new FXMLLoader(SidebarController.class
                .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml"));
        VBox leftPane = sidebarLoader.load();
        sidebarController = sidebarLoader.getController();
        rootLayout.setLeft(leftPane);
    }

    public static void setDetailsAsCenter() throws IOException {
        FXMLLoader passwordDetailsLoader = new FXMLLoader(BaseDetailsController.class
                .getResource("/com/jamesd/passwordmanager/views/base-details-view.fxml"));
        VBox detailsVbox = passwordDetailsLoader.load();
        passwordDetailsController = passwordDetailsLoader.getController();
        centerSet = 1;
        rootLayout.setCenter(detailsVbox);
    }

    public static void setPreferencesAsCenter() throws IOException {
        FXMLLoader preferencesLoader = new FXMLLoader(PreferencesController.class
                .getResource("/com/jamesd/passwordmanager/views/preferences.fxml"));
        VBox preferencesVbox = preferencesLoader.load();
        preferencesController = preferencesLoader.getController();
        centerSet = 2;
        rootLayout.setCenter(preferencesVbox);
    }

    public static void setBreachCheckerAsCenter() throws IOException {
        FXMLLoader breachCheckerLoader = new FXMLLoader(BreachCheckController.class
                .getResource("/com/jamesd/passwordmanager/views/check-for-breaches.fxml"));
        VBox breachCheckerVbox = breachCheckerLoader.load();
        breachCheckController = breachCheckerLoader.getController();
        centerSet = 3;
        rootLayout.setCenter(breachCheckerVbox);
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
     * Returns an integer which corresponds with the current view set to the center
     * @return 0 if unset, 1 if details view, 2 if preferences view and 3 if breach checker view
     */
    public static int getCenterSet() {
        return centerSet;
    }

    /**
     * Retrieves the PasswordHomeController
     * @return PasswordHomeController object
     */
    public static PasswordHomeController getPasswordHomeController() { return passwordHomeController; }

    /**
     * Retrieves the SidebarController
     * @return SidebarController object
     */
    public static SidebarController getSidebarController() { return sidebarController; }

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
     * Retrieves the BreachCheckController object
     * @return BreachCheckController object
     */
    public static BreachCheckController getBreachCheckController() { return breachCheckController; }

    /**
     * Entry point to the application
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}