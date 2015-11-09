package nl.xillio.migrationtool.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;

/**
 * Listener that can be attached to a {@link Node#focusedProperty()} that will return focus to the previous focus owner.
 * <p>
 * Note: This listener does not return focus to buttons
 *
 * @author Geert Konijnendijk, Sander Visser
 *         <p>
 *
 */
public class ReturnFocusListener implements ChangeListener<Boolean> {
    // The previous owner of the focus
    private Node previousFocusOwner;

    public ReturnFocusListener(Scene scene) {
        // Keep track of the previous focus owner
        scene.focusOwnerProperty().addListener((observable, oldValue, newValue) ->
                // Do not return focus to buttons
                previousFocusOwner = oldValue instanceof ButtonBase ? previousFocusOwner : oldValue);
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        //if focussed return focus.
        if (newValue) {
            Platform.runLater(() -> {
                if (previousFocusOwner != null) {
                    previousFocusOwner.requestFocus();
                }
            });
        }
    }
}