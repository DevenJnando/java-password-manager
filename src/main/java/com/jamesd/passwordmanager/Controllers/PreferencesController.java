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

public class PreferencesController implements Initializable {

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

    private static Stage stage;
    private Logger logger = LoggerFactory.getLogger(PreferencesController.class);

    public PreferencesController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeChoiceBox();
    }

    public static Stage getStage() {
        return stage;
    }

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

    public void updateMasterPassword() throws IOException {
        if(PasswordManagerApp.getLoggedInUser() != null) {
            loadChangeMasterPasswordView();
            logger.info("Switched context to MasterPasswordController");
        } else {
            logger.error("User is not logged in. Aborting operation");
        }
    }
}
