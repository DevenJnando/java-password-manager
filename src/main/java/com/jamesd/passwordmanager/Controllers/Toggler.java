package com.jamesd.passwordmanager.Controllers;

import com.jamesd.passwordmanager.Utils.EncryptDecryptPasswordsUtil;
import com.jamesd.passwordmanager.Wrappers.CreditDebitCardEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.DatabasePasswordEntryWrapper;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import javafx.scene.Node;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class responsible for toggling passwords from hidden to visible and vice versa
 */
public class Toggler {

    private final String hidePasswordTextFieldId;
    private final String showPasswordTextFieldId;
    private final List<String> paneSubclasses = new ArrayList<>();
    private Boolean showPassword;
    private static Logger logger = LoggerFactory.getLogger(Toggler.class);

    /**
     * Constructor which takes the hidden password ID and visible password ID as arguments. Sets the IDs of each
     * and populates the List of all usable Pane subclasses
     * @param hidePasswordId ID of the hidden password TextField object
     * @param showPasswordId ID of the visible password TextField object
     */
    public Toggler(String hidePasswordId, String showPasswordId) {
        showPassword = false;
        String[] subclasses = {
                "javafx.scene.layout.AnchorPane",
                "javafx.scene.layout.BorderPane",
                "javafx.scene.control.DialogPane",
                "javafx.scene.layout.FlowPane",
                "javafx.scene.layout.GridPane",
                "javafx.scene.layout.HBox",
                "javafx.scene.layout.StackPane",
                "javafx.scene.text.TextFlow",
                "javafx.scene.layout.TilePane",
                "javafx.scene.layout.VBox"
        };
        paneSubclasses.addAll(List.of(subclasses));
        this.hidePasswordTextFieldId = hidePasswordId;
        this.showPasswordTextFieldId = showPasswordId;
    }

    /**
     * Reflection method which determines the class type of the Node argument. If the Node is one of the Pane
     * subclasses, then that class is returned. Otherwise, a null value is returned
     * @param node Node object (should be a subclass of Pane)
     * @return Returns either the class of the Node object, or null if the Node is not a subclass of Pane
     */
    private Class<?> obtainClassType(Node node) {
        for(String className : paneSubclasses) {
            try {
                Class<?> cls = Class.forName(className);
                if(cls.isInstance(node)) {
                    return cls;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Reflectively toggles the password from hidden to visible and vice versa. Takes only a Node object as an argument
     * @param node Node object containing the hidden/visible password TextField object
     * @return Returns either a CustomTextField or a CustomPasswordField object which depends on whether the password is
     * to be visible or hidden. Returns null if the Node object is not a subclass of Pane
     * @throws NoSuchMethodException Throws NoSuchMethodException if the Node object has no "getChildren" method
     * @throws InvocationTargetException Throws InvocationTargetException if the "getChildren" method cannot be invoked
     * on the Node object
     * @throws IllegalAccessException Throws IllegalAccessException if the "getChildren" method does not have access to
     * the Node object's class
     */
    public Object togglePassword(Node node)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        // Reflectively obtains the Node object's class
        Class<?> cls = obtainClassType(node);
        if(cls != null) {
            Method getChildren = cls.getMethod("getChildren");

            // Block to execute if the password is currently visible
            if (showPassword) {
                List<Node> filteredChildren = ((List<Node>) getChildren.invoke(node)).stream()
                        .filter(o -> o.getId().equals(getShowPasswordText()))
                        .collect(Collectors.toList());
                if (!filteredChildren.isEmpty()) {
                    // Creates a hidden password CustomPasswordField object with the same text as the
                    // visible one and returns it
                    CustomTextField toBeRemoved = (CustomTextField) filteredChildren.get(0);
                    CustomPasswordField passwordHide = new CustomPasswordField();
                    passwordHide.setEditable(true);
                    passwordHide.setText(toBeRemoved.getText());
                    passwordHide.setId(hidePasswordTextFieldId);
                    showPassword = false;
                    return passwordHide;
                }
            }

            // Block to execute if the password is currently hidden
            else {
                List<Node> filteredChildren = ((List<Node>) getChildren.invoke(node)).stream()
                        .filter(o -> o.getId().equals(getHidePasswordText()))
                        .collect(Collectors.toList());
                if (!filteredChildren.isEmpty()) {
                    // Creates a visible password CustomTextField object with the same text as the
                    // hidden one and returns it
                    CustomPasswordField toBeRemoved = (CustomPasswordField) filteredChildren.get(0);
                    CustomTextField passwordShow = new CustomTextField();
                    passwordShow.setEditable(true);
                    passwordShow.setText(toBeRemoved.getText());
                    passwordShow.setId(showPasswordTextFieldId);
                    showPassword = true;
                    return passwordShow;
                }
            }
        }

        // Returns null if the Node object is not a subclass of Pane
        return null;
    }

    /**
     * Reflectively toggles the password from hidden to visible and vice versa. Takes a Node object and an object
     * which is a subclass of BaseWrapper which is used to get the password text
     * @param node Node object containing the visible/hidden password TextField
     * @param wrapper Subclass of BaseWrapper object which contains the decrypted password text
     * @param <T> The actual class type of the BaseWrapper subclass object
     * @return Returns either a CustomTextField or a CustomPasswordField object which depends on whether the password is
     * to be visible or hidden. Returns null if the Node object is not a subclass of Pane
     * @throws NoSuchMethodException Throws NoSuchMethodException if the Node object has no "getChildren" method
     * @throws InvocationTargetException Throws InvocationTargetException if the "getChildren" method cannot be invoked
     * on the Node object
     * @throws IllegalAccessException Throws IllegalAccessException if the "getChildren" method does not have access to
     * the Node object's class
     */
    public <T> Object togglePassword(Node node, T wrapper)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //Reflectively obtains the Node object's class
        Class<?> cls = obtainClassType(node);
        if(cls != null) {

            // Block to execute if the password is currently visible
            if (showPassword) {
                // Creates a hidden password CustomPasswordField object, determines the class type of the
                // BaseWrapper subclass object, retrieves the password using the appropriate method and
                // returns the new CustomPasswordField object
                CustomPasswordField passwordHide = new CustomPasswordField();
                passwordHide.setEditable(true);
                if(wrapper instanceof WebsitePasswordEntryWrapper) {
                    WebsitePasswordEntryWrapper websiteWrapper = (WebsitePasswordEntryWrapper) wrapper;
                    passwordHide.setText(websiteWrapper.getWebsitePasswordEntry().getDecryptedPassword());
                }
                if(wrapper instanceof DatabasePasswordEntryWrapper) {
                    DatabasePasswordEntryWrapper databaseWrapper = (DatabasePasswordEntryWrapper) wrapper;
                    passwordHide.setText(databaseWrapper.getDatabasePasswordEntry().getDecryptedPassword());
                }
                if(wrapper instanceof CreditDebitCardEntryWrapper) {
                    CreditDebitCardEntryWrapper creditDebitCardWrapper = (CreditDebitCardEntryWrapper) wrapper;
                    passwordHide.setText(creditDebitCardWrapper.getCreditDebitCardEntry().getDecryptedPassword());
                }
                passwordHide.setId(hidePasswordTextFieldId);
                showPassword = false;
                return passwordHide;
            }

            // Block to execute if the password is currently hidden
            else {
                // Creates a visible password CustomTextField object, determines the class type of the
                // BaseWrapper subclass object, retrieves the password using the appropriate method and
                // returns the new CustomTextField object
                CustomTextField passwordShow = new CustomTextField();
                passwordShow.setEditable(true);
                if(wrapper instanceof WebsitePasswordEntryWrapper) {
                    WebsitePasswordEntryWrapper websiteWrapper = (WebsitePasswordEntryWrapper) wrapper;
                    passwordShow.setText(websiteWrapper.getWebsitePasswordEntry().getDecryptedPassword());
                }
                if(wrapper instanceof DatabasePasswordEntryWrapper) {
                    DatabasePasswordEntryWrapper databaseWrapper = (DatabasePasswordEntryWrapper) wrapper;
                    passwordShow.setText(databaseWrapper.getDatabasePasswordEntry().getDecryptedPassword());
                }
                if(wrapper instanceof CreditDebitCardEntryWrapper) {
                    CreditDebitCardEntryWrapper creditDebitCardWrapper = (CreditDebitCardEntryWrapper) wrapper;
                    passwordShow.setText(creditDebitCardWrapper.getCreditDebitCardEntry().getDecryptedPassword());
                }
                passwordShow.setId(showPasswordTextFieldId);
                showPassword = true;
                return passwordShow;
            }
        }

        // Returns null if the Node object is not a subclass of Pane
        return null;
    }

    /**
     * Retrieves the hidden password TextField ID
     * @return ID of the hidden password TextField
     */
    public String getHidePasswordText() {
        return hidePasswordTextFieldId;
    }

    /**
     * Retrieves the visible password TextField ID
     * @return ID of the visible password TextField
     */
    public String getShowPasswordText() {
        return showPasswordTextFieldId;
    }

    /**
     * Retrieves the flag which states whether the password is currently visible or hidden
     * @return Boolean true if visible, else false
     */
    public Boolean getShowPassword() {
        return showPassword;
    }
}
