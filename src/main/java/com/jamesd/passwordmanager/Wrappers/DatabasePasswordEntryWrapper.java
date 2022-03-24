package com.jamesd.passwordmanager.Wrappers;

import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import javafx.beans.property.BooleanProperty;
import javafx.scene.image.ImageView;

public class DatabasePasswordEntryWrapper extends BaseWrapper {
    private DatabasePasswordEntry databasePasswordEntry;

    public DatabasePasswordEntryWrapper() {

    }

    public DatabasePasswordEntryWrapper(DatabasePasswordEntry databasePasswordEntry, ImageView favicon) {
        super(favicon);
        this.databasePasswordEntry = databasePasswordEntry;
    }

    public DatabasePasswordEntry getDatabasePasswordEntry() {
        return this.databasePasswordEntry;
    }

    public void setDatabasePasswordEntry(DatabasePasswordEntry databasePasswordEntry) {
        this.databasePasswordEntry = databasePasswordEntry;
    }
}
