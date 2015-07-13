package nl.xillio.migrationtool.dialogs;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

import org.apache.commons.io.FileUtils;

/**
 * A dialog to remove an item in the project view.
 */
public class RenameDialog extends FXMLDialog {

	@FXML
	private TextField tfname;
	private final TreeItem<Pair<File, String>> treeItem;

	private final Alert error = new Alert(AlertType.ERROR);

	/**
	 * Default constructor.
	 *
	 * @param treeItem
	 *        the tree item on which the item will be deleted
	 */
	public RenameDialog(final TreeItem<Pair<File, String>> treeItem) {
		super("/fxml/dialogs/Rename.fxml");
		this.treeItem = treeItem;
		setTitle("Rename");
		tfname.setText(this.treeItem.getValue().getKey().getName());
	}

	@FXML
	private void cancelBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		close();
	}

	@FXML
	private void okayBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {

		final File oldFile = treeItem.getValue().getKey();
		final File newFile = new File(oldFile.getParent(), tfname.getText());

		if (newFile.exists()) {
			error.setContentText("Cannot rename file or folder. The target path already exists.");
			error.show();
		} else {
			try {
				if (oldFile.isDirectory()) {
					FileUtils.moveDirectory(oldFile, newFile);
				} else {
					FileUtils.moveFile(oldFile, newFile);
				}
				treeItem.setValue(new Pair<File,String>(newFile, tfname.getText()));
				close();
			} catch (IOException e) {
				e.printStackTrace();
				error.setContentText("Something went wrong while renaming a file/folder: " + e.getMessage());
				error.show();
			}
		}
	}
}
