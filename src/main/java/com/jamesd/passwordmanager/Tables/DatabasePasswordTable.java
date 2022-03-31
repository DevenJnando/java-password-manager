package com.jamesd.passwordmanager.Tables;

import com.jamesd.passwordmanager.Controllers.BaseDetailsController;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which builds and returns a TableView containing DatabasePasswordEntryWrapper objects
 */
public class DatabasePasswordTable extends BasePasswordTable<DatabasePasswordEntryWrapper, DatabasePasswordEntry> {

    /**
     * FXML field
     */
    @FXML
    private TableView<DatabasePasswordEntryWrapper> passwordTableView = new TableView<>();

    private static Logger logger = LoggerFactory.getLogger(DatabasePasswordTable.class);

    /**
     * Default constructor
     */
    public DatabasePasswordTable() {

    }

    @Override
    protected void loadColumns() {
        TableColumn<DatabasePasswordEntryWrapper, Boolean> checkMark = new TableColumn("Checkbox");
        TableColumn<DatabasePasswordEntryWrapper, String> faviconCol = new TableColumn("");
        TableColumn<DatabasePasswordEntryWrapper, String> passwordNameCol = new TableColumn("Password Name");
        TableColumn<DatabasePasswordEntryWrapper, String> hostnameCol = new TableColumn("Hostname");
        TableColumn<DatabasePasswordEntryWrapper, String> databaseNameCol = new TableColumn("Database Name");
        TableColumn<DatabasePasswordEntryWrapper, String> databaseUsernameCol = new TableColumn("Database Username");
        TableColumn<DatabasePasswordEntryWrapper, String> dateSetCol = new TableColumn("Last Updated On");
        TableColumn<DatabasePasswordEntryWrapper, String> updateMessageCol = new TableColumn("");
        faviconCol.setCellValueFactory(new PropertyValueFactory<>("favicon"));
        checkMark.setCellFactory(cell -> new CheckBoxTableCell<>());
        checkMark.setCellValueFactory(o -> {
            BooleanProperty checked = o.getValue().isChecked();
            checked.addListener((observable, oldValue, newValue) -> {
                o.getValue().setChecked(newValue);
            });
            return checked;
        });
        passwordNameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getDatabasePasswordEntry().getPasswordName()));
        hostnameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getDatabasePasswordEntry().getHostName()));
        databaseNameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getDatabasePasswordEntry().getDatabaseName()));
        databaseUsernameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getDatabasePasswordEntry().getDatabaseUsername()));
        dateSetCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getDatabasePasswordEntry().getDateSet()));
        updateMessageCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper(o.getValue().getDatabasePasswordEntry().getNeedsUpdatedMessage()));
        passwordTableView.getColumns().setAll(checkMark, faviconCol, passwordNameCol, hostnameCol, databaseNameCol, databaseUsernameCol,
                dateSetCol, updateMessageCol);
    }

    @Override
    protected List<DatabasePasswordEntryWrapper> wrapPasswords(List<DatabasePasswordEntry> passwordEntries) {
        List<DatabasePasswordEntryWrapper> passwordsWithFavicons = new ArrayList();
        for(DatabasePasswordEntry entry : passwordEntries) {
            ImageView favicon = new ImageView();
            try {
                Image image = new Image(new FileInputStream("src/main/resources/com/jamesd/passwordmanager/icons/database.png"));
                favicon = new ImageView(image);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
            favicon.setFitHeight(48);
            favicon.setFitWidth(48);
            favicon.setPickOnBounds(true);
            favicon.setSmooth(false);
            DatabasePasswordEntryWrapper wrapper = new DatabasePasswordEntryWrapper(entry, favicon);
            passwordsWithFavicons.add(wrapper);
        }
        return passwordsWithFavicons;
    }

    @Override
    public TableView<DatabasePasswordEntryWrapper> createTableView(PasswordEntryFolder folder) throws MalformedURLException,
            LoginException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Class<?> databaseEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
            Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
            List<?> genericPasswordList = PasswordEntryFolder.EntryFactory.generateEntries(folder);
            List<DatabasePasswordEntry> passwordList = new ArrayList<>();
            if(databaseEntryClass.equals(classOfEntry)) {
                passwordList = (List<DatabasePasswordEntry>) genericPasswordList;
                checkIfPasswordNeedsUpdated(passwordList);
            }
            List<DatabasePasswordEntryWrapper> wrappedPasswords = wrapPasswords(passwordList);
            ObservableList<DatabasePasswordEntryWrapper> oPasswordList = FXCollections.observableList(wrappedPasswords);
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
                            controller.setDatabaseDetailsBorderPane(t1, folder);
                        } catch (IOException | GeneralSecurityException e) {
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
