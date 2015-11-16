package nl.xillio.migrationtool.dialogs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Window;
import nl.xillio.license.License;
import nl.xillio.migrationtool.LicenseUtils;
import nl.xillio.migrationtool.gui.FXController;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Dialog contains all configurable Xill IDE options and allows to change them
 *
 * @author Zbynek Hochmann
 */
public class SettingsDialog extends FXMLDialog {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private TextField tfprojectfolder;
    @FXML
    private CheckBox cbopenbotwcleanconsole;
    @FXML
    private CheckBox cbrunbotwcleanconsole;
    @FXML
    private CheckBox cbdisplayindentguides;
    @FXML
    private TextField tffontsize;
    private RangeValidator tffontsizeValidator = new RangeValidator(5, 26, "px", false);
    @FXML
    private CheckBox cbautosavebotbeforerun;
    @FXML
    private CheckBox cbhighlightselword;
    @FXML
    private ComboBox<String> cbnewlinemode;
    @FXML
    private TextField tfprintmargincolumn;
    private RangeValidator tfprintmargincolumnValidator = new RangeValidator(0, 1000, null, false);
    @FXML
    private CheckBox cbshowgutter;
    @FXML
    private CheckBox cbshowinvisibles;
    @FXML
    private TextField tftabsize;
    private RangeValidator tftabsizeValidator = new RangeValidator(1, 100, null, false);
    @FXML
    private CheckBox cbusesofttabs;
    @FXML
    private CheckBox cbwraptext;
    @FXML
    private TextField tfwraplimit;
    private RangeValidator tfwraplimitValidator = new RangeValidator(2, 1000, null, false);
    @FXML
    private CheckBox cbshowprintmargin;
    @FXML
    private CheckBox cbshowlinenumbers;
    @FXML
    private Tab licenseTab;
    @FXML
    private Label lblLicenseType;

    private SettingsHandler settings;
    private Runnable onApply;

    /**
     * Dialog constructor
     *
     * @param settings SettingsHandler instance
     */
    public SettingsDialog(final SettingsHandler settings) {
        super("/fxml/dialogs/Settings.fxml");
        setTitle("Settings");
        this.settings = settings;

        setSize();
        setValidator(tffontsize, tffontsizeValidator);
        setValidator(tfprintmargincolumn, tfprintmargincolumnValidator);
        setValidator(tftabsize, tftabsizeValidator);
        setValidator(tfwraplimit, tfwraplimitValidator);

        ObservableList<String> options = FXCollections.observableArrayList("auto", "windows", "unix");
        cbnewlinemode.setItems(options);

        loadSettings();

        Platform.runLater(() -> FXController.hotkeys.getAllTextFields(getScene()).forEach(this::setShortcutHandler));

        loadLicenseInfo();
    }

    private void loadLicenseInfo() {
        License license = LicenseUtils.getLicense();

        lblLicenseType.setText(license.getVendor());
    }

    private void setSize() {
        String dimensions = settings.simple().get(Settings.LAYOUT, Settings.SettingsDialogDimensions);
        String[] parts = dimensions.split("[^0-9\\.]");
        if (parts.length != 2) {
            LOGGER.error("Failed to get size of settings dialog from the settings");
            return;
        }

        Window window = getScene().getWindow();

        window.setWidth(Double.parseDouble(parts[0]));
        window.setHeight(Double.parseDouble(parts[1]));
        window.widthProperty().addListener((a, b, c) -> saveSize());
        window.heightProperty().addListener((a, b, c) -> saveSize());

    }

    private void saveSize() {
        String value = getScene().getWindow().getWidth() + "x" + getScene().getWindow().getHeight();
        settings.simple().save(Settings.LAYOUT, Settings.SettingsDialogDimensions, value);
        settings.commit();
    }

    @FXML
    private void projectBrowseButtonPressed() {
        DirectoryChooser chooser = new DirectoryChooser();

        // Set directory
	    File folder = new File(tfprojectfolder.getText());
        if (folder.isDirectory()) {
            chooser.setInitialDirectory(folder);
        } else {
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        Platform.runLater(() -> {
            File chosen = chooser.showDialog(getScene().getWindow());

            if (chosen != null) {
                try {
                    tfprojectfolder.setText(chosen.getCanonicalPath());
                } catch (IOException e) {
                    LOGGER.error("Failed to get canonical path of " + chosen, e);
                }
            }
        });
    }

    private void setShortcutHandler(final TextField tf) {
        tf.addEventHandler(KeyEvent.ANY, this::handleShortcut);
    }

    private void handleShortcut(KeyEvent event) {
        TextField tf = (TextField) event.getTarget();

        if ((event.getCode() == KeyCode.TAB) || (event.getCharacter().equals("\t"))) {
            return;
        }

        if (event.getCode() == KeyCode.DELETE) {
            tf.setText("");
            event.consume();
            return;
        }


        String shortcut = getShortCutPattern(event);

        if (shortcut != null) {
            if (FXController.hotkeys.findShortcutInDialog(getScene(), shortcut) == null) {
                tf.setText(shortcut);
            }
        }

        event.consume();
    }

    private String getShortCutPattern(KeyEvent event) {
        String modifiers = (event.isControlDown() ? "Ctrl+" : "") +
                (event.isMetaDown() ? "Meta+" : "") +
                (event.isAltDown() ? "Alt+" : "") +
                (event.isShiftDown() ? "Shift+" : "");

        if ((event.getCode().isFunctionKey()) && (event.getEventType() == KeyEvent.KEY_RELEASED)) {
            // This is an F* key.
            return modifiers + event.getCode().getName().toUpperCase();
        } else if ((event.getEventType() == KeyEvent.KEY_PRESSED)) {
            // This is any other key holding a text value. We require there to be a modifier
            if (!modifiers.isEmpty()) {
                return modifiers + event.getCode().getName();
            }
        }

        return null;
    }

    private void setValidator(final TextField tf, final TextValidator validator) {

        tf.addEventHandler(KeyEvent.KEY_TYPED, event -> Platform.runLater(() -> {
            if (validator.test(tf.getText())) {
                tf.setStyle("-fx-text-fill: black;");
            } else {
                tf.setStyle("-fx-text-fill: red;");
            }
        }));
    }

    @FXML
    private void okayBtnPressed(final ActionEvent event) {
        if (apply()) {
            close();
        }
    }

    @FXML
    private void applyBtnPressed(final ActionEvent event) {
        apply();
    }

    private boolean apply() {
        try {
            validate();
        } catch (ValidationException e) {
            Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return false;
        }
        saveSettings();
        if (onApply != null) {
            onApply.run();
        }
        return true;
    }

    private void validate() throws ValidationException {
        if (!this.tffontsizeValidator.isValid()) {
            throw new ValidationException("Invalid font size value!");
        }

        if (!this.tfprintmargincolumnValidator.isValid()) {
            throw new ValidationException("Invalid print margin column value!");
        }

        if (!this.tftabsizeValidator.isValid()) {
            throw new ValidationException("Invalid tab size value!");
        }

        if (!this.tfwraplimitValidator.isValid()) {
            throw new ValidationException("Invalid wrap limit value!");
        }

        if (!tfprojectfolder.getText().isEmpty()) {
            File file = new File(tfprojectfolder.getText());
            if(!file.exists()) {
                throw new ValidationException("The chosen project folder does not exist!");
            }

            if (!file.isDirectory()) {
                throw new ValidationException("The chosen project folder is not a directory!");
            }
        }
    }

    private void saveSettings() {
        settings.setManualCommit(true);

        // General
        saveText(tfprojectfolder, Settings.SETTINGS_GENERAL, Settings.DefaultProjectLocation);
        saveCheckBox(cbopenbotwcleanconsole, Settings.SETTINGS_GENERAL, Settings.OpenBotWithCleanConsole);
        saveCheckBox(cbrunbotwcleanconsole, Settings.SETTINGS_GENERAL, Settings.RunBotWithCleanConsole);
        saveCheckBox(cbautosavebotbeforerun, Settings.SETTINGS_GENERAL, Settings.AutoSaveBotBeforeRun);

        // Editor
        saveCheckBox(cbdisplayindentguides, Settings.SETTINGS_EDITOR, Settings.DisplayIndentGuides);
        saveText(tffontsize, Settings.SETTINGS_EDITOR, Settings.FontSize);
        saveCheckBox(cbhighlightselword, Settings.SETTINGS_EDITOR, Settings.HighlightSelectedWord);
        saveComboBox(cbnewlinemode, Settings.SETTINGS_EDITOR, Settings.NewLineMode);
        saveText(tfprintmargincolumn, Settings.SETTINGS_EDITOR, Settings.PrintMarginColumn);
        saveCheckBox(cbshowgutter, Settings.SETTINGS_EDITOR, Settings.ShowGutter);
        saveCheckBox(cbshowinvisibles, Settings.SETTINGS_EDITOR, Settings.ShowInvisibles);
        saveText(tftabsize, Settings.SETTINGS_EDITOR, Settings.TabSize);
        saveCheckBox(cbusesofttabs, Settings.SETTINGS_EDITOR, Settings.UseSoftTabs);
        saveCheckBox(cbwraptext, Settings.SETTINGS_EDITOR, Settings.WrapText);
        saveText(tfwraplimit, Settings.SETTINGS_EDITOR, Settings.WrapLimit);
        saveCheckBox(cbshowprintmargin, Settings.SETTINGS_EDITOR, Settings.ShowPrintMargin);
        saveCheckBox(cbshowlinenumbers, Settings.SETTINGS_EDITOR, Settings.ShowLineNumbers);

        // Key bindings
        FXController.hotkeys.saveSettingsFromDialog(getScene(), settings);

        settings.commit();
        settings.setManualCommit(false);
    }

    private void loadSettings() {
        // General
        setText(tfprojectfolder, Settings.SETTINGS_GENERAL, Settings.DefaultProjectLocation);
        setCheckBox(cbopenbotwcleanconsole, Settings.SETTINGS_GENERAL, Settings.OpenBotWithCleanConsole);
        setCheckBox(cbrunbotwcleanconsole, Settings.SETTINGS_GENERAL, Settings.RunBotWithCleanConsole);
        setCheckBox(cbautosavebotbeforerun, Settings.SETTINGS_GENERAL, Settings.AutoSaveBotBeforeRun);

        // Editor
        setCheckBox(cbdisplayindentguides, Settings.SETTINGS_EDITOR, Settings.DisplayIndentGuides);
        setText(tffontsize, Settings.SETTINGS_EDITOR, Settings.FontSize);
        setCheckBox(cbhighlightselword, Settings.SETTINGS_EDITOR, Settings.HighlightSelectedWord);
        setComboBox(cbnewlinemode, Settings.SETTINGS_EDITOR, Settings.NewLineMode);
        setText(tfprintmargincolumn, Settings.SETTINGS_EDITOR, Settings.PrintMarginColumn);
        setCheckBox(cbshowgutter, Settings.SETTINGS_EDITOR, Settings.ShowGutter);
        setCheckBox(cbshowinvisibles, Settings.SETTINGS_EDITOR, Settings.ShowInvisibles);
        setText(tftabsize, Settings.SETTINGS_EDITOR, Settings.TabSize);
        setCheckBox(cbusesofttabs, Settings.SETTINGS_EDITOR, Settings.UseSoftTabs);
        setCheckBox(cbwraptext, Settings.SETTINGS_EDITOR, Settings.WrapText);
        setText(tfwraplimit, Settings.SETTINGS_EDITOR, Settings.WrapLimit);
        setCheckBox(cbshowprintmargin, Settings.SETTINGS_EDITOR, Settings.ShowPrintMargin);
        setCheckBox(cbshowlinenumbers, Settings.SETTINGS_EDITOR, Settings.ShowLineNumbers);

        // Key bindings
        Platform.runLater(() -> FXController.hotkeys.setDialogFromSettings(getScene(), settings));
    }

    private void setComboBox(final ComboBox<String> comboBox, final String category, final String keyValue) {
        String value = settings.simple().get(category, keyValue);
        comboBox.getSelectionModel().select(value);
    }

    private void saveComboBox(final ComboBox<String> comboBox, final String category, final String keyValue) {
        settings.simple().save(category, keyValue, comboBox.getSelectionModel().getSelectedItem());
    }

    private void setCheckBox(final CheckBox checkBox, final String category, final String keyValue) {
        checkBox.setSelected(this.settings.simple().getBoolean(category, keyValue));
    }

    private void saveCheckBox(final CheckBox checkBox, final String category, final String keyValue) {
        this.settings.simple().save(category, keyValue, checkBox.isSelected());
    }

    private void setText(final TextField field, final String category, final String keyValue) {
        field.setText(this.settings.simple().get(category, keyValue));
    }

    private void saveText(final TextField field, final String category, final String keyValue) {
        this.settings.simple().save(category, keyValue, field.getText());
    }

    @FXML
    private void cancelBtnPressed(final ActionEvent event) {
        close();
    }

    /**
     * Register all (configurable) settings from this dialog
     *
     * @param settings the SettingHandler instance
     */
    public static void register(final SettingsHandler settings) {
        // General
        settings.simple().register(Settings.LAYOUT, Settings.SettingsDialogDimensions, "800x600", "The size of the settings window");
        settings.simple().register(Settings.SETTINGS_GENERAL, Settings.DefaultProjectLocation, System.getProperty("user.home"), "The default project location");
        settings.simple().register(Settings.SETTINGS_GENERAL, Settings.OpenBotWithCleanConsole, "true", "If the console is cleared when the bot is open");
        settings.simple().register(Settings.SETTINGS_GENERAL, Settings.RunBotWithCleanConsole, "false", "If the console is cleared when the bot is about to run");
        settings.simple().register(Settings.SETTINGS_GENERAL, Settings.AutoSaveBotBeforeRun, "true", "Save the robot before it's run");

        // Editor
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.DisplayIndentGuides, "false", "Displays indent guides");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.FontSize, "12px", "The editor's font size");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.HighlightSelectedWord, "true", "Highlight selected word in editor");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.NewLineMode, "auto", "New-line mode in editor");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.PrintMarginColumn, "80", "Set print margin column");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.ShowGutter, "true", "Show editor gutter");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.ShowInvisibles, "false", "Show invisibles in editor");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.TabSize, "5", "Tab size in editor");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.UseSoftTabs, "true", "Use soft tabs in editor");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.WrapText, "false", "Wrap text in editor");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.WrapLimit, "60", "Wrap limit in editor");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.ShowPrintMargin, "false", "Show print margin in editor");
        settings.simple().register(Settings.SETTINGS_EDITOR, Settings.ShowLineNumbers, "true", "Show line numbers in editor");

        // Key bindings
        FXController.hotkeys.registerHotkeysSettings(settings);
    }

    public void setOnApply(Runnable applyHandler) {
        this.onApply = applyHandler;
    }

    private class ValidationException extends Exception {
        ValidationException(String message) {
            super(message);
        }
    }

    private interface TextValidator {
        boolean test(final String text);
    }

    private class RangeValidator implements TextValidator {
        private boolean valid = true;
        private String suffix;
        private int fromIncl;
        private int toIncl;
        private boolean allowEmpty;
        private Pattern pattern;

        RangeValidator(final int fromIncl, final int toIncl, final String suffix, final boolean allowEmpty) {
            this.fromIncl = fromIncl;
            this.toIncl = toIncl;
            this.suffix = (suffix == null ? "" : suffix);
            this.allowEmpty = allowEmpty;
            this.pattern = Pattern.compile("[0-9]+" + this.suffix);
        }

        @Override
        public boolean test(final String text) {
            if (allowEmpty && ((text == null) || (text.isEmpty()))) {
                valid = true;
            } else {
                valid = false;
                if (pattern.matcher(text).matches()) {
                    int value = Integer.parseInt(text.substring(0, text.length() - suffix.length()));
                    if ((value >= fromIncl) && (value <= toIncl)) {
                        valid = true;
                    }
                }
            }
            return valid;
        }

        public boolean isValid() {
            return this.valid;
        }
    }
}
