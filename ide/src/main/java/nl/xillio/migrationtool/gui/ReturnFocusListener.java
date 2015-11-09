package nl.xillio.migrationtool.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;


public class ReturnFocusListener implements ChangeListener<Boolean> {
    private Node previousFocusOwner;

    public ReturnFocusListener(Scene scene) {
        scene.focusOwnerProperty().addListener((observable, oldValue, newValue) -> previousFocusOwner = oldValue);
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            Platform.runLater(() -> {
                if (previousFocusOwner != null) {
                    previousFocusOwner.requestFocus();
                }
            });
        }
    }
}