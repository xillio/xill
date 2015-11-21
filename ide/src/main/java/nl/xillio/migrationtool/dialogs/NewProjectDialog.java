package nl.xillio.migrationtool.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import nl.xillio.migrationtool.gui.FXController;
import nl.xillio.migrationtool.gui.ProjectPane;
import nl.xillio.xill.util.settings.Settings;

import java.io.File;

/**
 * A dialog to add a new project.
 */
public class NewProjectDialog extends FXMLDialog {
    @FXML
    private TextField tfprojectname;
    @FXML
    private TextField tfprojectfolder;

    private final ProjectPane projectPane;
    private final String initalFoldervalue;
    private boolean hasBeenTypedInProjectFolder;

    /**
     * Default constructor.
     *
     * @param projectPane the projectPane to which this dialog is attached to.
     */
    public NewProjectDialog(final ProjectPane projectPane) {
        super("/fxml/dialogs/NewProject.fxml");

        this.projectPane = projectPane;
        setTitle("New Project");

        setProjectFolder(FXController.settings.simple().get(Settings.SETTINGS_GENERAL, Settings.DefaultProjectLocation));
        initalFoldervalue = tfprojectfolder.getText();
        tfprojectname.textProperty().addListener(this::typedInProjectName);
        tfprojectfolder.setOnKeyTyped(e -> hasBeenTypedInProjectFolder = true);
    }

    private void typedInProjectName(Object source, String oldValue, String newValue) {
        // The name changed. See if we need to fix this in the project folder field.
        // This we only do if the project folder field remains untouched
        if (isProjectFolderUntouched(newValue)) {
            setProjectFolder(initalFoldervalue + File.separator + newValue);
        }
    }

    private boolean isProjectFolderUntouched(String newValue) {
        String project = tfprojectfolder.getText();

        // If the project value equals the initial value then it is untouched
        if (project.equals(initalFoldervalue)) {
            hasBeenTypedInProjectFolder = false;
            return true;
        }

        // If anyone typed in this field it has been touched
        if (hasBeenTypedInProjectFolder) {
            return false;
        }

        // If the initial value isn't the prefix anymore is het been touched
        if (!project.startsWith(initalFoldervalue)) {
            return false;
        }

        int lastIndex = project.lastIndexOf(File.separatorChar);
        return project.substring(0, lastIndex).equals(initalFoldervalue);
    }

    /**
     * Set the default project folder
     *
     * @param folder project folder
     */
    public void setProjectFolder(final String folder) {
        tfprojectfolder.setText(folder);
    }

    @FXML
    private void browseBtnPressed(final ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(getInitialDirectory());


        File result = chooser.showDialog(getScene().getWindow());
        if (result != null) {
            tfprojectfolder.setText(result.getPath());

            // If we have no project name yet we want to auto fill this
            if (tfprojectname.getText().isEmpty()) {
                tfprojectname.setText(result.getName());
            }
        }
    }

    @FXML
    private void cancelBtnPressed(final ActionEvent event) {
        close();
    }

    @FXML
    private void okayBtnPressed(final ActionEvent event) {
        String projectName = tfprojectname.getText();
        String projectFolder = tfprojectfolder.getText();

        if ("".equals(projectName) || "".equals(projectFolder) || !projectPane.newProject(projectName, projectFolder, "")) {
            Alert error = new Alert(AlertType.ERROR);
            error.initModality(Modality.APPLICATION_MODAL);
            error.setContentText("Make sure the name and folder are not empty, and do not exist as a project yet.");
            error.show();
        } else {
            close();
        }
    }

    public File getInitialDirectory() {
        // Set directory
        if (tfprojectfolder.getText().isEmpty()) {
            return new File(System.getProperty("user.home"));
        }

        File currentFolder = new File(tfprojectfolder.getText());
        while (currentFolder != null && !currentFolder.exists()) {
            currentFolder = currentFolder.getParentFile();
        }

        return currentFolder;
    }
}
