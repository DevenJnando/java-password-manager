package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.StoredPassSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Tables.BreachesTable;
import com.jamesd.passwordmanager.Utils.BreachChecker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller which is responsible for checking if there are any breaches in any of the currently logged-in user's
 * usernames or email addresses. Any breaches which are found are returned to the user.
 */
public class BreachCheckController implements Initializable {

    /**
     * FXML fields
     */
    @FXML
    private VBox breachesVbox = new VBox();

    private HashMap<String, String> sitesAndUsernames = new HashMap<>();

    public BreachCheckController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.sitesAndUsernames = getAllWebsitePasswords();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showNoBreachesFound() {
        Label noBreachesFoundLabel = new Label("No breaches found, you're good!");
        noBreachesFoundLabel.setPrefSize(700, 50);
        noBreachesFoundLabel.setFont(new Font(18));
        breachesVbox.getChildren().clear();
        breachesVbox.getChildren().add(noBreachesFoundLabel);
    }

    public void showBreachesFound(HashMap<String, String> breaches) {
        Label breachesFoundLabel = new Label("Breaches found! Change your password on these sites as soon as possible!");
        breachesFoundLabel.setPrefSize(700, 50);
        breachesFoundLabel.setFont(new Font(18));
        BreachesTable tableBuilder = new BreachesTable();
        TableView<Map.Entry<String, String>> breachTableView = tableBuilder.createTable(breaches);
        breachesVbox.getChildren().clear();
        breachesVbox.getChildren().add(breachesFoundLabel);
        breachesVbox.getChildren().add(breachTableView);
    }

    @FXML
    public void checkForBreaches() {
        HashMap<String, String> breaches = BreachChecker.checkForBreaches(sitesAndUsernames);
        if(breaches.isEmpty()) {
            showNoBreachesFound();
        } else {
            showBreachesFound(breaches);
        }
    }

    public HashMap<String, String> getAllWebsitePasswords() throws ClassNotFoundException {
        HashMap<String, String> sitesAndUsernames = new HashMap<>();
        List<PasswordEntryFolder> webFolders = StoredPassSQLQueries
                .queryPasswordFolderContainerByUsernameAndType
                        (PasswordManagerApp.getLoggedInUser().getUsername(), "WebPassword");
        for(PasswordEntryFolder folder : webFolders) {
            List<WebsitePasswordEntry> entries = (List<WebsitePasswordEntry>) PasswordEntryFolder.EntryFactory.generateEntries(folder);
            entries.forEach(o -> {
                String urlToUse = o.getSiteUrl().contains("www.") ? o.getSiteUrl().split("www\\.")[1] : o.getSiteUrl();
                urlToUse = urlToUse.contains("/") ? urlToUse.split("/")[0] : urlToUse;
                urlToUse = urlToUse.contains(".com") ? urlToUse.split("\\.com")[0] : urlToUse;
                sitesAndUsernames.put(urlToUse, o.getPasswordUsername());
            });
        }
        return sitesAndUsernames;
    }
}
