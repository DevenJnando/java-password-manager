package com.jamesd.passwordmanager.Controllers;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * Abstract class containing common methods for checking any erroneous fields
 */
public abstract class ErrorChecker {

    private static Logger logger = LoggerFactory.getLogger(ErrorChecker.class);

    /**
     * Default constructor
     */
    public ErrorChecker() {

    }

    /**
     * Resets all error message labels and all error flags.
     */
    protected abstract void checkAndResetLabels();

    /**
     * Establishes whether there are any errors in the user's input
     * fields. If a folder has not been selected, any fields have been left blank, or the "enter password" field does not
     * match the "confirm password" field, then the erroneousFields flag will be set to true.
     * @return Returns true if any errors have been found, and false if no errors are found
     */
    protected abstract Boolean hasErroneousFields();

    /**
     * Generic method which searches a VBox object for a child node of a given ID. The node is returned upon retrieval.
     * @param id ID of the node to be retrieved
     * @param passwordVbox VBox which contains the child node
     * @param <T> Object type of the node within the VBox
     * @return Returns the node upon successful retrieval and a null value upon retrieval failure
     */
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

    /**
     * Generic method used to reset an error label within a VBox of a given ID.
     * @param id ID of the error label
     * @param passwordVbox VBox which contains the label
     * @param <T> Object type of the node within the VBox
     */
    public <T> void resetLabel(String id, VBox passwordVbox) {
        T toRemove = retrieveNode(id, passwordVbox);
        passwordVbox.getChildren().remove(toRemove);
    }

    /**
     * Sets an error label with a user given ID and error message.
     * @param id ID of the error label
     * @param errorMessage Message to be displayed by the error label
     * @param passwordVbox VBox where the label will be added
     */
    public void setErrorLabel(String id, String errorMessage, VBox passwordVbox) {
        Label errorLabel = new Label();
        errorLabel.setId(id);
        errorLabel.setText(errorMessage);
        errorLabel.setTextFill(Color.RED);
        passwordVbox.getChildren().add(errorLabel);
    }
}
