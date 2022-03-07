package com.jamesd.passwordmanager.Utils;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public abstract class TransitionUtil {

    public TransitionUtil() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class");
    }

    public static FadeTransition createFader(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), node);
        fade.setFromValue(1);
        fade.setToValue(0);
        return fade;
    }
}
