package com.jamesd.passwordmanager.Utils;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Utility class for creating a transition effect
 */
public abstract class TransitionUtil {

    /**
     * Constructor throws UnsupportedOperationException if called - class is abstract
     */
    public TransitionUtil() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class");
    }

    /**
     * Creates a FadeTransition object which lasts two seconds and is applied to the node parameter
     * @param node Node to have the transition effect applied to
     * @return FadeTransition object
     */
    public static FadeTransition createFader(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        return fade;
    }
}
