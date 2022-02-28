package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.User;
import com.jamesd.passwordmanager.Models.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.login.LoginException;

import javafx.scene.image.ImageView;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PasswordHomeController implements Initializable {

    public static Logger logger = LoggerFactory.getLogger(PasswordHomeController.class);

    @FXML
    private JFXDrawer menuDrawer = new JFXDrawer();
    @FXML
    private JFXHamburger menuHamburger = new JFXHamburger();
    @FXML
    private VBox menuContent = new VBox();
    @FXML
    private TableView<WebsitePasswordEntryWrapper> passwordTableView = new TableView();
    @FXML
    private Button addPasswordButton = new Button();
    @FXML
    private JFXButton deletePasswordsButton = new JFXButton();

    private static Stage stage;
    private static ObservableList<WebsitePasswordEntryWrapper> loadedPasswords;

    public PasswordHomeController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDeleteButtonIcon();
        try {
            populatePasswordList();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setDeleteButtonIcon() {
        Text delete = GlyphsDude.createIcon(FontAwesomeIcon.TRASH, "3.0em");
        deletePasswordsButton.setGraphic(delete);
    }

    public void onHamburgerClick() {
        if(getMenuDrawer().isOpened()){
            getMenuDrawer().close();
            logger.info("Closed menu drawer.");
        } else {
            getMenuDrawer().open();
            logger.info("Opened menu drawer");
        }
    }

    private long daysSinceLastUpdate(String passwordEntryDateSet) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateSet = LocalDate.parse(passwordEntryDateSet, formatter);
        long daysSinceLastUpdate = dateSet.until(currentDate, ChronoUnit.DAYS);
        return daysSinceLastUpdate;
    }

    private Boolean passwordNeedsUpdated(String passwordEntryDateSet) {
        Boolean needsUpdated = false;
        long daysSinceLastUpdate = daysSinceLastUpdate(passwordEntryDateSet);
        if(daysSinceLastUpdate > 182) {
            needsUpdated = true;
        }
        return needsUpdated;
    }

    private void checkIfPasswordNeedsUpdated(List<WebsitePasswordEntry> passwordEntries) {
        for(WebsitePasswordEntry entry : passwordEntries) {
            if(passwordNeedsUpdated(entry.getDateSet())) {
                long daysOutOfDate = daysSinceLastUpdate(entry.getDateSet());

                String outOfDateMessage = daysOutOfDate > 1
                        ? "Password is " + daysOutOfDate + " days out of date!"
                        : "Password is " + daysOutOfDate + " day out of date!";
                entry.setNeedsUpdatedMessage(outOfDateMessage);
            }
        }
    }

    private void loadAddPasswordModal() throws IOException, LoginException {
        Stage addPasswordStage = new Stage();
        FXMLLoader addPasswordLoader = new FXMLLoader(AddPasswordController.class.getResource("/com/jamesd/passwordmanager/views/add-password-modal.fxml"));
        AnchorPane addPasswordPane = addPasswordLoader.load();
        Scene addPasswordScene = new Scene(addPasswordPane);
        addPasswordStage.setScene(addPasswordScene);
        addPasswordStage.setTitle("Add New Password");
        addPasswordStage.initOwner(PasswordManagerApp.getMainStage());
        addPasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = addPasswordStage;
        stage.showAndWait();
        PasswordManagerApp.getPasswordHomeController().populatePasswordList();
        PasswordManagerApp.getPasswordHomeController().passwordTableView.refresh();
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

    public List<WebsitePasswordEntryWrapper> wrapPasswords(List<WebsitePasswordEntry> passwordEntries) throws MalformedURLException {
        List<WebsitePasswordEntryWrapper> passwordsWithFavicons = new ArrayList();
        for(WebsitePasswordEntry entry : passwordEntries) {
            String faviconUrl = entry.getSiteUrl();
            ImageView favicon = new ImageView();
            String urlToUse = faviconUrl.contains("www.") ? faviconUrl.split("www\\.")[1] : faviconUrl;
            urlToUse = urlToUse.contains("/") ? urlToUse.split("/")[0] : urlToUse;
            if(getUrlFavicon("https", urlToUse)) {
                String filename = urlToUse.split("\\.")[0];
                favicon = createFavicon(new URL("https", "logo.clearbit.com", "/" + urlToUse), filename);
            }
            WebsitePasswordEntryWrapper wrapper = new WebsitePasswordEntryWrapper(entry, favicon);
            passwordsWithFavicons.add(wrapper);
        }
        return passwordsWithFavicons;
    }

    public Boolean getUrlFavicon(String protocol, String url) {
        try {
            URL faviconUrl = new URL(protocol, "logo.clearbit.com", "/" + url);
            new BufferedInputStream(faviconUrl.openStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ImageView createFavicon(URL faviconUrl, String filename) {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(faviconUrl.openStream());
            File outputFile = new File("src/main/resources/com/jamesd/passwordmanager/icons/favicons/" + filename + ".png");
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                outputStream.write(data, 0, byteContent);
            }
            outputStream.flush();
            outputStream.close();
            Image image = new Image(new FileInputStream("src/main/resources/com/jamesd/passwordmanager/icons/favicons/" + filename + ".png"));
            ImageView favicon = new ImageView(image);
            favicon.setFitHeight(32);
            favicon.setFitWidth(32);
            return favicon;
        } catch(IOException e) {
            ImageView favicon = new ImageView();
            e.printStackTrace();
            return favicon;
        }
    }

    private void loadColumns() {
        TableColumn<WebsitePasswordEntryWrapper, Boolean> checkMark = new TableColumn("Checkbox");
        TableColumn<WebsitePasswordEntryWrapper, String> faviconCol = new TableColumn("");
        TableColumn<WebsitePasswordEntryWrapper, String> passwordNameCol = new TableColumn("Password Name");
        TableColumn<WebsitePasswordEntryWrapper, String> siteUrlCol = new TableColumn("Website URL");
        TableColumn<WebsitePasswordEntryWrapper, String> passwordUsernameCol = new TableColumn("Username");
        TableColumn<WebsitePasswordEntryWrapper, String> dateSetCol = new TableColumn("Last Updated On");
        TableColumn<WebsitePasswordEntryWrapper, String> updateMessageCol = new TableColumn("");
        faviconCol.setCellValueFactory(new PropertyValueFactory<>("favicon"));
        checkMark.setCellFactory(cell -> new CheckBoxTableCell<>());
        checkMark.setCellValueFactory(o -> {
            BooleanProperty checked = o.getValue().isChecked();
            checked.addListener((observable, oldValue, newValue) -> {
                o.getValue().setChecked(newValue);
            });
            return checked;
        });
        passwordNameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getWebsitePasswordEntry().getPasswordName()));
        siteUrlCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getWebsitePasswordEntry().getSiteUrl()));
        passwordUsernameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getWebsitePasswordEntry().getPasswordUsername()));
        dateSetCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getWebsitePasswordEntry().getDateSet()));
        updateMessageCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getWebsitePasswordEntry().getNeedsUpdatedMessage()));
        passwordTableView.getColumns().setAll(checkMark, faviconCol, passwordNameCol, siteUrlCol, passwordUsernameCol, dateSetCol, updateMessageCol);
    }

    public void populatePasswordList() throws LoginException, MalformedURLException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            if(loadedPasswords != null) {
                loadColumns();
                passwordTableView.setItems(loadedPasswords);
            } else {
                User user = PasswordManagerApp.getLoggedInUser();
                MasterSQLQueries.close();
                List<WebsitePasswordEntry> passwordList = StoredPassSQLQueries.queryPasswordsContainerByUsername(user.getUsername());
                checkIfPasswordNeedsUpdated(passwordList);
                List<WebsitePasswordEntryWrapper> wrappedPasswords = wrapPasswords(passwordList);
                ObservableList<WebsitePasswordEntryWrapper> oPasswordList = FXCollections.observableList(wrappedPasswords);
                loadColumns();
                loadedPasswords = oPasswordList;
                passwordTableView.setItems(loadedPasswords);
            }
            passwordTableView.setCursor(Cursor.HAND);
            passwordTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            passwordTableView.getSelectionModel().setCellSelectionEnabled(true);
            passwordTableView.setEditable(true);
            passwordTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, passwordEntry, t1) -> {
                try {
                    TablePosition position = passwordTableView.getSelectionModel().getSelectedCells().get(0);
                    TableColumn selectedColumn = position.getTableColumn();
                    if (!selectedColumn.getText().equals("Checkbox")) {
                        try {
                            PasswordManagerApp.loadPasswordDetailsView(t1);
                        } catch (IOException
                                | InvalidAlgorithmParameterException
                                | LoginException
                                | NoSuchPaddingException
                                | IllegalBlockSizeException
                                | NoSuchAlgorithmException
                                | BadPaddingException
                                | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    logger.error("Password table not loaded. Cannot create listener.");
                }
            });
            logger.info("Successfully populated password list.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }


    public void deletePasswords() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            List passwordsToBeDeleted = loadedPasswords.stream().filter(o -> o.isChecked().getValue()).collect(Collectors.toList());
            if(!passwordsToBeDeleted.isEmpty()) {
                loadDeletePasswordModal();
                logger.info("Loaded delete passwords modal.");
            }
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    @FXML
    private void confirmDelete() throws MalformedURLException, LoginException {
        DeletePasswordController deletePasswordController = new DeletePasswordController();
        deletePasswordController.deleteMultipleEntries(passwordTableView);
    }

    @FXML
    private void cancelDelete() {
        getStage().close();
    }

    public void logout() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadLogoutModal();
            logger.info("Switched context to LogoutController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    public void addPassword() throws LoginException, IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadAddPasswordModal();
            logger.info("Switched context to the AddPasswordController.");
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }

    public JFXDrawer getMenuDrawer() {
        return this.menuDrawer;
    }

    public VBox getMenuContent() {
        return this.menuContent;
    }

    public static Stage getStage() {
        return stage;
    }

    public static ObservableList<WebsitePasswordEntryWrapper> getLoadedPasswords() {
        return loadedPasswords;
    }

    public static void setLoadedPasswords(ObservableList<WebsitePasswordEntryWrapper> loadedPasswords) {
        PasswordHomeController.loadedPasswords = loadedPasswords;
    }
}
