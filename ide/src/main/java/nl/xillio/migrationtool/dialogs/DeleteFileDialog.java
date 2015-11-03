package nl.xillio.migrationtool.dialogs;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import nl.xillio.migrationtool.gui.FXController;
import nl.xillio.migrationtool.gui.ProjectPane;
import nl.xillio.migrationtool.gui.RobotTab;

/**
 * A dialog for deleting a item in the project view.
 */
public class DeleteFileDialog extends FXMLDialog {
	private final TreeItem<Pair<File, String>> treeItem;
	private final ProjectPane projectPane;
	private final FXController controller;

	/**
	 * Default constructor.
	 *
	 * @param projectPane
	 *        the projectPane to which this dialog is attached to.
	 * @param treeItem
	 *        the tree item on which the item will be deleted
	 */
	public DeleteFileDialog(final boolean robotRunning, final FXController controller, final ProjectPane projectPane, final TreeItem<Pair<File, String>> treeItem) {
		super(robotRunning ? "/fxml/dialogs/DeleteRunningBot.fxml" : "/fxml/dialogs/DeleteFile.fxml");
		this.projectPane = projectPane;
		this.controller = controller;
		this.treeItem = treeItem;
		setTitle("Delete File");
	}

	@FXML
	private void cancelBtnPressed(final ActionEvent event) {
		close();
	}

	@FXML
	private void deleteBtnPressed(final ActionEvent event) {
		// Get the robot tab.
		RobotTab tab = (RobotTab)controller.findTab(treeItem.getValue().getKey());

		// Stop the robot and close the tab.
		if (tab != null) {
			tab.getEditorPane().getControls().stop();
			controller.closeTab(tab);
		}

		// Delete the file and remove it from the project pane.
		deleteFile(treeItem);
		treeItem.getParent().getChildren().remove(treeItem);
		projectPane.getSelectionModel().clearSelection();
		close();
	}

	/**
	 * Deletes a file or folder from the filesystem.
	 *
	 * @param item
	 *        the item to delete
	 */
	public static void deleteFile(final TreeItem<Pair<File, String>> item) {
		// Empty the folder first
		item.getChildren().forEach(DeleteFileDialog::deleteFile);

		// Delete the folder/file
		item.getValue().getKey().delete();
	}
}
