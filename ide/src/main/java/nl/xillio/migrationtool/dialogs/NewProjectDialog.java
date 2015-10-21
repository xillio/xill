package nl.xillio.migrationtool.dialogs;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import nl.xillio.migrationtool.gui.ProjectPane;

/**
 * A dialog to add a new project.
 */
public class NewProjectDialog extends FXMLDialog {

	@FXML
	private TextField tfprojectname;
	@FXML
	private TextField tfprojectfolder;

	private final ProjectPane projectPane;

	/**
	 * Default constructor.
	 *
	 * @param projectPane
	 *        the projectPane to which this dialog is attached to.
	 */
	public NewProjectDialog(final ProjectPane projectPane) {
		super("/fxml/dialogs/NewProject.fxml");

		this.projectPane = projectPane;
		setTitle("New Project");
	}

	public void setProjectFolder(final String folder) {
		tfprojectfolder.setText(folder);
	}
	
	@FXML
	private void browseBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		File result = new DirectoryChooser().showDialog(getOwner());
		if (result != null) {
			tfprojectfolder.setText(result.getPath());
		}
	}

	@FXML
	private void cancelBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		close();
	}

	@FXML
	private void okayBtnPressed(@SuppressWarnings("unused") final ActionEvent event) {
		String projectName = tfprojectname.getText();
		String projectFolder = tfprojectfolder.getText();

		if (projectName.equals("") || projectFolder.equals("") || !projectPane.newProject(projectName, projectFolder, "")) {
			Alert error = new Alert(AlertType.ERROR);
			error.setContentText("Make sure the name and folder are not empty, and do not exist as a project yet.");
			error.show();
		} else {
			close();
		}
	}
}
