package nl.xillio.migrationtool.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import nl.xillio.events.Event;
import nl.xillio.xill.api.Debugger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * A status bar, that can be used to represent the progress of the current running robot.
 */
public class StatusBar extends AnchorPane {

    // Used enum for statuses instead of the strings previously implemented
    public enum Status {
        STATUS_RUNNING("Running"),
        STATUS_STOPPED("Stopped"),
        STATUS_PAUSED("Paused"),
        STATUS_COMPILING("Compiling"),
        STATUS_READY("Ready");

        private String representation;

        Status(String representation) {
            this.representation = representation;
        }

        @Override
        public String toString() {
            return representation;
        }
    }

    private static final Logger LOGGER = LogManager.getLogger(StatusBar.class);

    @FXML
    private ProgressBar barRobotProgress;
    @FXML
    private Labeled lblTimeRemaining;
    @FXML
    private Labeled lblStatusVal;

    /**
     * Default constructor. A robot has to be attached to the component using setProcessor.
     */
    public StatusBar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StatusBar.fxml"));
            loader.setClassLoader(getClass().getClassLoader());
            loader.setController(this);
            Node ui = loader.load();
            getChildren().add(ui);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Adds event listeners used to display status to the debugger
     *
     * @param debugger The debugger to get the {@link Event Events} from
     */
    public void registerDebugger(Debugger debugger) {
        debugger.getOnRobotStart().addListener(e -> setStatus(Status.STATUS_RUNNING));
        debugger.getOnRobotStop().addListener(e -> setStatus(Status.STATUS_STOPPED));
        debugger.getOnRobotPause().addListener(e -> setStatus(Status.STATUS_PAUSED));
        debugger.getOnRobotContinue().addListener(e -> setStatus(Status.STATUS_RUNNING));
    }

    /**
     * Set the status of the status bar based on the robot action
     *
     * @param status The actual status to be displayed on the status bar
     */
    public void setStatus(Status status) {
        Platform.runLater(() -> lblStatusVal.setText(status.toString()));
    }
}
