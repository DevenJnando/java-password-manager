package com.jamesd.passwordmanager.Controllers;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;

public abstract class NewPasswordController extends ModifyPasswordController {

    @FXML
    VBox passwordVbox = new VBox();

    public NewPasswordController() {
        super();
    }

    @Override
    public void togglePassword(Event event) {
        try {
            Class<?> customTextFieldClass = Class.forName("org.controlsfx.control.textfield.CustomTextField");
            Class<?> customPasswordFieldClass = Class.forName("org.controlsfx.control.textfield.CustomPasswordField");
            Object passwordState = passwordToggler.togglePassword(passwordVbox);
            Object confirmPasswordState = confirmPasswordToggler.togglePassword(passwordVbox);
            if(customTextFieldClass.isInstance(passwordState)
                    && customTextFieldClass.isInstance(confirmPasswordState)) {
                CustomTextField passwordShow = (CustomTextField) passwordState;
                CustomTextField confirmPasswordShow = (CustomTextField) confirmPasswordState;
                visiblePasswordField = passwordShow;
                visibleConfirmPasswordField = confirmPasswordShow;
                setTextFormatters();
                setIcons();
                attachStrengthListener("Enter Password: ");
                passwordVbox.getChildren().set(7, visiblePasswordField);
                passwordVbox.getChildren().set(9, visibleConfirmPasswordField);
            } else if(customPasswordFieldClass.isInstance(passwordState)
                    && customPasswordFieldClass.isInstance(confirmPasswordState)) {
                CustomPasswordField passwordHide = (CustomPasswordField) passwordState;
                CustomPasswordField confirmPasswordHide = (CustomPasswordField) confirmPasswordState;
                hiddenPasswordField = passwordHide;
                hiddenConfirmPasswordField = confirmPasswordHide;
                setTextFormatters();
                setIcons();
                attachStrengthListener("Enter Password: ");
                passwordVbox.getChildren().set(7, hiddenPasswordField);
                passwordVbox.getChildren().set(9, hiddenConfirmPasswordField);
            } else {
                throw new ClassCastException("Cannot cast object of type " + passwordState.getClass() + " to type " +
                        CustomTextField.class + " or type " + CustomPasswordField.class);
            }
        } catch(NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("Toggler failed...");
        }
    }

    @Override
    public void setIcons() {
        Text eye1 = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text eye2 = GlyphsDude.createIcon(FontAwesomeIcon.EYE);
        Text eyeSlash1 = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
        Text eyeSlash2 = GlyphsDude.createIcon(FontAwesomeIcon.EYE_SLASH);
        visiblePasswordField.setRight(eyeSlash1);
        visiblePasswordField.getRight().setCursor(Cursor.HAND);
        visibleConfirmPasswordField.setRight(eyeSlash2);
        visibleConfirmPasswordField.getRight().setCursor(Cursor.HAND);
        hiddenPasswordField.setRight(eye1);
        hiddenPasswordField.getRight().setCursor(Cursor.HAND);
        hiddenConfirmPasswordField.setRight(eye2);
        hiddenConfirmPasswordField.getRight().setCursor(Cursor.HAND);
        visiblePasswordField.getRight().setOnMouseClicked(this::togglePassword);
        visiblePasswordField.getRight().setOnMousePressed(this::togglePassword);
        visibleConfirmPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        visibleConfirmPasswordField.getRight().setOnMousePressed(this::togglePassword);
        hiddenPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        hiddenPasswordField.getRight().setOnMousePressed(this::togglePassword);
        hiddenConfirmPasswordField.getRight().setOnMouseClicked(this::togglePassword);
        hiddenConfirmPasswordField.getRight().setOnMousePressed(this::togglePassword);
    }

    protected abstract void addNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException;

    protected abstract void confirmAndAddNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException;
}
