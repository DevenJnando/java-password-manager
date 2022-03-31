package com.jamesd.passwordmanager.Controllers;

import javafx.event.Event;

/**
 * Interface containing shared abstract methods for all password entries
 */
public abstract class PasswordController extends ErrorChecker {

    /**
     * Method which sets icons for input fields/buttons. E.g. clickable eye/eye-slash icon for hiding/showing a password
     */
    protected abstract void setIcons();

    /**
     * Method which sets text formatters for all relevant input fields
     */
    protected abstract void setTextFormatters();

    /**
     * Method which toggles a password from hidden to visible and vice versa
     * @param event event which triggers a password toggle
     */
    protected abstract void togglePassword(Event event);
}
