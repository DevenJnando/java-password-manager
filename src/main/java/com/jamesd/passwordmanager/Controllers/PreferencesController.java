package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import com.jamesd.passwordmanager.Models.HierarchyModels.CountryCode;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PhoneNumberLocaleUtil;
import com.jamesd.passwordmanager.Utils.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
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
    ComboBox<CountryCode> countryCodeComboBox = new ComboBox<>();
    @FXML
    Label saveReminderLabel = new Label();
    @FXML
    Label saveTwoFactorLabel = new Label();
    @FXML
    Label saveUpdatePasswordLabel = new Label();
    @FXML
    Label phoneNumberLabel = new Label();
    @FXML
    Label twoFactorErrorLabel = new Label();
    @FXML
    TextField phoneNumberField = new TextField();
    @FXML
    CheckBox twoFactorEnabled = new CheckBox();

    private String selectedCountryCode;

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
     * Initialize method which populates the update reminder ChoiceBox, and sets the two-factor authentication data
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeChoiceBox();
        populateCountryCodeComboBox();
        checkBoxListener();
        twoFactorEnabled.setSelected(PasswordManagerApp.getLoggedInUser().isTwoFactorEnabled());
        phoneNumberField.setText(PasswordManagerApp.getLoggedInUser().getPhoneNumber());
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
     * Populates country code ComboBox object and adds a listener to set the country code for a user's phone number
     */
    public void populateCountryCodeComboBox() {
        countryCodeComboBox.setItems(FXCollections.observableList(PhoneNumberLocaleUtil.getPhoneNumberList()));
        countryCodeComboBox.getSelectionModel().selectedItemProperty().addListener((obj, oldVal, newVal)
                -> selectedCountryCode = newVal.getCountryCode());
    }

    /**
     * Listener which disables/enables the phone number input depending on if the user wants two-factor authentication
     * enabled or not
     */
    private void checkBoxListener() {
        twoFactorEnabled.selectedProperty().addListener((obj, oldValue, newValue) -> {
            phoneNumberField.setDisable(!newValue);
        });
    }

    /**
     * Saves the two-factor authentication settings for the currently logged-in user
     */
    @FXML
    public void saveTwoFactorSettings() {
        if(twoFactorEnabled.isSelected()) {
            if(PhoneNumberLocaleUtil.checkValidity(selectedCountryCode, phoneNumberField.getText())) {
                PasswordManagerApp.getLoggedInUser().setTwoFactorEnabled(true);
                PasswordManagerApp.getLoggedInUser().setPhoneNumber(selectedCountryCode + phoneNumberField.getText());
                MasterSQLQueries.updateUserInDb(PasswordManagerApp.getLoggedInUser());
                twoFactorErrorLabel.setText("");
                showSavedLabel(saveTwoFactorLabel);
            } else {
                twoFactorErrorLabel.setText("When enabling two-factor authentication, you must enter a valid phone number.");
                twoFactorErrorLabel.setTextFill(Color.RED);
            }
        } else {
            PasswordManagerApp.getLoggedInUser().setTwoFactorEnabled(false);
            MasterSQLQueries.updateUserInDb(PasswordManagerApp.getLoggedInUser());
            twoFactorErrorLabel.setText("");
            showSavedLabel(saveTwoFactorLabel);
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
