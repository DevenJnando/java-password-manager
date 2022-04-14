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

/**
 * Abstract class which provides implementations of ModifyPasswordController methods, as well as its own abstract
 * methods for adding new password entries to the password database
 */
public abstract class NewPasswordController extends ModifyPasswordController {

    /**
     * FXML fields
     */
    @FXML
    VBox passwordVbox = new VBox();

    /**
     * Default constructor
     */
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

    /**
     * Once validation is passed, this method is called to add a new
     * database password to the selected folder in the password database using the user-inputted fields.
     * @throws GeneralSecurityException Throws GeneralSecurityException if the password encryption process fails
     * @throws UnsupportedEncodingException Throws UnsupportedEncodingException if a character has been entered which
     * cannot be encoded in UTF-8
     * @throws ClassNotFoundException Throws ClassNotFoundException if the DatabasePasswordEntry class cannot be found
     */
    protected abstract void addNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException;

    /**
     * Performs validation checks on the user-input fields and proceeds
     * to call addNewPassword once validation passes. If validation passes, but the password is too weak, a further error
     * will be given to the user.
     * @throws GeneralSecurityException Throws GeneralSecurityException if the password encryption process fails
     * @throws UnsupportedEncodingException Throws UnsupportedEncodingException if a character has been entered which
     * cannot be encoded in UTF-8
     * @throws ClassNotFoundException Throws ClassNotFoundException if the DatabasePasswordEntry class cannot be found
     */
    @FXML
    protected abstract void confirmAndAddNewPassword() throws GeneralSecurityException, UnsupportedEncodingException, ClassNotFoundException;
}
