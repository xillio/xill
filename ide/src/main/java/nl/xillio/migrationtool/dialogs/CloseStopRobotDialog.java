package nl.xillio.migrationtool.dialogs;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import nl.xillio.migrationtool.gui.RobotTab;

public class CloseStopRobotDialog extends FXMLDialog {
	private final RobotTab tab;
	private final Event closeEvent;
	
	public CloseStopRobotDialog(final RobotTab tab, final Event closeEvent) {
		super("/fxml/dialogs/CloseStopRobot.fxml");
		
		this.tab = tab;
		this.closeEvent = closeEvent;
		setTitle(tab.getText() + " is still running, stop it?");
	}
	
	@FXML
	private void yesBtnPressed(final ActionEvent event) {
		tab.getEditorPane().getControls().stop();
		this.close();
	}
	
	@FXML
	private void noBtnPressed(final ActionEvent event) {
		closeEvent.consume();
		this.close();
	}
}
