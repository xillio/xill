package nl.xillio.migrationtool.gui;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

/**
 * A status bar, that can be used to represent the progress of the current running robot.
 */
public class StatusBar extends AnchorPane {

	private static final Logger log = LogManager.getLogger();
	// private Robot robot;

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

	// /**
	// * Attaches a robot to the status bar.
	// *
	// * @param robot
	// * the robot to set
	// */
	// public void setRobot(final Robot robot) {
	// this.robot = robot;
	// Platform.runLater(() -> startProgressBarUpdater());
	// }

	/**
	 * Initializes a thread that polls the progress of the current robot and updates the progress indicators.
	 */
	public void startProgressBarUpdater() {
		// new Thread(() -> {
		// while (true) {
		//
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// log.error(e.getMessage());
		// }
		//
		// Platform.runLater(() -> {
		// double progress = robot.getStatus().getProgress();
		// RunState status = robot.getStatus().getRunState();
		// String progressMessage = robot.getStatus().getStatusMessage();
		//
		// if (progress < 0 && status == RunState.STARTED) {
		// // Robot is running but progress is not (yet) being tracked
		// barRobotProgress.setVisible(true);
		// barRobotProgress.setProgress(-1);
		// lblTimeRemaining.setText("Time remaining: unknown");
		// lblStatusVal.setText(progressMessage);
		// } else if (progress >= 1 && status == RunState.STARTED) {
		// // Robot is done but still rounding up. Show undetermined
		// barRobotProgress.setVisible(true);
		// barRobotProgress.setProgress(-1);
		// lblTimeRemaining.setText("Time remaining: 0 seconds");
		// lblStatusVal.setText("");
		// } else if (status == RunState.STARTED || status == RunState.PAUSED) {
		// // Robot is running and has progress: Show progress
		// barRobotProgress.setVisible(true);
		// barRobotProgress.setProgress(progress);
		// lblStatusVal.setText(progressMessage);
		// long runtime = robot.getStatus().getRuntime();
		// long runtimeremaining = (long) (runtime / progress) - runtime;
		// String message = "Time remaining: " + formatTimeRemaining(runtimeremaining);
		// lblTimeRemaining.setText(message);
		// } else {
		// // Robot is inactive
		// barRobotProgress.setVisible(false);
		// lblTimeRemaining.setText("");
		// lblStatusVal.setText("");
		// }
		// });
		// }
		// }).start();
	}

}
