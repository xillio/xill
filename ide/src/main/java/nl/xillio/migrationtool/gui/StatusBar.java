package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import nl.xillio.events.Event;
import nl.xillio.xill.api.Debugger;

/**
 * A status bar, that can be used to represent the progress of the current running robot.
 */
public class StatusBar extends AnchorPane {

	private static final String STATUS_RUNNING = "Running", STATUS_STOPPED = "Stopped",
			STATUS_PAUSED = "Paused", STATUS_COMPILING = "Compiling", STATUS_READY = "Ready";



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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds event listeners used to display status to the debugger
	 * 
	 * @param debugger
	 *        The debugger to get the {@link Event Events} from
	 */
	public void registerDebugger(Debugger debugger) {
		debugger.getOnRobotStart().addListener(e -> setStatus(STATUS_RUNNING));
		debugger.getOnRobotStop().addListener(e -> setStatus(STATUS_STOPPED));
		debugger.getOnRobotPause().addListener(e -> setStatus(STATUS_PAUSED));
		debugger.getOnRobotContinue().addListener(e -> setStatus(STATUS_RUNNING));
	}

	/**
	 * Signal to the {@link StatusBar} that the robot is being compiled
	 */
	public void setCompiling(boolean compiling) {
		setStatus(compiling ? STATUS_COMPILING : STATUS_READY);
	}

	private void setStatus(String status) {
		Platform.runLater(() -> lblStatusVal.setText(status));
	}

}
