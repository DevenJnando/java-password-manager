package com.jamesd.passwordmanager.Wrappers;

import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import javafx.beans.property.BooleanProperty;
import javafx.scene.image.ImageView;

/**
 * Wrapper class for a database password entry
 */
public class DatabasePasswordEntryWrapper extends BaseWrapper {

    private DatabasePasswordEntry databasePasswordEntry;

    /**
     * Default constructor
     */
    public DatabasePasswordEntryWrapper() {

    }

    /**
     * Constructor which instantiates the DatabasePasswordEntry object and its ImageView favicon
     * @param databasePasswordEntry the DatabasePasswordEntry object to wrap
     * @param favicon the logo for this DatabasePasswordEntry object
     */
    public DatabasePasswordEntryWrapper(DatabasePasswordEntry databasePasswordEntry, ImageView favicon) {
        super(favicon);
        this.databasePasswordEntry = databasePasswordEntry;
    }

    /**
     * Retrieves the DatabasePasswordEntry object
     * @return DatabasePasswordEntry wrapped by this class
     */
    public DatabasePasswordEntry getDatabasePasswordEntry() {
        return this.databasePasswordEntry;
    }

    /**
     * Sets the DatabasePasswordEntry object
     * @param databasePasswordEntry DatabasePasswordEntry to wrap
     */
    public void setDatabasePasswordEntry(DatabasePasswordEntry databasePasswordEntry) {
        this.databasePasswordEntry = databasePasswordEntry;
    }
}
