package nl.xillio.xill.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;

public class HotkeysHandler {

	public enum Hotkeys {
		NEW, OPEN, SAVE, SAVEAS, SAVEALL, CLOSE, HELP, RUN, 
		PAUSE, STOP, STEPIN, STEPOVER, CLEARCONSOLE, COPY, CUT, 
		PASTE, RESET_ZOOM, FIND, OPENSETTINGS
	}
	
	private class Hotkey {
		String shortcut;
		String settingsid;
		String settingsDscr;
		String fxid;
		
		public Hotkey(String shortcut, String settingsid, String settingsDscr, String fxid) {
			this.shortcut = shortcut;
			this.settingsid = settingsid;
			this.settingsDscr = settingsDscr;
			this.fxid = "#" + fxid;
		}
	}
	
	private HashMap<Hotkeys, Hotkey> hotkeys = new HashMap<>();

	public HotkeysHandler() {
		init();
	}
	
	private void init() {
		// Shortcut is Ctrl on Windows and Meta on Mac.
		hotkeys.put(Hotkeys.NEW, new Hotkey("Shortcut+N", Settings.NewFile, "Shortcut to New file", "tfnewfile"));
		hotkeys.put(Hotkeys.OPEN, new Hotkey("Shortcut+O", Settings.OpenFile, "Shortcut to Open file", "tfopenfile"));
		hotkeys.put(Hotkeys.SAVE, new Hotkey("Shortcut+S", Settings.SaveFile, "Shortcut to Save file", "tfsavefile"));
		hotkeys.put(Hotkeys.SAVEAS, new Hotkey("Shortcut+Alt+S", Settings.SaveFileAs, "Shortcut to Save file as", "tfsavefileas"));
		hotkeys.put(Hotkeys.SAVEALL, new Hotkey("Shortcut+Shift+S", Settings.SaveAll, "Shortcut to Save all", "tfsaveall"));
		hotkeys.put(Hotkeys.CLOSE, new Hotkey("Shortcut+W", Settings.Close, "Shortcut to close currently open bot", "tfclose"));
		hotkeys.put(Hotkeys.HELP, new Hotkey("F1", Settings.HelpHome, "Shortcut to show help", "tfhelphome"));
		hotkeys.put(Hotkeys.RUN, new Hotkey("F6", Settings.Run, "Shortcut to run bot", "tfrun"));
		hotkeys.put(Hotkeys.PAUSE,new Hotkey( "F7", Settings.Pause, "Shortcut to pause bot", "tfpause"));
		hotkeys.put(Hotkeys.STOP, new Hotkey("F8", Settings.Stop, "Shortcut to stop bot", "tfstop"));
		hotkeys.put(Hotkeys.STEPIN, new Hotkey("F9", Settings.Stepin, "Shortcut to step in", "tfstepin"));
		hotkeys.put(Hotkeys.STEPOVER, new Hotkey("F10", Settings.Stepover, "Shortcut to step over", "tfstepover"));
		hotkeys.put(Hotkeys.CLEARCONSOLE, new Hotkey("Shortcut+L", Settings.ClearConsole, "Shortcut to clear console", "tfclearconsole"));
		hotkeys.put(Hotkeys.COPY, new Hotkey("Shortcut+C", Settings.Copy, "Shortcut to copy to clipboard", "tfcopy"));
		hotkeys.put(Hotkeys.CUT, new Hotkey("Shortcut+X", Settings.Cut, "Shortcut to cut to clipboard", "tfcut"));
		hotkeys.put(Hotkeys.PASTE,new Hotkey("Shortcut+V", Settings.Paste, "Shortcut to paste from clipboard", "tfpaste"));
		hotkeys.put(Hotkeys.RESET_ZOOM,new Hotkey("Shortcut+0", Settings.ResetZoom, "Shortcut to reset zoom", "tfresetzoom"));
		hotkeys.put(Hotkeys.FIND, new Hotkey("Shortcut+F", Settings.Search, "Shortcut to start search", "tfsearch"));
		hotkeys.put(Hotkeys.OPENSETTINGS, new Hotkey("Shortcut+P", Settings.OpenSettings, "Shortcut to open settings dialog", "tfopensettings"));
	}
	
	public void setHotkeysFromSettings(final SettingsHandler settings) {
		hotkeys.entrySet().stream().forEach(hk -> {
			hk.getValue().shortcut = settings.simple().get(Settings.SETTINGS_KEYBINDINGS, hk.getValue().settingsid);
		});
	}
	
	public void setHotkeysFromDialog(final Scene scene) {
		hotkeys.entrySet().stream().forEach(hk -> hk.getValue().shortcut = ((TextField)scene.lookup(hk.getValue().fxid)).getText());
	}

	public void setDialogFromSettings(final Scene scene, final SettingsHandler settings) {
		hotkeys.entrySet().stream().forEach(hk -> {
			TextField tf = (TextField)scene.lookup(hk.getValue().fxid);
			tf.setText(settings.simple().get(Settings.SETTINGS_KEYBINDINGS, hk.getValue().settingsid));
		});
	}

	public TextField findShortcutInDialog(final Scene scene, final String shortcut) {
		TextField result[] = {null};
		hotkeys.entrySet().stream().forEach(hk -> {
			TextField tf = (TextField)scene.lookup(hk.getValue().fxid);
			if (tf.getText().equals(shortcut)) {
				result[0] = tf;
			}
		});
		return result[0];
	}
	
	public List<TextField> getAllTextFields(final Scene scene) {
		LinkedList<TextField> result = new LinkedList<>();
		for (Map.Entry<Hotkeys, Hotkey> entry : hotkeys.entrySet()) {
			result.add((TextField)scene.lookup(entry.getValue().fxid));
		}
		return result;
	}
	
	public void saveSettingsFromDialog(final Scene scene, final SettingsHandler settings) {
		hotkeys.entrySet().stream().forEach(hk -> settings.simple().save(Settings.SETTINGS_KEYBINDINGS, hk.getValue().settingsid, ((TextField)scene.lookup(hk.getValue().fxid)).getText()));
	}
	
	public String getShortcut(final Hotkeys hk) {
		return hotkeys.get(hk).shortcut;
	}
	
	public Hotkeys getHotkey(final KeyEvent keyEvent) {
		Hotkeys result[] = {null};
		hotkeys.entrySet().stream().filter(o -> (KeyCombination.valueOf(o.getValue().shortcut).match(keyEvent))).forEach(o -> result[0] = o.getKey());
		return result[0];
	}
	
	public void registerHotkeysSettings(final SettingsHandler settings) {
		hotkeys.entrySet().forEach( hk -> settings.simple().register(Settings.SETTINGS_KEYBINDINGS, hk.getValue().settingsid, hk.getValue().shortcut, hk.getValue().settingsDscr));
	}
}
