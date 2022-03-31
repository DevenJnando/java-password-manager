package com.jamesd.passwordmanager.Tables;

import com.jamesd.passwordmanager.Controllers.BaseDetailsController;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    private TableView<WebsitePasswordEntryWrapper> passwordTableView = new TableView<>();

    private static Logger logger = LoggerFactory.getLogger(WebsitePasswordTable.class);

    /**
     * Default constructor
     */
    public WebsitePasswordTable() {

    }

    /**
     * Leverages clearbit to check if a URL has a valid logo behind it or not
     * @param protocol Protocol to use
     * @param url WebsitePasswordEntry object's URL
     * @return Boolean true if logo exists, else false
     */
    public Boolean getUrlFavicon(String protocol, String url) {
        try {
            URL faviconUrl = new URL(protocol, "logo.clearbit.com", "/" + url);
            new BufferedInputStream(faviconUrl.openStream());
            return true;
        } catch (IOException e) {
            logger.error("No logo found for provided url: " + url);
            return false;
        }
    }

    /**
     * Checks if a logo can be found for a WebsitePasswordEntry object's URL, and if it can it is downloaded and
     * assigned to the relevant WebsitePasswordEntryWrapper object's favicon field
     * @param faviconUrl WebsitePasswordEntry object's URL
     * @param filename Filename String to save the logo as
     * @return ImageView containing the logo which corresponds to the faviconUrl parameter
     */
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

    @Override
    public List<WebsitePasswordEntryWrapper> wrapPasswords(List<WebsitePasswordEntry> passwordEntries) throws MalformedURLException {
        List<WebsitePasswordEntryWrapper> passwordsWithFavicons = new ArrayList();
        for(WebsitePasswordEntry entry : passwordEntries) {
            String faviconUrl = entry.getSiteUrl();
            ImageView favicon = new ImageView();

            //Removes "www" from the URL if it exists and anything after a "/" if that exists too
            String urlToUse = faviconUrl.contains("www.") ? faviconUrl.split("www\\.")[1] : faviconUrl;
            urlToUse = urlToUse.contains("/") ? urlToUse.split("/")[0] : urlToUse;
            if(getUrlFavicon("https", urlToUse)) {

                // Uses only the domain name of the website for the filename
                String filename = urlToUse.split("\\.")[0];
                favicon = createFavicon(new URL("https", "logo.clearbit.com", "/" + urlToUse), filename);
            }
            WebsitePasswordEntryWrapper wrapper = new WebsitePasswordEntryWrapper(entry, favicon);
            passwordsWithFavicons.add(wrapper);
        }
        return passwordsWithFavicons;
    }

    @Override
    protected void loadColumns() {
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
                            BaseDetailsController controller = PasswordManagerApp.getPasswordDetailsController();
                            controller.setWebDetailsBorderPane(t1, folder);
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    logger.error("Password table not loaded. Cannot create listener.");
                }
            });
            logger.info("Successfully populated password list.");
            return this.passwordTableView;
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }
}
