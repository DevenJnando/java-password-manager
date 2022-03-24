package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Tables.DatabasePasswordTable;
import com.jamesd.passwordmanager.Tables.WebsitePasswordTable;
import com.jamesd.passwordmanager.Utils.TreeViewIteratorUtil;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PasswordHomeController implements Initializable {

    public static Logger logger = LoggerFactory.getLogger(PasswordHomeController.class);

    @FXML
    private VBox sidebar = new VBox();
    @FXML
    private VBox passwordTableVbox = new VBox();
    @FXML
    private ImageView logo = new ImageView();
    @FXML
    private TitledPane folderMenuTitlePane = new TitledPane();
    @FXML
    private TreeView<String> folderNavigationTreeView = new TreeView<>();
    @FXML
    private Button homeButton = new Button();
    @FXML
    private Button addPasswordButton = new Button();
    @FXML
    private Button userPreferencesButton = new Button();
    @FXML
    private Button logoutButton = new Button();
    @FXML
    private JFXButton deletePasswordsButton = new JFXButton();

    private static Stage stage;
    private List<PasswordEntryFolder> passwordEntryFolders;
    private PasswordEntryFolder selectedFolder;
    private BaseAddPasswordController baseAddPasswordController = new BaseAddPasswordController();

    public PasswordHomeController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setImageView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        setButtons();
        setTitlePane();
        setDeleteButtonIcon();
        sidebar.getChildren().add(logo);
        sidebar.getChildren().add(homeButton);
        sidebar.getChildren().add(folderMenuTitlePane);

        sidebar.getChildren().add(addPasswordButton);
        sidebar.getChildren().add(userPreferencesButton);
        sidebar.getChildren().add(logoutButton);

        Label passwordFolderNavigationLabel = new Label("Load Passwords:");
        passwordFolderNavigationLabel.setId("folderNavigationLabel");
        passwordFolderNavigationLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        sidebar.getChildren().add(passwordFolderNavigationLabel);
        try {
            populatePasswordFolders();
        } catch (LoginException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setTitlePane() {
        VBox passwordFolderSettingsVbox = new VBox();
        JFXButton addNewPasswordFolderButton = new JFXButton("Add Folder");
        addNewPasswordFolderButton.setOnAction(e -> {
            try {
                addNewPasswordFolder();
            } catch (LoginException | IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        JFXButton removePasswordFolderButton = new JFXButton("Remove Folder");
        removePasswordFolderButton.setOnAction(e -> {
            try {
                deletePasswordFolder();
            } catch (IOException | LoginException ex) {
                ex.printStackTrace();
            }
        });
        addNewPasswordFolderButton.setId("addNewFolderButton");
        addNewPasswordFolderButton.setPrefHeight(25);
        addNewPasswordFolderButton.setPrefWidth(200);
        removePasswordFolderButton.setId("removeFolderButton");
        removePasswordFolderButton.setPrefHeight(25);
        removePasswordFolderButton.setPrefWidth(200);
        passwordFolderSettingsVbox.getChildren().add(addNewPasswordFolderButton);
        passwordFolderSettingsVbox.getChildren().add(removePasswordFolderButton);
        TitledPane passwordFolderSettingsTitlePane = new TitledPane("Password Folder Settings", passwordFolderSettingsVbox);
        passwordFolderSettingsTitlePane.setId("folderSettingsTitlePane");
        passwordFolderSettingsTitlePane.setExpanded(false);
        folderMenuTitlePane = passwordFolderSettingsTitlePane;
    }

    public VBox getPasswordTableVbox() {
        return this.passwordTableVbox;
    }

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

    public void setButtons() {
        Button homeButton = new Button("Home");
        Button addPasswordButton = new Button("Add New Entry");
        Button userPreferencesButton = new Button("User Preferences");
        Button logoutButton = new Button("Logout");
        homeButton.setId("homeButton");
        homeButton.setPrefHeight(35);
        homeButton.setPrefWidth(215);
        addPasswordButton.setId("addPasswordButton");
        addPasswordButton.setPrefHeight(35);
        addPasswordButton.setPrefWidth(215);
        userPreferencesButton.setId("userPreferencesButton");
        userPreferencesButton.setPrefHeight(35);
        userPreferencesButton.setPrefWidth(215);
        logoutButton.setId("logoutButton");
        logoutButton.setPrefHeight(35);
        logoutButton.setPrefWidth(215);
        homeButton.setOnAction(e -> {
            try {
                backToHome();
            } catch (LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });
        addPasswordButton.setOnAction(e -> {
            try {
                addPassword();
            } catch (LoginException | IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        userPreferencesButton.setOnAction(e -> {
            try {
                preferences();
            } catch (LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });
        logoutButton.setOnAction(e -> {
            try {
                logout();
            } catch (LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });
        this.homeButton = homeButton;
        this.addPasswordButton = addPasswordButton;
        this.userPreferencesButton = userPreferencesButton;
        this.logoutButton = logoutButton;
    }

    public void setDeleteButtonIcon() {
        Text delete = GlyphsDude.createIcon(FontAwesomeIcon.TRASH, "3.0em");
        deletePasswordsButton.setGraphic(delete);
    }

    private void loadAddPasswordFolderModal() throws IOException, LoginException, ClassNotFoundException {
        Stage addPasswordFolderStage = new Stage();
        FXMLLoader addPasswordFolderLoader = new FXMLLoader(AddFolderController.class.getResource("/com/jamesd/passwordmanager/views/add-folder-modal.fxml"));
        AnchorPane addPasswordFolderPane = addPasswordFolderLoader.load();
        Scene addPasswordFolderScene = new Scene(addPasswordFolderPane);
        addPasswordFolderStage.setScene(addPasswordFolderScene);
        addPasswordFolderStage.setTitle("Add New Folder");
        addPasswordFolderStage.initOwner(PasswordManagerApp.getMainStage());
        addPasswordFolderStage.initModality(Modality.APPLICATION_MODAL);
        stage = addPasswordFolderStage;
        stage.showAndWait();
        PasswordManagerApp.getPasswordHomeController().populatePasswordFolders();
    }

    private void loadDeletePasswordFolderModal() throws IOException {
        Stage addPasswordFolderStage = new Stage();
        FXMLLoader addPasswordFolderLoader = new FXMLLoader(AddFolderController.class.getResource("/com/jamesd/passwordmanager/views/delete-folder-modal.fxml"));
        AnchorPane addPasswordFolderPane = addPasswordFolderLoader.load();
        Scene addPasswordFolderScene = new Scene(addPasswordFolderPane);
        addPasswordFolderStage.setScene(addPasswordFolderScene);
        addPasswordFolderStage.setTitle("Delete Folder");
        addPasswordFolderStage.initOwner(PasswordManagerApp.getMainStage());
        addPasswordFolderStage.initModality(Modality.APPLICATION_MODAL);
        stage = addPasswordFolderStage;
        stage.showAndWait();
    }

    private void loadAddPasswordModal() throws IOException {
        Stage addPasswordStage = new Stage();
        FXMLLoader addPasswordLoader = new FXMLLoader(BaseAddPasswordController.class.getResource("/com/jamesd/passwordmanager/views/base-add-password-modal.fxml"));
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

    private void loadLogoutModal() throws IOException{
        Stage logoutStage = new Stage();
        AnchorPane logoutPane = FXMLLoader.load(LogoutController.class.getResource("/com/jamesd/passwordmanager/views/logout.fxml"));
        Scene logoutScene = new Scene(logoutPane);
        logoutStage.setScene(logoutScene);
        logoutStage.setTitle("Logout");
        logoutStage.initOwner(PasswordManagerApp.getMainStage());
        logoutStage.initModality(Modality.APPLICATION_MODAL);
        stage = logoutStage;
        stage.showAndWait();
    }

    public void populatePasswordFolders() throws LoginException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Class<?> websiteEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            Class<?> databaseEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
            TreeItem<String> folderNavigationRoot = new TreeItem<>("Root Folder");
            List<PasswordEntryFolder> passwordEntryFolders = StoredPassSQLQueries.queryPasswordFolderContainerByUsername(
                    PasswordManagerApp.getLoggedInUser().getUsername());
            PasswordManagerApp.getPasswordHomeController().setPasswordEntryFolders(passwordEntryFolders);
            for(PasswordEntryFolder folder : PasswordManagerApp.getPasswordHomeController().getPasswordEntryFolders()) {
                TreeItem<String> folderLeaf = new TreeItem<>(folder.getPasswordFolder(), GlyphsDude.createIcon(FontAwesomeIcon.FOLDER, "1.5em"));
                Boolean set = false;
                for (TreeItem<String> folderNode : folderNavigationRoot.getChildren()) {
                    if(folderNode.getValue().contentEquals(folder.getReadableTypeString())) {
                        folderNode.getChildren().add(folderLeaf);
                        set = true;
                        break;
                    }
                }
                if(!set) {
                    String folderType = folder.getReadableTypeString();
                    Text icon = null;
                    Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
                    if(websiteEntryClass.equals(classOfEntry)) {
                        icon = GlyphsDude.createIcon(FontAwesomeIcon.INTERNET_EXPLORER, "1.5em");
                    } else if(databaseEntryClass.equals(classOfEntry)) {
                        icon = GlyphsDude.createIcon(FontAwesomeIcon.DATABASE, "1.5em");
                    }
                    TreeItem<String> folderNode = new TreeItem<>(folderType, icon);
                    folderNavigationRoot.getChildren().add(folderNode);
                    folderNode.getChildren().add(folderLeaf);
                }
            }
            TreeView<String> navView = new TreeView<>(folderNavigationRoot);
            navView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                if(newValue.isLeaf()) {
                    for(PasswordEntryFolder folder : PasswordManagerApp.getPasswordHomeController().getPasswordEntryFolders()) {
                        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
                        if(folder.getPasswordFolder().equals(newValue.getValue())) {
                            if(websiteEntryClass.equals(classOfEntry)) {
                                populateWebsiteEntryPasswords(folder);
                            } else if(databaseEntryClass.equals(classOfEntry)) {
                                populateDatabaseEntryPasswords(folder);
                            }
                        }
                    }
                }
            });
            navView.setId("folderNavigationTreeView");
            navView.setPrefWidth(215);
            navView.setPrefHeight(265);
            folderNavigationTreeView = navView;
            List<Node> navViews = sidebar.getChildren()
                    .stream()
                    .filter(o -> o.getId().equals("folderNavigationTreeView"))
                    .collect(Collectors.toList());
            if(navViews.isEmpty()) {
                sidebar.getChildren().add(folderNavigationTreeView);
                PasswordManagerApp.getPasswordHomeController().setSidebar(sidebar);
            } else {
                folderNavigationTreeView.getRoot().setExpanded(true);
                sidebar.getChildren().set(7, folderNavigationTreeView);
                PasswordManagerApp.getPasswordHomeController().setSidebar(sidebar);
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

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

    @FXML
    private void addNewPasswordFolder() throws LoginException, IOException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadAddPasswordFolderModal();
            logger.info("Switched context to AddFolderController");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @FXML
    private void deletePasswordFolder() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadDeletePasswordFolderModal();
            logger.info("Switched context to DeleteFolderController");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @FXML
    private void backToHome() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            PasswordManagerApp.loadPasswordHomeView();
            logger.info("Reloaded home page from another context.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @FXML
    private void preferences() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            PasswordManagerApp.loadPreferencesView();
            logger.info("Switched context to PreferencesController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process");
        }
    }

    @FXML
    public void deletePasswords() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            List<Object> passwordsToBeDeleted = new ArrayList<>();
            TableView<Object> tableView =
                    (TableView<Object>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
            for(Object o : tableView.getItems()){
                try {
                    Class<?> websitePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper");
                    Class<?> databasePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper");
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

    @FXML
    private void confirmDelete() throws LoginException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Class<?> websitePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            Class<?> databasePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
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
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @FXML
    private void cancelDelete() {
        getStage().close();
    }

    @FXML
    public void logout() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadLogoutModal();
            logger.info("Switched context to LogoutController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @FXML
    public void addPassword() throws LoginException, IOException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadAddPasswordModal();
            logger.info("Switched context to the AddPasswordController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

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
                TreeViewIteratorUtil<String> treeViewIteratorUtil = new TreeViewIteratorUtil<>(folderNavigationTreeView.getRoot());
                TreeItem<String> childNode = new TreeItem<>();
                while(treeViewIteratorUtil.hasNext()) {
                    TreeItem<String> currentNode = treeViewIteratorUtil.next();
                    System.out.println("Current node value: " + currentNode.getValue());
                    System.out.println("Selected folder: " + selectedFolder.getPasswordFolder());
                    if(currentNode.getValue().equals(selectedFolder.getPasswordFolder())) {
                        childNode = currentNode;
                        break;
                    }
                }
                childNode.getParent().setExpanded(true);
                folderNavigationTreeView.getSelectionModel().select(childNode);

                Class<?> websitePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
                Class<?> databasePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
                if(websitePasswordEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateWebsiteEntryPasswords(selectedFolder);
                    TableView<WebsitePasswordEntryWrapper> tableView = (TableView<WebsitePasswordEntryWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                } if(databasePasswordEntryClass.equals(PasswordEntryFolder.EntryFactory.determineEntryType(selectedFolder))) {
                    populateDatabaseEntryPasswords(selectedFolder);
                    TableView<DatabasePasswordEntryWrapper> tableView = (TableView<DatabasePasswordEntryWrapper>)
                            PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().get(1);
                    tableView.getSelectionModel().select(tableView.getItems().size() - 1, tableView.getColumns().get(1));
                }
            }
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public VBox getSidebar() {
        return sidebar;
    }

    public void setSidebar(VBox sidebar) {
        this.sidebar = sidebar;
    }

    public List<PasswordEntryFolder> getPasswordEntryFolders() {
        return passwordEntryFolders;
    }

    public void setPasswordEntryFolders(List<PasswordEntryFolder> passwordEntryFolders) {
        this.passwordEntryFolders = passwordEntryFolders;
    }

    public BaseAddPasswordController getBaseAddPasswordController() {
        return baseAddPasswordController;
    }

    public void setBaseAddPasswordController(BaseAddPasswordController baseAddPasswordController) {
        this.baseAddPasswordController = baseAddPasswordController;
    }

    public PasswordEntryFolder getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(PasswordEntryFolder folder) {
        selectedFolder = folder;
    }
}
