package com.jamesd.passwordmanager.Tables;

import com.jamesd.passwordmanager.Controllers.BaseDetailsController;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.FaviconBuilder;
import com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper;
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
 * Class which builds and returns a TableView object containing CreditDebitCardWrapper objects
 */
public class CreditDebitCardTable extends BasePasswordTable<CreditDebitCardEntryWrapper, CreditDebitCardEntry> {

    /**
     * FXML field
     */
    @FXML
    private final TableView<CreditDebitCardEntryWrapper> creditDebitCardTableView = new TableView<>();

    private static final Logger logger = LoggerFactory.getLogger(CreditDebitCardTable.class);

    /**
     * Default constructor
     */
    public CreditDebitCardTable() {

    }

    @Override
    protected void loadColumns() {
        TableColumn<CreditDebitCardEntryWrapper, Boolean> checkMark = new TableColumn<>("Checkbox");
        TableColumn<CreditDebitCardEntryWrapper, String> faviconCol = new TableColumn<>("");
        TableColumn<CreditDebitCardEntryWrapper, String> passwordNameCol = new TableColumn<>("Card Name");
        TableColumn<CreditDebitCardEntryWrapper, String> expiryDate = new TableColumn<>("Expiry Date");
        TableColumn<CreditDebitCardEntryWrapper, String> updateMessageCol = new TableColumn<>("");
        faviconCol.setCellValueFactory(new PropertyValueFactory<>("favicon"));
        checkMark.setCellFactory(cell -> new CheckBoxTableCell<>());
        checkMark.setCellValueFactory(o -> {
            BooleanProperty checked = o.getValue().isChecked();
            checked.addListener((observable, oldValue, newValue) -> {
                o.getValue().setChecked(newValue);
            });
            return checked;
        });
        passwordNameCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getCreditDebitCardEntry().getPasswordName()));
        expiryDate.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getCreditDebitCardEntry().getExpiryDate()));
        updateMessageCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getCreditDebitCardEntry().getNeedsUpdatedMessage()));
        updateMessageCol.setStyle("-fx-text-fill: red");
        creditDebitCardTableView.getColumns().setAll(checkMark, faviconCol, passwordNameCol, expiryDate, updateMessageCol);
    }

    @Override
    public List<CreditDebitCardEntryWrapper> wrapPasswords(List<CreditDebitCardEntry> passwordEntries) throws MalformedURLException {
        List<CreditDebitCardEntryWrapper> passwordsWithFavicons = new ArrayList<>();
        for(CreditDebitCardEntry entry : passwordEntries) {
            String cardType = entry.getCardType();
            String urlToUse = "";
            ImageView favicon = new ImageView();
            switch(cardType) {
                case "MasterCard":
                    urlToUse = "mastercard.com";
                    break;
                case "Visa":
                    urlToUse = "visa.com";
                    break;
                case "American Express":
                    urlToUse = "americanexpress.com";
                    break;
                default:
                    urlToUse = "placeholderurl.com";
                    break;
            }
            if(FaviconBuilder.getUrlFavicon("https", urlToUse)) {

                // Uses only the domain name of the website for the filename
                String filename = urlToUse.split("\\.")[0];
                favicon = FaviconBuilder.createFavicon(new URL("https", "logo.clearbit.com", "/" + urlToUse), filename);
            }
            CreditDebitCardEntryWrapper wrapper = new CreditDebitCardEntryWrapper(entry, favicon);
            passwordsWithFavicons.add(wrapper);
        }
        return passwordsWithFavicons;
    }

    @Override
    public TableView<CreditDebitCardEntryWrapper> createTableView(PasswordEntryFolder folder)
            throws LoginException, MalformedURLException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
            Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
            List<?> genericPasswordList = PasswordEntryFolder.EntryFactory.generateEntries(folder);
            List<CreditDebitCardEntry> passwordList = new ArrayList<>();
            if(passwordEntryClass.equals(classOfEntry)) {
                passwordList = (List<CreditDebitCardEntry>) genericPasswordList;
                checkIfPasswordNeedsUpdated(passwordList);
            }
            List<CreditDebitCardEntryWrapper> wrappedPasswords = wrapPasswords(passwordList);
            ObservableList<CreditDebitCardEntryWrapper> oPasswordList = FXCollections.observableList(wrappedPasswords);
            loadColumns();
            creditDebitCardTableView.setItems(oPasswordList);
            creditDebitCardTableView.setCursor(Cursor.HAND);
            creditDebitCardTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            creditDebitCardTableView.getSelectionModel().setCellSelectionEnabled(true);
            creditDebitCardTableView.setEditable(true);
            creditDebitCardTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, passwordEntry, t1) -> {
                try {
                    TablePosition position = creditDebitCardTableView.getSelectionModel().getSelectedCells().get(0);
                    TableColumn selectedColumn = position.getTableColumn();
                    if (!selectedColumn.getText().equals("Checkbox")) {
                        try {
                            if(PasswordManagerApp.getCenterSet() != 1) {
                                PasswordManagerApp.setDetailsAsCenter();
                            }
                            BaseDetailsController controller = PasswordManagerApp.getPasswordDetailsController();
                            controller.setCreditDebitCardDetailsBorderPane(t1, folder);
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    logger.error("Password table not loaded. Cannot create listener.");
                }
                creditDebitCardTableView.getSelectionModel().clearSelection();
            });
            logger.info("Successfully populated credit/debit card list.");
            return this.creditDebitCardTableView;
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }
}
