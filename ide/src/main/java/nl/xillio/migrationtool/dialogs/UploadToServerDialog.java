package nl.xillio.migrationtool.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import nl.xillio.migrationtool.gui.ProjectPane;
import nl.xillio.xill.api.errors.NotImplementedException;

import java.io.File;

/**
 * A dialog to upload an item to the server.
 */
public class UploadToServerDialog extends FXMLDialog {

	private final TreeItem<Pair<File, String>> treeItem;
	private final ProjectPane projectPane;

	/**
	 * Default constructor.
	 *
	 * @param projectPane the projectPane to which this dialog is attached to.
	 * @param treeItem    the tree item on which the item will be deleted
	 */
	public UploadToServerDialog(final ProjectPane projectPane, final TreeItem<Pair<File, String>> treeItem) {
		super("/fxml/dialogs/UploadToServer.fxml");
		this.treeItem = treeItem;
		this.projectPane = projectPane;

		setTitle("Upload to server");
	}

	@FXML
	private void cancelBtnPressed(final ActionEvent event) {
		close();
	}

	@FXML
	private void okayBtnPressed(final ActionEvent event) {
		throw new NotImplementedException("Upload to server functionality has not yet been implemented.");
	}

}
