package com.jamesd.passwordmanager.Models.CustomControls;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * Custom control class which acts as a toggle for the user to switch between light and dark modes.
 * Could be generified if needs be to allow for custom messages/colour palettes.
 */
public class SwitchButton extends Label {

    private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);

    /**
     * Default constructor, creates a new button which is attached to a label. A listener is attached to the
     * SimpleBooleanProperty switchedOn field which updates the button background and text.
     */
    public SwitchButton() {
        Button switchBtn = new Button();
        switchBtn.setPrefWidth(60);
        switchBtn.setOnAction(t -> switchedOn.set(!switchedOn.get()));

        setGraphic(switchBtn);

        switchedOn.addListener((ov, t, t1) -> {
            if (t1)
            {
                setText("Dark");
                setStyle("-fx-background-color: black;-fx-text-fill:white;");
                switchBtn.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.SUN_ALT));
                switchBtn.setStyle("-fx-background-color: white");
                setContentDisplay(ContentDisplay.LEFT);
            }
            else
            {
                setText("Light");
                setStyle("-fx-background-color: white;-fx-text-fill:black;");
                switchBtn.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.MOON_ALT));
                switchBtn.setStyle("-fx-background-color: grey");
                setContentDisplay(ContentDisplay.LEFT);
            }
        });

        switchedOn.set(false);
    }

    /**
     * Retrieves the SimpleBooleanProperty switchedOn field
     * @return switchedOn field
     */
    public SimpleBooleanProperty switchOnProperty() { return switchedOn; }
}
