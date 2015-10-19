package nl.xillio.migrationtool.dialogs;

import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;


public class SettingsDialog  extends FXMLDialog {

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
	private TextField tfnewfile;
	@FXML
	private TextField tfopenfile;
	@FXML
	private TextField tfsavefile;
	@FXML
	private TextField tfsavefileas;
	@FXML
	private TextField tfsaveall;
	
	
	private SettingsHandler settings;
	
	public SettingsDialog(final SettingsHandler settings) {
		super("/fxml/dialogs/Settings.fxml");
		setTitle("Settings");
		this.settings = settings;
		loadSettings();
		
		setRangeValidator(tffontsize, tffontsizeValid, 5, 50, "px");
		
		setShortcutHandler(tfnewfile);
		setShortcutHandler(tfopenfile);
	}

	private void setShortcutHandler(final TextField tf) {
		tf.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if ((event.getCode() == KeyCode.TAB) || (event.getCharacter().equals("\t"))) {
					return;
				}
				if (event.getCode() == KeyCode.DELETE) {
					tf.setText("");
					event.consume();
				}
				if ( (event.getEventType() == KeyEvent.KEY_PRESSED) && (!event.getText().isEmpty()) ) {
					if ((event.isControlDown() || event.isAltDown() || event.isShiftDown())) {
						tf.setText( (event.isControlDown() ? "Shortcut+" : "") + (event.isAltDown() ? "Alt+" : "") + (event.isShiftDown() ? "Shift+" : "") + event.getText().toUpperCase().charAt(0) );
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
			//!! apply(); // It applies new settings to be effective immediately
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
		saveText(tfnewfile, Settings.SETTINGS_KEYBINDINGS, Settings.NewFile);
		saveText(tfopenfile, Settings.SETTINGS_KEYBINDINGS, Settings.OpenFile);
		saveText(tfsavefile, Settings.SETTINGS_KEYBINDINGS, Settings.SaveFile);
		saveText(tfsavefileas, Settings.SETTINGS_KEYBINDINGS, Settings.SaveFileAs);
		saveText(tfsaveall, Settings.SETTINGS_KEYBINDINGS, Settings.SaveAll);
				
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
		setText(tfnewfile, Settings.SETTINGS_KEYBINDINGS, Settings.NewFile);
		setText(tfopenfile, Settings.SETTINGS_KEYBINDINGS, Settings.OpenFile);
		setText(tfsavefile, Settings.SETTINGS_KEYBINDINGS, Settings.SaveFile);
		setText(tfsavefileas, Settings.SETTINGS_KEYBINDINGS, Settings.SaveFileAs);
		setText(tfsaveall, Settings.SETTINGS_KEYBINDINGS, Settings.SaveAll);
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
		settings.simple().register(Settings.SETTINGS_KEYBINDINGS, Settings.NewFile, "Shortcut+N", "Shortcut to New file");
		settings.simple().register(Settings.SETTINGS_KEYBINDINGS, Settings.OpenFile, "Shortcut+O", "Shortcut to Open file");
		settings.simple().register(Settings.SETTINGS_KEYBINDINGS, Settings.SaveFile, "Shortcut+S", "Shortcut to Save file");
		settings.simple().register(Settings.SETTINGS_KEYBINDINGS, Settings.SaveFileAs, "Shortcut+Shift+S", "Shortcut to Save file as");
		settings.simple().register(Settings.SETTINGS_KEYBINDINGS, Settings.SaveAll, "Shortcut+Alt+S", "Shortcut to Save all");
	}

}
