package nl.xillio.migrationtool.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import nl.xillio.migrationtool.LicenseUtils;

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
        tfLicenseFile.clear();
        close();
    }

    @FXML
    private void okayBtnPressed(final ActionEvent event) {
        if (LicenseUtils.isValid(getChosen())) {
            close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "This license is not valid");
            alert.setTitle("Invalid license");
            alert.initOwner(this.getScene().getWindow());
            alert.show();
        }
    }

    public File getChosen() {
        return new File(tfLicenseFile.getText());
    }
}
