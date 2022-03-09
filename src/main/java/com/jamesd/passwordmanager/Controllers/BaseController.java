package com.jamesd.passwordmanager.Controllers;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public abstract class BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class);


    public BaseController() {

    }

    protected abstract void checkAndResetLabels();

    protected abstract Boolean hasErroneousFields();

    protected <T> T retrieveNode(String id, VBox passwordVbox) {
        T node = null;
        try {
            node = (T) passwordVbox.getChildren()
                    .stream()
                    .filter(o -> o.getId().equals(id))
                    .collect(Collectors.toList()).get(0);
        } catch(IndexOutOfBoundsException e) {
            logger.info("Label " + id + " is not present. Nothing to do here.");
        }
        return node;
    }

    public <T> void resetLabel(String id, VBox passwordVbox) {
        T toRemove = retrieveNode(id, passwordVbox);
        passwordVbox.getChildren().remove(toRemove);
    }

    public void setErrorLabel(String id, String errorMessage, VBox passwordVbox) {
        Label errorLabel = new Label();
        errorLabel.setId(id);
        errorLabel.setText(errorMessage);
        errorLabel.setTextFill(Color.RED);
        passwordVbox.getChildren().add(errorLabel);
    }
}
