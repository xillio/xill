package nl.xillio.xill.util;

import com.sun.javafx.tk.Toolkit;
import javafx.scene.input.KeyCode;

/**
 * This class represents a hotkey that triggers an action.
 */
class Hotkey {
    private static final String SYSTEM_SPECIFIC_SHORTCUT_TEXT = Toolkit.getToolkit().getPlatformShortcutKey().getName();

    private String shortcut;
    private String settingsid;
    private String settingsDscr;
    private String fxid;

    /**
     * Create a new Hotkey
     *
     * @param shortcut     the hotkey pattern
     * @param settingsid   the id that should be used in the settings
     * @param settingsDscr the description that should be used in the settings
     * @param fxid         the id that is used in the fxml definition
     */
    public Hotkey(String shortcut, String settingsid, String settingsDscr, String fxid) {
        this.shortcut = shortcut;
        this.settingsid = settingsid;
        this.settingsDscr = settingsDscr;
        this.fxid = "#" + fxid;
    }

    /**
     * Gets the textual representation of the hot key key combination.
     * If this representation contains the 'Shortcut' modifier it will be replaced by the system specific modifier
     *
     * @return the representation
     */
    public String getShortcut() {
        return shortcut.replaceAll(KeyCode.SHORTCUT.getName(), SYSTEM_SPECIFIC_SHORTCUT_TEXT);
    }

    /**
     * @return the id used in the settings framework to save this hotkey
     */
    public String getSettingsId() {
        return settingsid;
    }

    /**
     * @return the description used in the settings framework
     */
    public String getSettingsDescription() {
        return settingsDscr;
    }

    /**
     * @return the if used in the fxml definition
     */
    public String getFxId() {
        return fxid;
    }

    /**
     * Set the shortcut text.
     *
     * @param shortcut the pattern
     */
    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }
}