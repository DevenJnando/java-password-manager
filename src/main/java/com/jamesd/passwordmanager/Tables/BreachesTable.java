package com.jamesd.passwordmanager.Tables;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Class which generates a viewable table of all password breaches and returns it to the user
 */
public class BreachesTable {

    /**
     * FXML field
     */
    @FXML
    private final TableView<Map.Entry<String, String>> breachesTableView = new TableView<>();

    private static final Logger logger = LoggerFactory.getLogger(WebsitePasswordTable.class);

    /**
     * Default constructor
     */
    public BreachesTable() {

    }

    /**
     * Loads the username/email and breached website columns and assigns them to the TableView object
     */
    public void loadColumns() {
        TableColumn<Map.Entry<String, String>, String> breachedWebsite = new TableColumn<>("Breached Website");
        TableColumn<Map.Entry<String, String>, String> usernameOrEmail = new TableColumn<>("Breached Username/Email");
        breachedWebsite.setPrefWidth(200);
        breachedWebsite.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
        usernameOrEmail.setPrefWidth(200);
        usernameOrEmail.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        breachesTableView.getColumns().setAll(usernameOrEmail, breachedWebsite);
    }

    /**
     * Creates the table of breaches for each website password. Takes a HashMap of all breaches as a parameter
     * @param breaches HashMap of all usernames which have been breached, along with the site where the breach came from
     * @return TableView of all breaches in an observable form
     */
    public TableView<Map.Entry<String, String>> createTable(HashMap<String, String> breaches) {
        loadColumns();
        ObservableList<Map.Entry<String, String>> oBreaches = FXCollections.observableArrayList(breaches.entrySet());
        breachesTableView.setItems(oBreaches);
        breachesTableView.setCursor(Cursor.HAND);
        breachesTableView.setEditable(false);
        logger.info("Created table of breaches successfully");
        return breachesTableView;
    }
}
