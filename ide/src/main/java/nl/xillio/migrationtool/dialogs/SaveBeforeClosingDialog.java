package nl.xillio.migrationtool.dialogs;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import nl.xillio.migrationtool.gui.RobotTab;

/**
 * A dialog to ask for saving upon tab closing.
 */
public class SaveBeforeClosingDialog extends FXMLDialog {
	private final RobotTab tab;
	private final Event closeEvent;
	private boolean cancelPressed = false;

	/**
	 * Default constructor.
	 *
	 * @param tab
	 *        the tab to close
	 * @param closeEvent
	 *        the original event
	 */
	public SaveBeforeClosingDialog(final RobotTab tab, final Event closeEvent) {
		super("/fxml/dialogs/SaveBeforeClosing.fxml");
		setTitle("Save changes?");
		this.tab = tab;
		this.closeEvent = closeEvent;
		setTitle("Save changes to " + this.tab.getText() + "?");
	}

	@FXML
	private void yesBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		// Try to save, don't close the tab if it failed
		if (!tab.save()) {
			closeEvent.consume();
		}
		close();
	}

	@FXML
	private void noBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		close();
	}

	@FXML
	private void cancelBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		closeEvent.consume();
		cancelPressed = true;
		close();
	}

	/**
	 * @return if the Cancel button has been pressed
	 */
	public boolean isCancelPressed() {
		return cancelPressed;
	}
}
