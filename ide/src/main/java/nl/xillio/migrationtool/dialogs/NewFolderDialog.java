package nl.xillio.migrationtool.dialogs;

import java.io.File;
import java.io.IOException;

import javafx.scene.control.ButtonType;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import nl.xillio.migrationtool.gui.ProjectPane;

/**
 * A dialog for creating a new folder in the project view.
 */
public class NewFolderDialog extends FXMLDialog {

	@FXML
	private TextField tffolder;

	private final TreeItem<Pair<File, String>> treeItem;
	private final ProjectPane projectPane;

	private static final Logger LOGGER = LogManager.getLogger(NewFolderDialog.class);

	/**
	 * Default constructor.
	 *
	 * @param projectPane
	 *        the projectPane to which this dialog is attached to.
	 * @param treeItem
	 *        the tree item on which the item will be deleted
	 */
	public NewFolderDialog(final ProjectPane projectPane, final TreeItem<Pair<File, String>> treeItem) {
		super("/fxml/dialogs/NewFolder.fxml");
        this.setTitle("Add Folder");
		this.projectPane = projectPane;
		this.treeItem = treeItem;
	}

	@FXML
	private void cancelBtnPressed(final ActionEvent event) {
		close();
	}

	@FXML
	private void okayBtnPressed(final ActionEvent event) {
		try {
			File newFolder = new File(getFolder(treeItem.getValue().getKey()), tffolder.getText());
			FileUtils.forceMkdir(newFolder);
		} catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            AlertDialog error = new AlertDialog(AlertType.ERROR, "Could not create folder", "",
                    e.getMessage(), ButtonType.OK);
			error.show();
		}
		close();
	}

	private File getFolder(File key) {
        return key.isDirectory() ? key : key.getParentFile();
	}
}
