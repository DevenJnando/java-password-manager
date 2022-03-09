package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Users.User;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Tables.WebsitePasswordTable;
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
    private TableView<WebsitePasswordEntryWrapper> passwordTableView = new TableView();
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
    private static List<PasswordEntryFolder> passwordEntryFolders;
    public static BaseAddPasswordController baseAddPasswordController = new BaseAddPasswordController();

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
        passwordFolderNavigationLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        sidebar.getChildren().add(passwordFolderNavigationLabel);
        try {
            populatePasswordFolders();
        } catch (LoginException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        sidebar.getChildren().add(folderNavigationTreeView);
    }

    public void setTitlePane() {
        VBox passwordFolderSettingsVbox = new VBox();
        JFXButton addNewPasswordFolderButton = new JFXButton("Add Folder");
        addNewPasswordFolderButton.setOnAction(e -> {
            try {
                addNewPasswordFolder();
            } catch (LoginException | IOException ex) {
                ex.printStackTrace();
            }
        });
        JFXButton removePasswordFolderButton = new JFXButton("Remove Folder");
        addNewPasswordFolderButton.setPrefHeight(25);
        addNewPasswordFolderButton.setPrefWidth(200);
        removePasswordFolderButton.setPrefHeight(25);
        removePasswordFolderButton.setPrefWidth(200);
        passwordFolderSettingsVbox.getChildren().add(addNewPasswordFolderButton);
        passwordFolderSettingsVbox.getChildren().add(removePasswordFolderButton);
        TitledPane passwordFolderSettingsTitlePane = new TitledPane("Password Folder Settings", passwordFolderSettingsVbox);
        passwordFolderSettingsTitlePane.setExpanded(false);
        folderMenuTitlePane = passwordFolderSettingsTitlePane;
    }

    public VBox getPasswordTableVbox() {
        return this.passwordTableVbox;
    }

    public void setImageView() throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/main/resources/com/jamesd/passwordmanager/icons/1password-logo-round.png"));
        ImageView logo = new ImageView(image);
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
        homeButton.setPrefHeight(35);
        homeButton.setPrefWidth(215);
        addPasswordButton.setPrefHeight(35);
        addPasswordButton.setPrefWidth(215);
        userPreferencesButton.setPrefHeight(35);
        userPreferencesButton.setPrefWidth(215);
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
            } catch (LoginException | IOException ex) {
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

    private void loadAddPasswordFolderModal() throws IOException {
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
        setBaseAddPasswordController(addPasswordLoader.getController());
        Scene addPasswordScene = new Scene(addPasswordPane);
        addPasswordStage.setScene(addPasswordScene);
        addPasswordStage.setTitle("Add New Password");
        addPasswordStage.initOwner(PasswordManagerApp.getMainStage());
        addPasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = addPasswordStage;
        stage.showAndWait();
        //PasswordManagerApp.getPasswordHomeController().populatePasswordList();
        //PasswordManagerApp.getPasswordHomeController().passwordTableView.refresh();
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
            Class<?> websitePasswordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            TreeItem<String> folderNavigationRoot = new TreeItem<>("Root Folder");
            User user = PasswordManagerApp.getLoggedInUser();
            List<PasswordEntryFolder> passwordEntryFolders = StoredPassSQLQueries.queryPasswordFolderContainerByUsername(user.getUsername());
            PasswordHomeController.passwordEntryFolders = passwordEntryFolders;
            for(PasswordEntryFolder folder : passwordEntryFolders) {
                TreeItem<String> folderLeaf = new TreeItem<>(folder.getPasswordFolder(), GlyphsDude.createIcon(FontAwesomeIcon.FOLDER, "1.5em"));
                Boolean set = false;
                for (TreeItem<String> folderNode : folderNavigationRoot.getChildren()) {
                    if(folderNode.getValue().contentEquals(folder.getPasswordType())) {
                        folderNode.getChildren().add(folderLeaf);
                        set = true;
                        break;
                    }
                }
                if(!set) {
                    String folderType = "";
                    Text icon = null;
                    Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
                    if(websitePasswordEntryClass.equals(classOfEntry)) {
                        folderType = "Website Passwords";
                        icon = GlyphsDude.createIcon(FontAwesomeIcon.INTERNET_EXPLORER, "1.5em");
                    }
                    TreeItem<String> folderNode = new TreeItem<>(folderType, icon);
                    folderNavigationRoot.getChildren().add(folderNode);
                    folderNode.getChildren().add(folderLeaf);
                }
            }
            TreeView<String> navView = new TreeView<>(folderNavigationRoot);
            navView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                if(newValue.isLeaf()) {
                    for(PasswordEntryFolder folder : passwordEntryFolders) {
                        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
                        if(folder.getPasswordFolder().equals(newValue.getValue())) {
                            if(websitePasswordEntryClass.equals(classOfEntry)) {
                                populateWebsiteEntryPasswords(folder);
                            }
                        }
                    }
                }
            });
            navView.setPrefWidth(215);
            navView.setPrefHeight(265);
            folderNavigationTreeView = navView;
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    private void populateWebsiteEntryPasswords(PasswordEntryFolder folder) {
        WebsitePasswordTable websitePasswordTable = new WebsitePasswordTable();
        try {
            List<Node> tableViewList = PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren()
                    .stream()
                    .filter(o -> o.getId()
                            .equals("websiteEntryTableView"))
                    .collect(Collectors.toList());
            TableView<WebsitePasswordEntryWrapper> websitePasswordEntryTableView =
                    websitePasswordTable.createTableView(folder);
            websitePasswordEntryTableView.setId("websiteEntryTableView");
            websitePasswordEntryTableView.setMinHeight(210.0);
            websitePasswordEntryTableView.setPrefHeight(210.0);
            websitePasswordEntryTableView.setPrefWidth(1085.0);
            websitePasswordEntryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if(!tableViewList.isEmpty()) {
                passwordTableView = websitePasswordEntryTableView;
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().set(1, passwordTableView);
            } else {
                passwordTableView = websitePasswordEntryTableView;
                PasswordManagerApp.getPasswordHomeController().getPasswordTableVbox().getChildren().add(passwordTableView);
            }
        } catch (MalformedURLException
                | LoginException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addNewPasswordFolder() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadAddPasswordFolderModal();
            logger.info("Switched context to AddFolderController");
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
        /*
        if(PasswordManagerApp.getLoggedInUser() != null) {
            List<Object> passwordsToBeDeleted = List.of();
            for(Object o : loadedPasswords){
                try {
                    Class websitePasswordEntryWrapperClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntryWrapper");
                    if(websitePasswordEntryWrapperClass.equals(o)) {
                        WebsitePasswordEntryWrapper websitePasswordEntryWrapper = (WebsitePasswordEntryWrapper) o;
                        if(websitePasswordEntryWrapper.isChecked().getValue()) {
                            passwordsToBeDeleted.add(websitePasswordEntryWrapper);
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
         */
    }

    @FXML
    private void confirmDelete() throws IOException, LoginException {
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteMultipleEntries(passwordTableView);
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
    public void addPassword() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadAddPasswordModal();
            logger.info("Switched context to the AddPasswordController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static List<PasswordEntryFolder> getPasswordEntryFolders() {
        return passwordEntryFolders;
    }

    public static void setPasswordEntryFolders(List<PasswordEntryFolder> passwordEntryFolders) {
        PasswordHomeController.passwordEntryFolders = passwordEntryFolders;
    }

    public static BaseAddPasswordController getBaseAddPasswordController() {
        return baseAddPasswordController;
    }

    public static void setBaseAddPasswordController(BaseAddPasswordController baseAddPasswordController) {
        PasswordHomeController.baseAddPasswordController = baseAddPasswordController;
    }
}
