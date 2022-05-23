package com.jamesd.passwordmanager.Tables;

import com.jamesd.passwordmanager.Controllers.BaseDetailsController;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.FaviconBuilder;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which builds and returns a TableView object containing WebsitePasswordEntryWrapper objects
 */
public class WebsitePasswordTable extends BasePasswordTable<WebsitePasswordEntryWrapper, WebsitePasswordEntry> {

    /**
     * FXML field
     */
    @FXML
    private final TableView<WebsitePasswordEntryWrapper> passwordTableView = new TableView<>();

    private static final Logger logger = LoggerFactory.getLogger(WebsitePasswordTable.class);

    /**
     * Default constructor
     */
    public WebsitePasswordTable() {

    }

    @Override
    public List<WebsitePasswordEntryWrapper> wrapPasswords(List<WebsitePasswordEntry> passwordEntries) throws MalformedURLException {
        List<WebsitePasswordEntryWrapper> passwordsWithFavicons = new ArrayList<>();
        for(WebsitePasswordEntry entry : passwordEntries) {
            String faviconUrl = entry.getSiteUrl();
            ImageView favicon = new ImageView();

            //Removes "www" from the URL if it exists and anything after a "/" if that exists too
            String urlToUse = faviconUrl.contains("www.") ? faviconUrl.split("www\\.")[1] : faviconUrl;
            urlToUse = urlToUse.contains("/") ? urlToUse.split("/")[0] : urlToUse;
            if(FaviconBuilder.getUrlFavicon("https", urlToUse)) {

                // Uses only the domain name of the website for the filename
                String filename = urlToUse.split("\\.")[0];
                favicon = FaviconBuilder.createFavicon(new URL("https", "logo.clearbit.com", "/" + urlToUse), filename);
            }
            WebsitePasswordEntryWrapper wrapper = new WebsitePasswordEntryWrapper(entry, favicon);
            passwordsWithFavicons.add(wrapper);
        }
        return passwordsWithFavicons;
    }

    @Override
    protected void loadColumns() {
        TableColumn<WebsitePasswordEntryWrapper, Boolean> checkMark = new TableColumn<>("Checkbox");
        TableColumn<WebsitePasswordEntryWrapper, String> faviconCol = new TableColumn<>("");
        TableColumn<WebsitePasswordEntryWrapper, String> passwordNameCol = new TableColumn<>("Password Name");
        TableColumn<WebsitePasswordEntryWrapper, String> siteUrlCol = new TableColumn<>("Website URL");
        TableColumn<WebsitePasswordEntryWrapper, String> passwordUsernameCol = new TableColumn<>("Username");
        TableColumn<WebsitePasswordEntryWrapper, String> dateSetCol = new TableColumn<>("Last Updated On");
        TableColumn<WebsitePasswordEntryWrapper, String> updateMessageCol = new TableColumn<>("");
        faviconCol.setCellValueFactory(new PropertyValueFactory<>("favicon"));
        checkMark.setCellFactory(cell -> new CheckBoxTableCell<>());
        checkMark.setCellValueFactory(o -> {
            BooleanProperty checked = o.getValue().isChecked();
            checked.addListener((observable, oldValue, newValue) -> {
                o.getValue().setChecked(newValue);
            });
            return checked;
        });
        passwordNameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getWebsitePasswordEntry().getPasswordName()));
        siteUrlCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getWebsitePasswordEntry().getSiteUrl()));
        passwordUsernameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getWebsitePasswordEntry().getPasswordUsername()));
        dateSetCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getWebsitePasswordEntry().getDateSet()));
        updateMessageCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getWebsitePasswordEntry().getNeedsUpdatedMessage()));
        updateMessageCol.setStyle("-fx-text-fill: red");
        passwordTableView.getColumns().setAll(checkMark, faviconCol, passwordNameCol, siteUrlCol, passwordUsernameCol, dateSetCol, updateMessageCol);
    }

    @Override
    public TableView<WebsitePasswordEntryWrapper> createTableView(PasswordEntryFolder folder)
            throws LoginException, MalformedURLException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
            List<?> genericPasswordList = PasswordEntryFolder.EntryFactory.generateEntries(folder);
            List<WebsitePasswordEntry> passwordList = new ArrayList<>();
            if(passwordEntryClass.equals(classOfEntry)) {
                passwordList = (List<WebsitePasswordEntry>) genericPasswordList;
                checkIfPasswordNeedsUpdated(passwordList);
            }
            List<WebsitePasswordEntryWrapper> wrappedPasswords = wrapPasswords(passwordList);
            ObservableList<WebsitePasswordEntryWrapper> oPasswordList = FXCollections.observableList(wrappedPasswords);
            loadColumns();
            passwordTableView.setItems(oPasswordList);
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
                            if(PasswordManagerApp.getCenterSet() != 1) {
                                PasswordManagerApp.setDetailsAsCenter();
                            }
                            BaseDetailsController controller = PasswordManagerApp.getPasswordDetailsController();
                            controller.setWebDetailsBorderPane(t1, folder);
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    logger.error("Password table not loaded. Cannot create listener.");
                }
                passwordTableView.getSelectionModel().clearSelection();
            });
            logger.info("Successfully populated password list.");
            return this.passwordTableView;
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }
}
