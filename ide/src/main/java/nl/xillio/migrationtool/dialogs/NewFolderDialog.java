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
import nl.xillio.migrationtool.gui.ProjectPane;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * A dialog for creating a new folder in the project view.
 */
public class NewFolderDialog extends FXMLDialog {

	@FXML
	private TextField tffolder;

	private final TreeItem<Pair<File, String>> treeItem;
	private final ProjectPane projectPane;

	private static final Logger log = Logger.getLogger("XMT");

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
		this.projectPane = projectPane;
		this.treeItem = treeItem;

		setTitle("Add Folder");
	}

	@FXML
	private void cancelBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		close();
	}

	@FXML
	private void okayBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		try {
			FileUtils.forceMkdir(new File(treeItem.getValue().getKey(), tffolder.getText()));
			treeItem.setExpanded(true);
			projectPane.select(treeItem);
		} catch (IOException e) {
			treeItem.getParent().getChildren().remove(treeItem);
			projectPane.getSelectionModel().clearSelection();

			Alert error = new Alert(AlertType.ERROR);
			error.setContentText("Cannot create the new folder.");
			error.show();

			log.error(e.getMessage());
		}
		close();
	}
}
