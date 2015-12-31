package nl.xillio.migrationtool.dialogs;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public class AlertDialog extends Alert {

    private static final Logger LOGGER = LogManager.getLogger(FXMLDialog.class);

    /**
     * Create a new alert dialog with the given alert type, title, header and content text, and a variable number of buttons.
     *
     * @param type    The alert type of the dialog.
     * @param title   The text to display in the title bar of the dialog.
     * @param header  The text to display in the header of the dialog.
     * @param content The text to display in the dialog content area.
     * @param buttons The buttons to add to the button-bar area of the dialog.
     */
    public AlertDialog(AlertType type, String title, String header, String content, ButtonType... buttons) {
        super(type, content, buttons);
        this.initStyle(StageStyle.UNIFIED);

        // Set the text.
        this.setTitle(title);
        this.setHeaderText(header);

        // Set the icon.
        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        try (InputStream image = this.getClass().getResourceAsStream("/icon.png")) {
            if (image != null) {
                stage.getIcons().add(new Image(image));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
