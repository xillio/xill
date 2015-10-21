package nl.xillio.migrationtool.dialogs;

import java.io.File;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import nl.xillio.migrationtool.gui.FXController;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;


public class SettingsDialog  extends FXMLDialog {

	private boolean apply = false;
	
	private class Valid {
		private boolean valid = true;
		private String value = "";
		public void setValid(boolean valid) {
			this.valid = valid;
		}
		public boolean isValid() {
			return this.valid;
		}
		public void setValue(final String value) {
			this.value = value;
		}
		public String getValue() {
			return this.value;
		}
	}
	@FXML
	private TextField tfprojectfolder;
	@FXML
	private CheckBox cbopenbotwcleanconsole;
	@FXML
	private CheckBox cbrunbotwcleanconsole;
	@FXML
	private CheckBox cbenablecodecompletion;
	@FXML
	private CheckBox cbdisplayindentguides;
	@FXML
	private TextField tffontsize;
	private Valid tffontsizeValid = new Valid();
	@FXML
	private CheckBox cbautosavebotbeforerun;
	@FXML
	private CheckBox cbhighlightselword;
	@FXML
	private ComboBox cbnewlinemode;
	
	
	
	private SettingsHandler settings;
	
	public SettingsDialog(final SettingsHandler settings) {
		super("/fxml/dialogs/Settings.fxml");
		setTitle("Settings");
		this.settings = settings;
		loadSettings();
		
		setRangeValidator(tffontsize, tffontsizeValid, 5, 50, "px");
		
		
		
		Platform.runLater(() -> {
			FXController.hotkeys.getAllTextFields(getScene()).forEach(hk -> setShortcutHandler(hk));
		});
	}

	private void setShortcutHandler(final TextField tf) {
		tf.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				String shortcut = null;
				
				if ((event.getCode() == KeyCode.TAB) || (event.getCharacter().equals("\t"))) {
					return;
				}
				
				if (event.getCode() == KeyCode.DELETE) {
					tf.setText("");
					event.consume();
					return;
				}
				
				if ((event.getCode().isFunctionKey()) && (event.getEventType() == KeyEvent.KEY_RELEASED)) {
					shortcut = (event.isControlDown() ? "Shortcut+" : "") + (event.isAltDown() ? "Alt+" : "") + (event.isShiftDown() ? "Shift+" : "") + event.getCode().getName().toUpperCase();
				} 
				else if ( (event.getEventType() == KeyEvent.KEY_PRESSED) && (!event.getText().isEmpty()) ) {
					if ((event.isControlDown() || event.isAltDown() || event.isShiftDown())) {
						shortcut = (event.isControlDown() ? "Shortcut+" : "") + (event.isAltDown() ? "Alt+" : "") + (event.isShiftDown() ? "Shift+" : "") + event.getText().toUpperCase().charAt(0);
					}
				}

				if (shortcut != null) {
					if (FXController.hotkeys.findShortcutInDialog(getScene(), shortcut) == null) {
						tf.setText(shortcut);
					}
				}

				event.consume();
			}
		});
	}
	
	private void setRangeValidator(final TextField tf, final Valid valid, final int fromIncl, final int toIncl, final String suffix) {
		Pattern pattern = Pattern.compile("[0-9]+" + suffix);
		
		tf.addEventHandler(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
		    public void handle(KeyEvent event) {
		    	Platform.runLater(() -> {
		    		boolean matches = false;
		    		String text = tf.getText();
		    		if (pattern.matcher(text).matches()) {
		    			int value = Integer.parseInt(text.substring(0, text.length()-suffix.length()));
		    			if ((value >= fromIncl) && (value <= toIncl)) {
			    			matches = true;
			    			valid.setValue(new Integer(value).toString());
			    		}
			    	}
		    		if (matches) {
		    			tf.setStyle("-fx-text-fill: black;");
		    		} else {
		    			tf.setStyle("-fx-text-fill: red;");
		    			valid.setValue("");
		    		}
		    		valid.setValid(matches);
		    	});
		    }
		});
	}

	@FXML
	private void okayBtnPressed(final ActionEvent event) {
		try {
			validate();
			apply = true;
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK);
			alert.showAndWait();
			return;
		}
		saveSettings();
		close();
	}

	private void validate() throws Exception {
		if (!this.tffontsizeValid.isValid()) {
			throw new Exception("Invalid font size value!");
		}
		
		if (!tfprojectfolder.getText().isEmpty()) {
			File file = new File(tfprojectfolder.getText());
			if ((file == null) || (!file.isDirectory())) {
				throw new Exception("Invalid default project folder!");
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
		saveCheckBox(cbenablecodecompletion, Settings.SETTINGS_EDITOR, Settings.EnableCodeCompletion);
		saveCheckBox(cbdisplayindentguides, Settings.SETTINGS_EDITOR, Settings.DisplayIndentGuides);
		saveText(tffontsize, Settings.SETTINGS_EDITOR, Settings.FontSize);
		saveCheckBox(cbhighlightselword, Settings.SETTINGS_EDITOR, Settings.HighlightSelectedWord);

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
		setCheckBox(cbenablecodecompletion, Settings.SETTINGS_EDITOR, Settings.EnableCodeCompletion);
		setCheckBox(cbdisplayindentguides, Settings.SETTINGS_EDITOR, Settings.DisplayIndentGuides);
		setText(tffontsize, Settings.SETTINGS_EDITOR, Settings.FontSize);
		setCheckBox(cbhighlightselword, Settings.SETTINGS_EDITOR, Settings.HighlightSelectedWord);
		
		// Key bindings
		Platform.runLater(() -> {
			FXController.hotkeys.setDialogFromSettings(getScene(), settings);
		});
	}

	public boolean shouldApply() {
		return apply;
	}
	
	private void setCheckBox(final CheckBox checkBox, final String category, final String keyValue) {
		checkBox.setSelected(new Boolean(this.settings.simple().get(category, keyValue)));
	}
	
	private void setText(final TextField field, final String category, final String keyValue) {
		field.setText(this.settings.simple().get(category, keyValue));
	}
	
	private void saveText(final TextField field, final String category, final String keyValue) {
		this.settings.simple().save(category, keyValue, field.getText());
	}
	
	private void saveCheckBox(final CheckBox checkBox, final String category, final String keyValue) {
		this.settings.simple().save(category, keyValue, new Boolean(checkBox.isSelected()).toString());
	}
	
	@FXML
	private void cancelBtnPressed(final ActionEvent event) {
		close();
	}
	
	public static void register(final SettingsHandler settings) {
		// General
		settings.simple().register(Settings.SETTINGS_GENERAL, Settings.DefaultProjectLocation, "", "The default project location");
		settings.simple().register(Settings.SETTINGS_GENERAL, Settings.OpenBotWithCleanConsole, "true", "If the console is cleared when the bot is open");
		settings.simple().register(Settings.SETTINGS_GENERAL, Settings.RunBotWithCleanConsole, "false", "If the console is cleared when the bot is about to run");
		settings.simple().register(Settings.SETTINGS_GENERAL, Settings.AutoSaveBotBeforeRun, "true", "Save the robot before it's run");
		
		// Editor
		settings.simple().register(Settings.SETTINGS_EDITOR, Settings.EnableCodeCompletion, "true", "Enable the code completion");
		settings.simple().register(Settings.SETTINGS_EDITOR, Settings.DisplayIndentGuides, "false", "Displays indent guides");
		settings.simple().register(Settings.SETTINGS_EDITOR, Settings.FontSize, "12px", "The editor's font size");
		settings.simple().register(Settings.SETTINGS_EDITOR, Settings.HighlightSelectedWord, "true", "Highlight selected word in editor");
		
		// Key bindings
		FXController.hotkeys.registerHotkeysSettings(settings);
	}

}
