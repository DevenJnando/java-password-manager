package com.jamesd.passwordmanager.Controllers;

import javafx.event.Event;

public abstract class PasswordController {

    public PasswordController() {

    }

    protected abstract void setIcons();
    protected abstract void togglePassword(Event event);
}
