package com.jamesd.passwordmanager.Tables;

import com.jamesd.passwordmanager.Controllers.BaseDetailsController;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.DocumentEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Wrappers.DocumentWrapper;
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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentTable extends BasePasswordTable<DocumentWrapper, DocumentEntry> {

    @FXML
    private final TableView<DocumentWrapper> documentTableView = new TableView<>();

    private final String[] EXTENSIONS = {"dll", "doc", "docx", "html", "iso", "json", "log", "mov", "mp3", "mp4", "pdf",
            "png", "ppt", "sql", "txt", "wav", "wmv", "xls", "xml", "zip"};

    private static final Logger logger = LoggerFactory.getLogger(DocumentTable.class);

    @Override
    protected void loadColumns() {
        TableColumn<DocumentWrapper, Boolean> checkMark = new TableColumn<>("Checkbox");
        TableColumn<DocumentWrapper, String> faviconCol = new TableColumn<>("");
        TableColumn<DocumentWrapper, String> documentName = new TableColumn<>("Document Name");
        TableColumn<DocumentWrapper, String> documentDescription = new TableColumn<>("Document Description");
        TableColumn<DocumentWrapper, String> dateSetCol = new TableColumn<>("Last Updated On");
        faviconCol.setCellValueFactory(new PropertyValueFactory<>("favicon"));
        checkMark.setCellFactory(cell -> new CheckBoxTableCell<>());
        checkMark.setCellValueFactory(o -> {
            BooleanProperty checked = o.getValue().isChecked();
            checked.addListener((observable, oldValue, newValue) -> {
                o.getValue().setChecked(newValue);
            });
            return checked;
        });
        documentName.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getDocumentEntry().getPasswordName()));
        documentDescription.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getDocumentEntry().getDocumentDescription()));
        dateSetCol.setCellValueFactory(o -> new ReadOnlyObjectWrapper<>(o.getValue().getDocumentEntry().getDateSet()));
        documentTableView.getColumns().setAll(checkMark, faviconCol, documentName, documentDescription, dateSetCol);
    }

    @Override
    protected List<DocumentWrapper> wrapPasswords(List<DocumentEntry> documentEntries) {
        List<DocumentWrapper> wrappedDocumentPasswords = new ArrayList<>();
        for(DocumentEntry entry : documentEntries) {
            String fileExtension = entry.getPasswordName().split("\\.")[1];
            if(Arrays.stream(EXTENSIONS).anyMatch(o -> o.contentEquals(fileExtension))) {
                try {
                    Image image = new Image(new FileInputStream("src/main/resources/com/jamesd/passwordmanager/icons/extensions/" + fileExtension + ".png"));
                    ImageView icon = new ImageView(image);
                    icon.setFitHeight(32);
                    icon.setFitWidth(32);
                    DocumentWrapper documentWrapper = new DocumentWrapper(entry, icon);
                    wrappedDocumentPasswords.add(documentWrapper);
                } catch (FileNotFoundException e) {
                    logger.error("File does not exist", e);
                }
            } else {
                try {
                    Image image = new Image(new FileInputStream("src/main/resources/com/jamesd/passwordmanager/icons/extensions/unknown.png"));
                    ImageView icon = new ImageView(image);
                    icon.setFitHeight(32);
                    icon.setFitWidth(32);
                    DocumentWrapper documentWrapper = new DocumentWrapper(entry, icon);
                    wrappedDocumentPasswords.add(documentWrapper);
                } catch (FileNotFoundException e) {
                    logger.error("File does not exist", e);
                }
            }
        }
        return wrappedDocumentPasswords;
    }

    @Override
    public TableView<DocumentWrapper> createTableView(PasswordEntryFolder folder) throws LoginException, ClassNotFoundException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DocumentEntry");
            Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
            List<?> genericPasswordList = PasswordEntryFolder.EntryFactory.generateEntries(folder);
            List<DocumentEntry> passwordList = new ArrayList<>();
            if(passwordEntryClass.equals(classOfEntry)) {
                passwordList = (List<DocumentEntry>) genericPasswordList;
                checkIfPasswordNeedsUpdated(passwordList);
            }
            List<DocumentWrapper> wrappedPasswords = wrapPasswords(passwordList);
            ObservableList<DocumentWrapper> oPasswordList = FXCollections.observableList(wrappedPasswords);
            loadColumns();
            documentTableView.setItems(oPasswordList);
            documentTableView.setCursor(Cursor.HAND);
            documentTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            documentTableView.getSelectionModel().setCellSelectionEnabled(true);
            documentTableView.setEditable(true);
            documentTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, passwordEntry, t1) -> {
                try {
                    TablePosition position = documentTableView.getSelectionModel().getSelectedCells().get(0);
                    TableColumn selectedColumn = position.getTableColumn();
                    if (!selectedColumn.getText().equals("Checkbox")) {
                        try {
                            BaseDetailsController controller = PasswordManagerApp.getPasswordDetailsController();
                            controller.setDocumentDetailsBorderPane(t1, folder);
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    logger.error("Password table not loaded. Cannot create listener.");
                }
            });
            logger.info("Successfully populated document list.");
            return this.documentTableView;
        } else {
            throw new LoginException("User is not logged in. Aborting process.");
        }
    }
}
