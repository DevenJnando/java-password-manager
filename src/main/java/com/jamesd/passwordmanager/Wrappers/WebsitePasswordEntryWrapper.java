package com.jamesd.passwordmanager.Wrappers;

import com.jamesd.passwordmanager.Models.WebsitePasswordEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;

public class WebsitePasswordEntryWrapper {
    private WebsitePasswordEntry websitePasswordEntry;
    private ImageView favicon;
    private BooleanProperty checked = new SimpleBooleanProperty();

    public WebsitePasswordEntryWrapper() {

    }

    public WebsitePasswordEntryWrapper(WebsitePasswordEntry websitePasswordEntry, ImageView favicon) {
        this.websitePasswordEntry = websitePasswordEntry;
        this.favicon = favicon;
    }

    public ImageView getFavicon() {
        return this.favicon;
    }

    public WebsitePasswordEntry getWebsitePasswordEntry() {
        return this.websitePasswordEntry;
    }

    public void setWebsitePasswordEntry(WebsitePasswordEntry websitePasswordEntry) {
        this.websitePasswordEntry = websitePasswordEntry;
    }

    public BooleanProperty isChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked.set(checked);
    }

    public void setFavicon(ImageView favicon) {
        this.favicon = favicon;
    }
}
