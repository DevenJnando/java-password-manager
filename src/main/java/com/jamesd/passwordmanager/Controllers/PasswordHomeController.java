package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Tables.CreditDebitCardTable;
import com.jamesd.passwordmanager.Tables.DatabasePasswordTable;
import com.jamesd.passwordmanager.Tables.DocumentTable;
import com.jamesd.passwordmanager.Tables.WebsitePasswordTable;
import com.jamesd.passwordmanager.Utils.TreeViewIteratorUtil;
import com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.DocumentWrapper;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Home controller for the password manager application. Responsible for populating password entries and their containing
 * folders. It is also responsible for providing an entry point to modals such as "add password", "delete password", etc.
 */
public class PasswordHomeController implements Initializable {

    public static Logger logger = LoggerFactory.getLogger(PasswordHomeController.class);

    /**
     * FXML fields
     */
    @FXML
    private VBox sidebar = new VBox();
    @FXML
    private VBox passwordTableVbox = new VBox();
    @FXML
    private ImageView logo = new ImageView();
    @FXML
    private TitledPane folderMenuTitledPane = new TitledPane();
    @FXML
    private Button homeButton = new Button();
    @FXML
    private Button addPasswordButton = new Button();
    @FXML
    private Button breachCheckerButton = new Button();
    @FXML
    private Button userPreferencesButton = new Button();
    @FXML
    private Button logoutButton = new Button();
    @FXML
    private JFXButton deletePasswordsButton = new JFXButton();

    /**
     * Stage for modals
     */
    private static Stage stage;

    private List<PasswordEntryFolder> passwordEntryFolders;
    private PasswordEntryFolder selectedFolder;
    private BaseAddPasswordController baseAddPasswordController = new BaseAddPasswordController();

    /**
     * Default constructor
     */
    public PasswordHomeController() {

    }

    /**
     * Initialize method which sets the password manager logo (currently a placeholder, sorry 1password!), sets the
     * buttons, the title pane and the icon for the delete button. Finally, the list of PasswordEntryFolder objects is
     * populated
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setImageView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setButtons();
        setTitledPane();
        setDeleteButtonIcon();
        sidebar.getChildren().add(logo);
        sidebar.getChildren().add(homeButton);
        sidebar.getChildren().add(folderMenuTitledPane);
        sidebar.getChildren().add(addPasswordButton);
        sidebar.getChildren().add(breachCheckerButton);
        sidebar.getChildren().add(userPreferencesButton);
        sidebar.getChildren().add(logoutButton);

        Label passwordFolderNavigationLabel = new Label("Load Passwords:");
        passwordFolderNavigationLabel.setId("folderNavigationLabel");
        passwordFolderNavigationLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        sidebar.getChildren().add(passwordFolderNavigationLabel);
        try {
            populatePasswordFolders();
        } catch (LoginException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the TitledPane object and populates it with various options:
     * Add folder button
     * Remove folder button
     */
    public void setTitledPane() {
        VBox passwordFolderSettingsVbox = new VBox();

        // Add folder button with listener
        JFXButton addNewPasswordFolderButton = new JFXButton("Add Folder");
        addNewPasswordFolderButton.setOnAction(e -> {
            try {
                addNewPasswordFolder();
            } catch (LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });

        // Remove folder button with listener
        JFXButton removePasswordFolderButton = new JFXButton("Remove Folder");
        removePasswordFolderButton.setOnAction(e -> {
            try {
                deletePasswordFolder();
            } catch (IOException | LoginException ex) {
                ex.printStackTrace();
            }
        });

        // IDs, height and width added to Button objects
        addNewPasswordFolderButton.setId("addNewFolderButton");
        addNewPasswordFolderButton.setPrefHeight(25);
        addNewPasswordFolderButton.setPrefWidth(200);
        removePasswordFolderButton.setId("removeFolderButton");
        removePasswordFolderButton.setPrefHeight(25);
        removePasswordFolderButton.setPrefWidth(200);

        // Button objects added to the passwordFolderSettingsVbox object
        passwordFolderSettingsVbox.getChildren().add(addNewPasswordFolderButton);
        passwordFolderSettingsVbox.getChildren().add(removePasswordFolderButton);

        // TitledPane created, ID set and passwordFolderSettingsVbox object added to it
        TitledPane passwordFolderSettingsTitledPane = new TitledPane("Password Folder Settings", passwordFolderSettingsVbox);
        passwordFolderSettingsTitledPane.setId("folderSettingsTitlePane");
        passwordFolderSettingsTitledPane.setExpanded(false);

        // passwordFolderSettingsTitledPane object assigned to TitledPane field
        folderMenuTitledPane = passwordFolderSettingsTitledPane;
    }

    /**
     * Retrieves the VBox object which contains the TableView for all passwords in the selected password folder
     * @return VBox containing password's TableView
     */
    public VBox getPasswordTableVbox() {
        return this.passwordTableVbox;
    }

    /**
     * Sets the logo for the password manager application
     * @throws FileNotFoundException Throws FileNotFoundException if the logo cannot be located
     */
    public void setImageView() throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/main/resources/com/jamesd/passwordmanager/icons/1password-logo-round.png"));
        ImageView logo = new ImageView(image);
        logo.setId("sidebarLogo");
        logo.setFitHeight(119);
        logo.setFitWidth(215);
        logo.setPickOnBounds(true);
        logo.setSmooth(false);
        this.logo = logo;
    }

    /**
     * Sets the buttons in the sidebar. These buttons are:
     * Home
     * Add new entry
     * User preferences
     * Logout
     * Listeners are also added to each button which call methods corresponding to their intended functionality
     */
    public void setButtons() {

        // Buttons created
        Button homeButton = new Button("Home");
        Button addPasswordButton = new Button("Add New Entry");
        Button breachCheckerButton = new Button("Breach Checker");
        Button userPreferencesButton = new Button("User Preferences");
        Button logoutButton = new Button("Logout");

        // ID, height and width set for each button
        homeButton.setId("homeButton");
        homeButton.setPrefHeight(35);
        homeButton.setPrefWidth(215);
        addPasswordButton.setId("addPasswordButton");
        addPasswordButton.setPrefHeight(35);
        addPasswordButton.setPrefWidth(215);
        breachCheckerButton.setId("breachCheckerButton");
        breachCheckerButton.setPrefHeight(35);
        breachCheckerButton.setPrefWidth(215);
        userPreferencesButton.setId("userPreferencesButton");
        userPreferencesButton.setPrefHeight(35);
        userPreferencesButton.setPrefWidth(215);
        logoutButton.setId("logoutButton");
        logoutButton.setPrefHeight(35);
        logoutButton.setPrefWidth(215);

        // Listeners added which call each button's relevant methods
        homeButton.setOnAction(e -> {
            try {
                backToHome();
            } catch(LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });
        addPasswordButton.setOnAction(e -> {
            try {
                addPassword();
            } catch(LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });
        breachCheckerButton.setOnAction(e -> {
            try {
                breachChecker();
            } catch(LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });
        userPreferencesButton.setOnAction(e -> {
            try {
                preferences();
            } catch(LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });
        logoutButton.setOnAction(e -> {
            try {
                logout();
            } catch(LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });

        // Button objects assigned to button fields
        this.homeButton = homeButton;
        this.addPasswordButton = addPasswordButton;
        this.breachCheckerButton = breachCheckerButton;
        this.userPreferencesButton = userPreferencesButton;
        this.logoutButton = logoutButton;
    }

    /**
     * Sets the delete button icon
     */
    public void setDeleteButtonIcon() {
        Text delete = GlyphsDude.createIcon(FontAwesomeIcon.TRASH, "3.0em");
        deletePasswordsButton.setGraphic(delete);
    }

    /**
     * Loads the "add folder" modal and switches context to the AddFolderController class
     * @throws IOException Throws IOException if the "add folder" modal cannot be loaded
     */
    private void loadAddPasswordFolderModal() throws IOException {
        Stage addPasswordFolderStage = new Stage();
        FXMLLoader addPasswordFolderLoader = new FXMLLoader(AddFolderController.class
                .getResource("/com/jamesd/passwordmanager/views/add-folder-modal.fxml"));
        AnchorPane addPasswordFolderPane = addPasswordFolderLoader.load();
        Scene addPasswordFolderScene = new Scene(addPasswordFolderPane);
        addPasswordFolderStage.setScene(addPasswordFolderScene);
        addPasswordFolderStage.setTitle("Add New Folder");
        addPasswordFolderStage.initOwner(PasswordManagerApp.getMainStage());
        addPasswordFolderStage.initModality(Modality.APPLICATION_MODAL);
        stage = addPasswordFolderStage;
        stage.showAndWait();
    }

    /**
     * Loads the "delete folder" modal and switches context to the DeleteFolderController class
     * @throws IOException Throws IOException if the "delete folder" modal cannot be loaded
     */
    private void loadDeletePasswordFolderModal() throws IOException {
        Stage deletePasswordFolderStage = new Stage();
        FXMLLoader deletePasswordFolderLoader = new FXMLLoader(DeleteFolderController.class
                .getResource("/com/jamesd/passwordmanager/views/delete-folder-modal.fxml"));
        AnchorPane deletePasswordFolderPane = deletePasswordFolderLoader.load();
        Scene deletePasswordFolderScene = new Scene(deletePasswordFolderPane);
        deletePasswordFolderStage.setScene(deletePasswordFolderScene);
        deletePasswordFolderStage.setTitle("Delete Folder");
        deletePasswordFolderStage.initOwner(PasswordManagerApp.getMainStage());
        deletePasswordFolderStage.initModality(Modality.APPLICATION_MODAL);
        stage = deletePasswordFolderStage;
        stage.showAndWait();
    }

    /**
     * Loads the "add password" modal and switches context to the BaseAddPasswordController class
     * @throws IOException Throws IOException if the "add password" modal cannot be loaded
     */
    private void loadAddPasswordModal() throws IOException {
        Stage addPasswordStage = new Stage();
        FXMLLoader addPasswordLoader = new FXMLLoader(BaseAddPasswordController.class
                .getResource("/com/jamesd/passwordmanager/views/base-add-password-modal.fxml"));
        AnchorPane addPasswordPane = addPasswordLoader.load();
        PasswordManagerApp.getPasswordHomeController().setBaseAddPasswordController(addPasswordLoader.getController());
        Scene addPasswordScene = new Scene(addPasswordPane);
        addPasswordStage.setScene(addPasswordScene);
        addPasswordStage.setTitle("Add New Password");
        addPasswordStage.initOwner(PasswordManagerApp.getMainStage());
        addPasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = addPasswordStage;
        stage.showAndWait();
    }

    /**
     * Loads the "delete multiple passwords" modal. Context does not switch to the DeletePasswordController until the deletion
     * action has been confirmed
     * @throws IOException Throws IOException if the "delete multiple passwords" modal cannot be loaded
     */
    private void loadDeletePasswordModal() throws IOException{
        Stage deletePasswordStage = new Stage();
        FXMLLoader deletePasswordLoader = new FXMLLoader(PasswordHomeController.class
                .getResource("/com/jamesd/passwordmanager/views/delete-multiple-passwords-modal.fxml"));
        AnchorPane deletePasswordPane = deletePasswordLoader.load();
        Scene deletePasswordScene = new Scene(deletePasswordPane);
        deletePasswordStage.setScene(deletePasswordScene);
        deletePasswordStage.setTitle("Delete Passwords");
        deletePasswordStage.initOwner(PasswordManagerApp.getMainStage());
        deletePasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = deletePasswordStage;
        stage.showAndWait();
    }

    /**
     * Loads the "logout" modal and switches context to the LogoutController
     * @throws IOException Throws IOExcecption if the "logout" modal cannot be loaded
     */
    private void loadLogoutModal() throws IOException{
        Stage logoutStage = new Stage();
        FXMLLoader logoutLoader = new FXMLLoader(LogoutController.class
                .getResource("/com/jamesd/passwordmanager/views/logout.fxml"));
        AnchorPane logoutPane = logoutLoader.load();
        Scene logoutScene = new Scene(logoutPane);
        logoutStage.setScene(logoutScene);
        logoutStage.setTitle("Logout");
        logoutStage.initOwner(PasswordManagerApp.getMainStage());
        logoutStage.initModality(Modality.APPLICATION_MODAL);
        stage = logoutStage;
        stage.showAndWait();
    }

    /**
     * Retrieves all PasswordEntryFolders which belong to the currently logged-in user and sorts them into their
     * respective folder types. E.g. "Website password folders" will have its own subset of folders, as will
     * "Database password folders" etc.
     * @throws LoginException Throws LoginException if the user calls this method whilst not logged in
     * @throws ClassNotFoundException Throws ClassNotFoundException if one or more of the PasswordEntry subclasses
     * cannot be found
     */
    public void populatePasswordFolders() throws LoginException, ClassNotFoundException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {

            // Establishes the PasswordEntry subclasses
            Class<?> websiteEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            Class<?> databaseEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
            Class<?> creditDebitCardEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
            Class<?> documentEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DocumentEntry");

            // Root node of the TreeView
            TreeItem<String> folderNavigationRoot = new TreeItem<>("Root Folder");

            List<PasswordEntryFolder> passwordEntryFolders = StoredPassSQLQueries.queryPasswordFolderContainerByUsername(
                    PasswordManagerApp.getLoggedInUser().getUsername());
            PasswordManagerApp.getPasswordHomeController().setPasswordEntryFolders(passwordEntryFolders);

            // Loops over each PasswordEntryFolder which was retrieved
            for(PasswordEntryFolder folder : PasswordManagerApp.getPasswordHomeController().getPasswordEntryFolders()) {
                TreeItem<String> folderLeaf = new TreeItem<>(folder.getPasswordFolder(), GlyphsDude.createIcon(FontAwesomeIcon.FOLDER, "1.5em"));
                boolean set = false;

                // Loops over each node in the root node TreeItem
                for (TreeItem<String> folderNode : folderNavigationRoot.getChildren()) {

                    // If the current node being accessed is an entry type folder e.g. "Website Passwords", the leaf node
                    // is added to the parent entry type folder node
                    if(folderNode.getValue().contentEquals(folder.getReadableTypeString())) {
                        folderNode.getChildren().add(folderLeaf);
                        set = true;
                        break;
                    }
                }

                // If this is not the case, it means that the entry type folder has not been created yet. This block
                // creates the entry type folder node after determining which type it should be, and adds the leaf node
                // to it
                if(!set) {
                    String folderType = folder.getReadableTypeString();
                    Text icon = null;
                    Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
                    if(websiteEntryClass.equals(classOfEntry)) {
                        icon = GlyphsDude.createIcon(FontAwesomeIcon.INTERNET_EXPLORER, "1.5em");
                    } else if(databaseEntryClass.equals(classOfEntry)) {
                        icon = GlyphsDude.createIcon(FontAwesomeIcon.DATABASE, "1.5em");
                    } else if(creditDebitCardEntryClass.equals(classOfEntry)) {
                        icon = GlyphsDude.createIcon(FontAwesomeIcon.CREDIT_CARD, "1.5em");
                    } else if(documentEntryClass.equals(classOfEntry)) {
                        icon = GlyphsDude.createIcon(FontAwesomeIcon.FILE, "1.5em");
                    }
                    TreeItem<String> folderNode = new TreeItem<>(folderType, icon);
                    folderNavigationRoot.getChildren().add(folderNode);
                    folderNode.getChildren().add(folderLeaf);
                }
            }

            TreeView<String> navView = new TreeView<>(folderNavigationRoot);

            // Adds a listener to the TreeView object which triggers when a node in the TreeView is selected by the user
            // If it is a leaf node - and therefore a folder containing passwords - then it is determined what type
            // of passwords are contained within the node and the appropriate method is then called
            navView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                if(newValue.isLeaf()) {
                    for(PasswordEntryFolder folder : PasswordManagerApp.getPasswordHomeController().getPasswordEntryFolders()) {
                        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
                        if(folder.getPasswordFolder().equals(newValue.getValue())) {
                            if(websiteEntryClass.equals(classOfEntry)) {
                                populateWebsiteEntryPasswords(folder);
                            } else if(databaseEntryClass.equals(classOfEntry)) {
                                populateDatabaseEntryPasswords(folder);
                            } else if(creditDebitCardEntryClass.equals(classOfEntry)) {
                                populateCreditDebitCardEntryPasswords(folder);
                            } else if(documentEntryClass.equals(classOfEntry)) {
                                populateDocumentEntryPasswords(folder);
                            }
                        }
                    }
                }
            });

            // Sets the ID, height and width
            navView.setId("folderNavigationTreeView");
            navView.setPrefWidth(215);
            navView.setPrefHeight(265);

            // Assigns the TreeView object to the folderNavigationTreeView field and determines whether to add it as a
            // new child to the sidebar VBox, or whether to replace an already existing TreeView object in the sidebar
            // VBox
            List<Node> navViews = sidebar.getChildren()
                    .stream()
                    .filter(o -> o.getId().equals("folderNavigationTreeView"))
                    .collect(Collectors.toList());
            if(navViews.isEmpty()) {
                sidebar.getChildren().add(navView);
            } else {
                sidebar.getChildren().set(7, navView);
                FXMLLoader sideBarLoader = new FXMLLoader(PasswordHomeController.class
                        .getResource("/com/jamesd/passwordmanager/views/sidebar-menu.fxml"));
                BorderPane homePane = (BorderPane) PasswordManagerApp.getRootLayout().getCenter();
                homePane.setLeft(sideBarLoader.load());
                PasswordManagerApp.setRootLayout(homePane);
            }
            setSidebar(sidebar);

        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Triggered when a "website password" folder is selected by the user. A new WebsitePasswordTable is generated,
     * which contains all the WebsitePasswordEntry objects located within the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user. Contains only WebsitePasswordEntry objects
     */
    public void populateWebsiteEntryPasswords(PasswordEntryFolder folder) {
        PasswordManagerApp.getPasswordHomeController().setSelectedFolder(folder);
        WebsitePasswordTable websitePasswordTable = new WebsitePasswordTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren();
            TableView<WebsitePasswordEntryWrapper> websitePasswordEntryTableView =
                    websitePasswordTable.createTableView(folder);
            websitePasswordEntryTableView.setId("websiteEntryTableView");
            websitePasswordEntryTableView.setMinHeight(210.0);
            websitePasswordEntryTableView.setPrefHeight(210.0);
            websitePasswordEntryTableView.setPrefWidth(1085.0);
            websitePasswordEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(tableViewList.size() < 2) {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(websitePasswordEntryTableView);
            } else {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, websitePasswordEntryTableView);
            }
        } catch (MalformedURLException
                | LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered when a "database password" folder is selected by the user. A new DatabasePasswordTable is generated,
     * which contains all the DatabasePasswordEntry objects located within the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user. Contains only DatabasePasswordEntry objects
     */
    public void populateDatabaseEntryPasswords(PasswordEntryFolder folder) {
        PasswordManagerApp.getPasswordHomeController().setSelectedFolder(folder);
        DatabasePasswordTable databasePasswordTable = new DatabasePasswordTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren();
            TableView<DatabasePasswordEntryWrapper> databasePasswordEntryTableView =
                    databasePasswordTable.createTableView(folder);
            databasePasswordEntryTableView.setId("databaseEntryTableView");
            databasePasswordEntryTableView.setMinHeight(210.0);
            databasePasswordEntryTableView.setPrefHeight(210.0);
            databasePasswordEntryTableView.setPrefWidth(1085.0);
            databasePasswordEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(tableViewList.size() < 2) {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(databasePasswordEntryTableView);
            } else {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, databasePasswordEntryTableView);
            }
        } catch (MalformedURLException
                | LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered when a "credit card" folder is selected by the user. A new CreditDebitCardTable is generated,
     * which contains all the CreditDebitCardEntry objects located within the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user. Contains only CreditDebitCardEntry objects
     */
    public void populateCreditDebitCardEntryPasswords(PasswordEntryFolder folder) {
        PasswordManagerApp.getPasswordHomeController().setSelectedFolder(folder);
        CreditDebitCardTable creditDebitCardTable = new CreditDebitCardTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren();
            TableView<CreditDebitCardEntryWrapper> creditDebitCardEntryTableView =
                    creditDebitCardTable.createTableView(folder);
            creditDebitCardEntryTableView.setId("creditDebitCardEntryTableView");
            creditDebitCardEntryTableView.setMinHeight(210.0);
            creditDebitCardEntryTableView.setPrefHeight(210.0);
            creditDebitCardEntryTableView.setPrefWidth(1085.0);
            creditDebitCardEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(tableViewList.size() < 2) {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(creditDebitCardEntryTableView);
            } else {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, creditDebitCardEntryTableView);
            }
        } catch (MalformedURLException
                | LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered when a "document" folder is selected by the user. A new DocumentTable is generated,
     * which contains all the DocumentEntry objects located within the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user. Contains only DocumentEntry objects
     */
    public void populateDocumentEntryPasswords(PasswordEntryFolder folder) {
        PasswordManagerApp.getPasswordHomeController().setSelectedFolder(folder);
        DocumentTable documentTable = new DocumentTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren();
            TableView<DocumentWrapper> documentEntryTableView =
                    documentTable.createTableView(folder);
            documentEntryTableView.setId("documentEntryTableView");
            documentEntryTableView.setMinHeight(210.0);
            documentEntryTableView.setPrefHeight(210.0);
            documentEntryTableView.setPrefWidth(1085.0);
            documentEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(tableViewList.size() < 2) {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(documentEntryTableView);
            } else {
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, documentEntryTableView);
            }
        } catch (LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Triggered by the "add new folder" button. Calls the method to load the "add new folder" modal
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "add new password" modal cannot be loaded
     */
    @FXML
    private void addNewPasswordFolder() throws LoginException, IOException{
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadAddPasswordFolderModal();
            logger.info("Switched context to AddFolderController");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Triggered by the "delete folder" button. Calls the method to load the "delete folder" modal
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "delete passwords" modal cannot be loaded
     */
    @FXML
    private void deletePasswordFolder() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadDeletePasswordFolderModal();
            logger.info("Switched context to DeleteFolderController");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Triggered by the "home" button. Reloads the Password homepage from elsewhere in the application
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "Password home" view cannot be loaded
     */
    @FXML
    private void backToHome() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            PasswordManagerApp.loadPasswordHomeView();
            logger.info("Reloaded home page from another context.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Triggered by the "User Preferences" button. Calls the method to load the "preferences" view and switch
     * context to the PreferencesController
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "preferences" view cannot be loaded
     */
    @FXML
    private void preferences() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            PasswordManagerApp.loadPreferencesView();
            logger.info("Switched context to PreferencesController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process");
        }
    }

    /**
     * Triggered by the "Breach Checker" button. Calls the method to load the "check for breaches" view and switch
     * context to the BreachCheckController
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "breach checker" view cannot be loaded
     */
    @FXML
    private void breachChecker() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            PasswordManagerApp.loadBreachCheckerView();
            logger.info("Switched context to BreachCheckController");
        } else {
            throw new LoginException("User is not logged in. Aborting process");
        }
    }

    /**
     * Consolidates each Object which has been check-marked within a TableView, casts them to an appropriate
     * class Wrapper type e.g. WebsitePasswordEntryWrapper and adds them to a list. The "delete
     * multiple passwords" modal is then loaded if this list is not empty
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "delete multiple passwords" modal cannot be loaded
     */
    @FXML
    public void deletePasswords() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            List<Object> passwordsToBeDeleted = new ArrayList<>();
            TableView<Object> tableView =
                    (TableView<Object>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);

            // Each object in the TableView is looped over
            for(Object o : tableView.getItems()){
                try {

                    // A reflector pattern is used to check what type of Wrapper the Objects are, and then they are
                    // cast to that Wrapper type and added to the list of passwords to be deleted
                    Class<?> websitePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper");
                    Class<?> databasePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper");
                    Class<?> creditDebitCardEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper");
                    Class<?> documentEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.DocumentWrapper");
                    if(websitePasswordEntryWrapperClass.isInstance(o)) {
                        WebsitePasswordEntryWrapper websitePasswordEntryWrapper = (WebsitePasswordEntryWrapper) o;
                        if(websitePasswordEntryWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(websitePasswordEntryWrapper);
                        }
                    } if(databasePasswordEntryWrapperClass.isInstance(o)) {
                        DatabasePasswordEntryWrapper databasePasswordEntryWrapper = (DatabasePasswordEntryWrapper) o;
                        if(databasePasswordEntryWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(databasePasswordEntryWrapper);
                        }
                    } if(creditDebitCardEntryWrapperClass.isInstance(o)) {
                        CreditDebitCardEntryWrapper creditDebitCardEntryWrapper = (CreditDebitCardEntryWrapper) o;
                        if(creditDebitCardEntryWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(creditDebitCardEntryWrapper);
                        }
                    } if(documentEntryWrapperClass.isInstance(o)) {
                        DocumentWrapper documentWrapper = (DocumentWrapper) o;
                        if(documentWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(documentWrapper);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if(!passwordsToBeDeleted.isEmpty()) {
                loadDeletePasswordModal();
                logger.info("Loaded delete passwords modal.");
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Upon confirmation from the user, the selected passwords are removed from the password database by creating
     * a new DeletePasswordController object and calling the controller's deleteMultipleEntries method taking the
     * current TableView object and the selected PasswordEntryFolder as arguments. The TableView object is then
     * repopulated after the selected entries have been deleted
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged-in
     * @throws ClassNotFoundException Throws ClassNotFoundException if any subclass of PasswordEntry cannot be found
     */
    @FXML
    private void confirmDelete() throws LoginException, ClassNotFoundException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Class<?> websitePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            Class<?> databasePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
            Class<?> creditDebitCardEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
            Class<?> documentEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DocumentEntry");
            TableView<Object> tableView =
                    (TableView<Object>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);

            DeletePasswordController deletePasswordController = new DeletePasswordController();
            deletePasswordController.deleteMultipleEntries(tableView, PasswordManagerApp.getPasswordHomeController().getSelectedFolder());
            PasswordManagerApp.getPasswordHomeController().populatePasswordFolders();

            if(PasswordEntryFolder.EntryFactory.determineEntryType(PasswordManagerApp.getPasswordHomeController().getSelectedFolder())
                    .equals(websitePasswordEntryClass)) {
                PasswordManagerApp.getPasswordHomeController().populateWebsiteEntryPasswords(PasswordManagerApp.getPasswordHomeController().getSelectedFolder());
            } if(PasswordEntryFolder.EntryFactory.determineEntryType(PasswordManagerApp.getPasswordHomeController().getSelectedFolder())
                    .equals(databasePasswordEntryClass)) {
                PasswordManagerApp.getPasswordHomeController().populateDatabaseEntryPasswords(PasswordManagerApp.getPasswordHomeController().getSelectedFolder());
            } if(PasswordEntryFolder.EntryFactory.determineEntryType(PasswordManagerApp.getPasswordHomeController().getSelectedFolder())
                    .equals(creditDebitCardEntryClass)) {
                PasswordManagerApp.getPasswordHomeController().populateCreditDebitCardEntryPasswords(PasswordManagerApp.getPasswordHomeController().getSelectedFolder());
            } if(PasswordEntryFolder.EntryFactory.determineEntryType(PasswordManagerApp.getPasswordHomeController().getSelectedFolder())
                    .equals(documentEntryClass)) {
                PasswordManagerApp.getPasswordHomeController().populateDocumentEntryPasswords(PasswordManagerApp.getPasswordHomeController().getSelectedFolder());
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Cancels the deletion operation and closes the "delete multiple passwords" modal
     */
    @FXML
    private void cancelDelete() {
        getStage().close();
    }

    /**
     * Triggered by the "logout" button. Calls the method to load the "logout" modal
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "logout" modal cannot be loaded
     */
    @FXML
    public void logout() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadLogoutModal();
            logger.info("Switched context to LogoutController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Triggered by the "add new password" button. Calls the method which loads the "add new password" modal
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws IOException Throws IOException if the "add new password" modal cannot be loaded
     */
    @FXML
    public void addPassword() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadAddPasswordModal();
            logger.info("Switched context to the AddPasswordController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    /**
     * Called when a new password is successfully added to the database. Displays the newly added password details to
     * the user
     * @throws ClassNotFoundException Throws ClassNotFoundException if any subclass of PasswordEntry cannot be found
     */
    public void viewNewlyAddedPassword() throws ClassNotFoundException {
        List<Node> treeView = sidebar.getChildren()
                .stream()
                .filter(o -> o.getId().equals("folderNavigationTreeView"))
                .collect(Collectors.toList());

        if(!treeView.isEmpty()) {
            TreeView<String> folderNavigationTreeView = (TreeView<String>) treeView.get(0);
            if (PasswordManagerApp.getPasswordHomeController()
                    .getBaseAddPasswordController()
                    .getSelectedFolder()
                    .getPasswordFolder() != null) {

                PasswordEntryFolder selectedFolder = PasswordManagerApp.getPasswordHomeController()
                        .getBaseAddPasswordController().getSelectedFolder();

                // Iterates over each child node of the folderNavigationTreeView root node and selects the
                // folder which has had a new password added to it
                TreeViewIteratorUtil<String> treeViewIteratorUtil = new TreeViewIteratorUtil<>(folderNavigationTreeView.getRoot());
                TreeItem<String> childNode = new TreeItem<>();
                while(treeViewIteratorUtil.hasNext()) {
                    TreeItem<String> currentNode = treeViewIteratorUtil.next();
                    if(currentNode.getValue().equals(selectedFolder.getPasswordFolder())) {
                        childNode = currentNode;
                        break;
                    }
                }
                childNode.getParent().setExpanded(true);
                folderNavigationTreeView.getSelectionModel().select(childNode);

                // Reflection used to determine which TableView to generate and populate. The newly added password is
                // then selected from this TableView
                Class<?> websitePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
                Class<?> databasePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
                Class<?> creditDebitCardEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
                Class<?> documentEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DocumentEntry");
                if(websitePasswordEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateWebsiteEntryPasswords(selectedFolder);
                    TableView<WebsitePasswordEntryWrapper> tableView = (TableView<WebsitePasswordEntryWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
                if(databasePasswordEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateDatabaseEntryPasswords(selectedFolder);
                    TableView<DatabasePasswordEntryWrapper> tableView = (TableView<DatabasePasswordEntryWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
                if(creditDebitCardEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateCreditDebitCardEntryPasswords(selectedFolder);
                    TableView<CreditDebitCardEntryWrapper> tableView = (TableView<CreditDebitCardEntryWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
                if(documentEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateDocumentEntryPasswords(selectedFolder);
                    TableView<DocumentWrapper> tableView = (TableView<DocumentWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
            }
        }
    }

    /**
     * Retrieves the Stage which contains a modal
     * @return Stage containing a modal
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Retrieves the sidebar VBox object
     * @return VBox which contains all menu option buttons
     */
    public VBox getSidebar() {
        return sidebar;
    }

    /**
     * Sets the sidebar VBox object
     * @param sidebar VBox which contains all menu option buttons
     */
    public void setSidebar(VBox sidebar) {
        this.sidebar = sidebar;
    }

    /**
     * Retrieves the list of all PasswordEntryFolders
     * @return List of PasswordEntryFolders
     */
    public List<PasswordEntryFolder> getPasswordEntryFolders() {
        return passwordEntryFolders;
    }

    /**
     * Sets the list of all PasswordEntryFolders
     * @param passwordEntryFolders List of PasswordEntryFolders
     */
    public void setPasswordEntryFolders(List<PasswordEntryFolder> passwordEntryFolders) {
        this.passwordEntryFolders = passwordEntryFolders;
    }

    /**
     * Retrieves the BaseAddPasswordController assigned to this controller
     * @return BaseAddPasswordController
     */
    public BaseAddPasswordController getBaseAddPasswordController() {
        return baseAddPasswordController;
    }

    /**
     * Sets the BaseAddPasswordController assigned to this controller
     * @param baseAddPasswordController BaseAddPasswordController
     */
    public void setBaseAddPasswordController(BaseAddPasswordController baseAddPasswordController) {
        this.baseAddPasswordController = baseAddPasswordController;
    }

    /**
     * Retrieves the selected PasswordEntryFolder
     * @return PasswordEntryFolder selected by the user
     */
    public PasswordEntryFolder getSelectedFolder() {
        return selectedFolder;
    }

    /**
     * Sets the selected PasswordEntryFolder
     * @param folder PasswordEntryFolder selected by the user
     */
    public void setSelectedFolder(PasswordEntryFolder folder) {
        selectedFolder = folder;
    }
}
