package nl.xillio.migrationtool.dialogs;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import nl.xillio.migrationtool.gui.ProjectPane;

/**
 * A dialog for deleting a item in the project view.
 */
public class DeleteFileDialog extends FXMLDialog {
	private final TreeItem<Pair<File, String>> treeItem;
	private final ProjectPane projectPane;

	/**
	 * Default constructor.
	 *
	 * @param projectPane
	 *        the projectPane to which this dialog is attached to.
	 * @param treeItem
	 *        the tree item on which the item will be deleted
	 */
	public DeleteFileDialog(final ProjectPane projectPane, final TreeItem<Pair<File, String>> treeItem) {
		super("/fxml/dialogs/DeleteFile.fxml");
		this.projectPane = projectPane;

		this.treeItem = treeItem;
		setTitle("Delete File");
	}

	@FXML
	private void cancelBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		close();
	}

	@FXML
	private void deleteBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
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
