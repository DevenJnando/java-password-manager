package com.jamesd.passwordmanager.Controllers;

import javafx.event.Event;

public abstract class PasswordController extends ErrorChecker {

    public PasswordController() {

    }

    protected abstract void setIcons();
    protected abstract void setTextFormatters();
    protected abstract void togglePassword(Event event);
}
