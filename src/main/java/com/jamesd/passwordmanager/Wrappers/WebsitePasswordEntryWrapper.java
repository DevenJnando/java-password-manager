package com.jamesd.passwordmanager.Wrappers;

import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;

public class WebsitePasswordEntryWrapper extends BaseWrapper {
    private WebsitePasswordEntry websitePasswordEntry;

    public WebsitePasswordEntryWrapper() {

    }

    public WebsitePasswordEntryWrapper(WebsitePasswordEntry websitePasswordEntry, ImageView favicon) {
        super(favicon);
        this.websitePasswordEntry = websitePasswordEntry;
    }

    public WebsitePasswordEntry getWebsitePasswordEntry() {
        return this.websitePasswordEntry;
    }

    public void setWebsitePasswordEntry(WebsitePasswordEntry websitePasswordEntry) {
        this.websitePasswordEntry = websitePasswordEntry;
    }
}
