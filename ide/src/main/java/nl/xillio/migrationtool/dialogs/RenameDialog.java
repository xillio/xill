package nl.xillio.migrationtool.dialogs;

import java.io.File;
import java.io.IOException;

import javafx.stage.Modality;
import org.apache.commons.io.FileUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import nl.xillio.xill.api.Xill;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A dialog to remove an item in the project view.
 */
public class RenameDialog extends FXMLDialog {
    private static final Logger LOGGER = LogManager.getLogger(RenameDialog.class);

	@FXML
	private TextField tfname;

	private final TreeItem<Pair<File, String>> treeItem;

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
	private void cancelBtnPressed(final ActionEvent event) {
		close();
	}

	@FXML
	private void okayBtnPressed(final ActionEvent event) {
        // Get the old file, new file name and new file.
		final File oldFile = treeItem.getValue().getKey();
		String fileName = tfname.getText();
		if (oldFile.isFile() && !fileName.endsWith(Xill.FILE_EXTENSION)) {
			fileName += "." + Xill.FILE_EXTENSION;
		}
		final File newFile = new File(oldFile.getParent(), fileName);

        try {
            // Rename the item and update the tree item.
            if (oldFile.isDirectory()) {
                FileUtils.moveDirectory(oldFile, newFile);
            } else {
                FileUtils.moveFile(oldFile, newFile);
            }
            treeItem.setValue(new Pair<>(newFile, tfname.getText()));
            close();
        } catch (IOException e) {
            LOGGER.error("IOException while renaming file.", e);
            new AlertDialog(AlertType.ERROR, "Failed to rename file/folder", "",
                    "Something went wrong while renaming a file/folder.\n" + e.getMessage()).showAndWait();
        }
	}
}
