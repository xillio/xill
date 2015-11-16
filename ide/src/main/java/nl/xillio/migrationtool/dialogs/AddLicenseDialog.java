package nl.xillio.migrationtool.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class AddLicenseDialog extends FXMLDialog {

    @FXML
    private TextField tfLicenseFile;

    public AddLicenseDialog() {
        super("/fxml/dialogs/AddLicense.fxml");
        setTitle("Browse License");
    }


    @FXML
    private void browseBtnPressed(final ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML doc(*.xml)", "*.xml"));
        File chosen = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (chosen != null) {
            tfLicenseFile.setText(chosen.getAbsolutePath());
        } else {
            tfLicenseFile.clear();
        }
    }

    @FXML
    private void cancelBtnPressed(final ActionEvent event) {
        close();
    }

    @FXML
    private void okayBtnPressed(final ActionEvent event) {
        close();
    }

    public File getChosen() {
        return new File(tfLicenseFile.getText());
    }
}
