package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.TransitionUtil;
import com.jfoenix.controls.JFXDrawer;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller responsible for performing user preferences actions
 */
public class PreferencesController implements Initializable {

    /**
     * FXML fields
     */
    @FXML
    ChoiceBox<String> reminderChoiceBox = new ChoiceBox<>();
    @FXML
    Label saveReminderLabel = new Label();
    @FXML
    Label saveTwoFactorLabel = new Label();
    @FXML
    Label saveUpdatePasswordLabel = new Label();
    @FXML
    private JFXDrawer menuDrawer = new JFXDrawer();

    /**
     * Stage for modals
     */
    private static Stage stage;

    private Logger logger = LoggerFactory.getLogger(PreferencesController.class);

    /**
     * Default constructor
     */
    public PreferencesController() {

    }

    /**
     * Initialize method which populates the update reminder ChoiceBox
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeChoiceBox();
    }

    /**
     * Retrieves the Stage object
     * @return Stage containing a modal
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Loads the "change master password" modal and switches context to the MasterPasswordController
     * @throws IOException Throws IOException if the "change master password" view cannot be loaded
     */
    public void loadChangeMasterPasswordView() throws IOException {
        Stage changeMasterPasswordStage = new Stage();
        FXMLLoader changeMasterPasswordLoader = new FXMLLoader
                (MasterPasswordController.class
                        .getResource("/com/jamesd/passwordmanager/views/update-master-password-modal.fxml"));
        AnchorPane changeMasterPasswordPane = changeMasterPasswordLoader.load();
        Scene changeMasterPasswordScene = new Scene(changeMasterPasswordPane);
        changeMasterPasswordStage.setScene(changeMasterPasswordScene);
        changeMasterPasswordStage.setTitle("Update Master Password");
        changeMasterPasswordStage.initOwner(PasswordManagerApp.getMainStage());
        changeMasterPasswordStage.initModality(Modality.APPLICATION_MODAL);
        stage = changeMasterPasswordStage;
        stage.showAndWait();
    }

    /**
     * Populates the update reminder ChoiceBox object with 3 options:
     * issue reminder after 1 month
     * issue reminder after 6 months
     * issue reminder after 1 year
     */
    public void initializeChoiceBox() {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            ArrayList<String> options = new ArrayList<>();
            options.add("1 month");
            options.add("6 months");
            options.add("1 year");
            ObservableList<String> obsOptions = FXCollections.observableList(options);
            reminderChoiceBox.setItems(obsOptions);
            reminderChoiceBox.getSelectionModel().select(PasswordManagerApp.getLoggedInUser().getReminderTimePeriod());
            reminderChoiceBox.selectionModelProperty().addListener((obj, oldValue, newValue) -> {
                PasswordManagerApp.getLoggedInUser().setReminderTimePeriod(newValue.getSelectedItem());
            });
        } else {
            logger.error("User is not logged in. Aborting operation.");
        }
    }

    /**
     * Displays a message to the user to let them know they have saved their settings successfully in the form of a
     * label. This label is shown for 2 seconds and then fades
     * @param savedLabel Label displaying "Saved!" to the user
     */
    public void showSavedLabel(Label savedLabel) {
        FadeTransition fader = TransitionUtil.createFader(savedLabel);
        SequentialTransition fade = new SequentialTransition(
                savedLabel,
                fader
        );
        savedLabel.setText("Saved!");
        savedLabel.setTextFill(Color.GREEN);
        fade.play();
    }

    /**
     * Saves the update reminder settings to the master database and feeds a successful operation back to the user
     * @throws IOException Throws IOException if a connection to the master database cannot be established
     */
    public void saveReminderSettings() throws IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            MasterSQLQueries.initialiseUsers();
            MasterSQLQueries.updateUserInDb(PasswordManagerApp.getLoggedInUser());
            MasterSQLQueries.close();
            showSavedLabel(saveReminderLabel);
        } else {
            logger.error("User is not logged in. Aborting operation.");
        }
    }

    /**
     * Triggered by the "update master password" button. Calls the method to load the "update master password" modal
     * @throws IOException Throws IOException if the "update master password" view cannot be loaded
     */
    public void updateMasterPassword() throws IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadChangeMasterPasswordView();
            logger.info("Switched context to MasterPasswordController");
        } else {
            logger.error("User is not logged in. Aborting operation");
        }
    }
}
