package nl.xillio.migrationtool.dialogs;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import nl.xillio.migrationtool.gui.ProjectPane;

/**
 * A dialog for deleting a project.
 */
public class DeleteProjectDialog extends FXMLDialog {
	private final ProjectPane projectPane;
	private final TreeItem<Pair<File, String>> treeItem;

	/**
	 * Default constructor.
	 *
	 * @param projectPane
	 *        the projectPane to which this dialog is attached to.
	 * @param treeItem
	 *        the tree item on which the project will be deleted
	 */
	public DeleteProjectDialog(final ProjectPane projectPane, final TreeItem<Pair<File, String>> treeItem) {
		super("/fxml/dialogs/DeleteProject.fxml");

		this.projectPane = projectPane;
		this.treeItem = treeItem;
		setTitle("Delete Project");
	}

	@FXML
	private void cancelBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		close();
	}

	@FXML
	private void removeBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		projectPane.removeProject(treeItem);
		close();
	}

	@FXML
	private void deleteBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		projectPane.deleteProject(treeItem);
		close();
	}
}
