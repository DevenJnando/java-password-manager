package com.jamesd.passwordmanager.Utils;

import com.jamesd.passwordmanager.Controllers.SidebarController;
import com.jamesd.passwordmanager.Models.CustomControls.SwitchButton;
import com.jamesd.passwordmanager.PasswordManagerApp;
import javafx.scene.Scene;

/**
 * Utility class which sets the theme of a Scene or SwitchButton object to either light mode or dark mode.
 */
public abstract class ThemeSetterUtil {

    /**
     * Default constructor
     */
    public ThemeSetterUtil() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

    /**
     * Sets the stylesheet for a Scene object
     * @param scene Scene object to have light/dark theme applied to
     */
    public static void setTheme(Scene scene) {
        if(PropertiesUtil.getThemeProperties().getProperty("current_theme").contentEquals("light")) {
            scene.getStylesheets().add(SidebarController.class.getResource
                    ("/com/jamesd/passwordmanager/css/light_mode.css").toExternalForm());
        } else {
            scene.getStylesheets().add(PasswordManagerApp.class.getResource
                    ("/com/jamesd/passwordmanager/css/dark_mode.css").toExternalForm());
        }
    }

    /**
     * Sets the flag for if the application should be in light or dark mode
     * @param switchButton SwitchButton control which is set by the user
     */
    public static void setThemeSwitch(SwitchButton switchButton) {
        switchButton.switchOnProperty().set(!PropertiesUtil.getThemeProperties().getProperty("current_theme").contentEquals("light"));
    }
}
