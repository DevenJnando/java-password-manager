package com.jamesd.passwordmanager.Wrappers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;

/**
 * Superclass for all wrapper subclasses. Contains common fields and methods for all wrapper classes.
 */
public abstract class BaseWrapper {

    protected ImageView favicon;
    protected BooleanProperty checked = new SimpleBooleanProperty();

    /**
     * Default constructor
     */
    public BaseWrapper() {

    }

    /**
     * Constructor which takes the selected favicon as an argument
     * @param favicon Logo for this password entry
     */
    public BaseWrapper(ImageView favicon) {
        this.favicon = favicon;
    }

    /**
     * Retrieves the favicon for this password entry
     * @return the logo for this password entry
     */
    public ImageView getFavicon() {
        return this.favicon;
    }

    /**
     * Retrieves the flag for when this password entry has been marked with a checkbox
     * @return true if checked, else false
     */
    public BooleanProperty isChecked() {
        return checked;
    }

    /**
     * Sets the flag for when this password entry has been marked with a checkbox
     * @param checked true if checked, else false
     */
    public void setChecked(Boolean checked) {
        this.checked.set(checked);
    }

    /**
     * Sets the favicon for this password entry
     * @param favicon logo for this password entry
     */
    public void setFavicon(ImageView favicon) {
        this.favicon = favicon;
    }
}
