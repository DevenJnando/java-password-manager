package com.jamesd.passwordmanager.Controllers;

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

public class Toggler {

    private final String hidePasswordTextFieldId;
    private final String showPasswordTextFieldId;
    private final List<String> paneSubclasses = new ArrayList<>();
    private Boolean showPassword;
    private static Logger logger = LoggerFactory.getLogger(Toggler.class);

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

    public Object togglePassword(Node node)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> cls = obtainClassType(node);
        if(cls != null) {
            Method getChildren = cls.getMethod("getChildren");
            if (showPassword) {
                List<Node> filteredChildren = ((List<Node>) getChildren.invoke(node)).stream()
                        .filter(o -> o.getId().equals(getShowPasswordText()))
                        .collect(Collectors.toList());
                if (!filteredChildren.isEmpty()) {
                    CustomTextField toBeRemoved = (CustomTextField) filteredChildren.get(0);
                    CustomPasswordField passwordHide = new CustomPasswordField();
                    passwordHide.setEditable(true);
                    passwordHide.setText(toBeRemoved.getText());
                    passwordHide.setId(hidePasswordTextFieldId);
                    showPassword = false;
                    return passwordHide;
                }
            } else {
                List<Node> filteredChildren = ((List<Node>) getChildren.invoke(node)).stream()
                        .filter(o -> o.getId().equals(getHidePasswordText()))
                        .collect(Collectors.toList());
                if (!filteredChildren.isEmpty()) {
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
        return null;
    }

    public <T> Object togglePassword(Node node, T wrapper)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> cls = obtainClassType(node);
        if(cls != null) {
            Method getChildren = cls.getMethod("getChildren");
            if (showPassword) {
                List<Node> filteredChildren = ((List<Node>) getChildren.invoke(node)).stream()
                        .filter(o -> o.getId().equals(getShowPasswordText()))
                        .collect(Collectors.toList());
                if (!filteredChildren.isEmpty()) {
                    CustomPasswordField passwordHide = new CustomPasswordField();
                    passwordHide.setEditable(true);
                    if(wrapper instanceof WebsitePasswordEntryWrapper) {
                        WebsitePasswordEntryWrapper websiteWrapper = (WebsitePasswordEntryWrapper) wrapper;
                        passwordHide.setText(websiteWrapper.getWebsitePasswordEntry().getDecryptedPassword());
                    } if(wrapper instanceof DatabasePasswordEntryWrapper) {
                        DatabasePasswordEntryWrapper databaseWrapper = (DatabasePasswordEntryWrapper) wrapper;
                        passwordHide.setText(databaseWrapper.getDatabasePasswordEntry().getDecryptedPassword());
                    }
                    passwordHide.setId(hidePasswordTextFieldId);
                    showPassword = false;
                    return passwordHide;
                }
            } else {
                List<Node> filteredChildren = ((List<Node>) getChildren.invoke(node)).stream()
                        .filter(o -> o.getId().equals(getHidePasswordText()))
                        .collect(Collectors.toList());
                if (!filteredChildren.isEmpty()) {
                    CustomTextField passwordShow = new CustomTextField();
                    passwordShow.setEditable(true);
                    if(wrapper instanceof WebsitePasswordEntryWrapper) {
                        WebsitePasswordEntryWrapper websiteWrapper = (WebsitePasswordEntryWrapper) wrapper;
                        passwordShow.setText(websiteWrapper.getWebsitePasswordEntry().getDecryptedPassword());
                    } if(wrapper instanceof DatabasePasswordEntryWrapper) {
                        DatabasePasswordEntryWrapper databaseWrapper = (DatabasePasswordEntryWrapper) wrapper;
                        passwordShow.setText(databaseWrapper.getDatabasePasswordEntry().getDecryptedPassword());
                    }
                    passwordShow.setId(showPasswordTextFieldId);
                    showPassword = true;
                    return passwordShow;
                }
            }
        }
        return null;
    }

    public String getHidePasswordText() {
        return hidePasswordTextFieldId;
    }

    public String getShowPasswordText() {
        return showPasswordTextFieldId;
    }

    public Boolean getShowPassword() {
        return showPassword;
    }
}
