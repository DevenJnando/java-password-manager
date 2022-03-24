package com.jamesd.passwordmanager.Wrappers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;

public abstract class BaseWrapper {

    protected ImageView favicon;
    protected BooleanProperty checked = new SimpleBooleanProperty();

    public BaseWrapper() {

    }

    public BaseWrapper(ImageView favicon) {
        this.favicon = favicon;
    }

    public ImageView getFavicon() {
        return this.favicon;
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
