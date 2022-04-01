package com.jamesd.passwordmanager.Wrappers;

import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;

/**
 * Wrapper class for a website password entry
 */
public class WebsitePasswordEntryWrapper extends BaseWrapper {

    private WebsitePasswordEntry websitePasswordEntry;

    /**
     * Default constructor
     */
    public WebsitePasswordEntryWrapper() {

    }

    /**
     * Constructor which instantiates the WebsitePasswordEntry object to be wrapped as well as its ImageView favicon
     * @param websitePasswordEntry WebsitePasswordEntry to wrap
     * @param favicon logo for the WebsitePasswordEntry
     */
    public WebsitePasswordEntryWrapper(WebsitePasswordEntry websitePasswordEntry, ImageView favicon) {
        super(favicon);
        this.websitePasswordEntry = websitePasswordEntry;
    }

    /**
     * Retrieves the WebsitePasswordEntry object
     * @return WebsitePasswordEntry which has been wrapped by this class
     */
    public WebsitePasswordEntry getWebsitePasswordEntry() {
        return this.websitePasswordEntry;
    }

    /**
     * Sets the WebsitePasswordEntry object
     * @param websitePasswordEntry WebsitePasswordEntry to be wrapped by this class
     */
    public void setWebsitePasswordEntry(WebsitePasswordEntry websitePasswordEntry) {
        this.websitePasswordEntry = websitePasswordEntry;
    }
}
