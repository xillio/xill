package nl.xillio.xill.util.settings;

/**
 * Just list of categories and simple variable names
 * 
 * @author Zbynek Hochmann
 */
public class Settings {

	// =============================================================================
	/** Layout category */
	static final String LAYOUT = "Layout";

	/** Width of the left panel */
	static final String LeftPanelWidth = "LeftPanelWidth";

	/** The collapsed-state of the left panel */
	static final String LeftPanelCollapsed = "LeftPanelCollapsed";

	/** The height of the project panel */
	static final String ProjectHeight = "ProjectHeight";

	/** Width of the right panel for the specified currentRobot */
	static final String RightPanelWidth_ = "RightPanelWidth_";

	/** The collapsed-state of the right panel for the specified currentRobot */
	static final String RightPanelCollapsed_ = "RightPanelCollapsed_";

	/** The height of the editor */
	static final String EditorHeight_ = "EditorHeight_";

	/** The height of the preview panel */
	static final String PreviewHeight_ = "PreviewHeight_";

	/** The zoom factor of the code editor.*/
	static final String AceZoom_ = "AceZoom_";

	/** The dimensions of the settings dialog */
	static final String SettingsDialogDimensions = "SettingsDialogDimensions";

	// =============================================================================
	/** File category */
	static final String FILE = "File";

	/** The last folder a file was opened from or saved to */
	static final String LastFolder = "LastFolder";

	// =============================================================================
	/** Warning category */
	static final String WARNING = "Warning";

	/** Show warning dialogs for debug messages */
	static final String DialogDebug =  "DialogDebug";

	/** Show warning dialogs for info messages */
	static final String DialogInfo = "DialogInfo";

	/** Show warning dialogs for warning messages */
	static final String DialogWarning = "DialogWarning";

	/** Show warning dialogs for error messages */
	static final String DialogError = "DialogError";

	// =============================================================================
	/** Server category */
	static final String SERVER = "Server";

	/** Optional username to access XMTS */
	static final String ServerUsername = "ServerUsername";

	/** Optional password to access XMTS */
	static final String ServerPassword = "ServerPassword";

	/** Location XMTS is running on */
	static final String ServerHost = "ServerHost";

	// =============================================================================
	/** Info category */
	static final String INFO = "Info";

	/** Last version that was run */
	static final String LastVersion = "LastVersion";

	// =============================================================================
	/** License category */
	static final String LICENSE = "License";

	/** ?TBS */
	static final String License = "License";

	/** ?TBS */
	static final String LicenseCheck = "LicenseCheck";

	// =============================================================================
	/** Workspace category */
	static final String WORKSPACE = "Workspace";

	/** List of last time open tabs */
	static final String OpenTabs = "OpenTabs";

	/** Last time active tab */
	static final String ActiveTab = "ActiveTab";

	// =============================================================================
	/** General settings dialog */
	static final String SETTINGS_GENERAL = "SettingsGeneral";

	/** */
	static final String DefaultProjectLocation = "DefaultProjectLocation";

	/** */
	static final String OpenBotWithCleanConsole = "OpenBotWithCleanConsole";

	/** */	
	static final String RunBotWithCleanConsole = "RunBotWithCleanConsole";

	// =============================================================================
	/** Editor settings dialog */
	static final String SETTINGS_EDITOR = "SettingsEditor";

	/** */	
	static final String DisplayIndentGuides = "DisplayIndentGuides";

	/** */	
	static final String FontSize = "FontSize";

	/** */	
	static final String AutoSaveBotBeforeRun = "AutoSaveBotBeforeRun";

	/** */	
	static final String HighlightSelectedWord = "HighlightSelectedWord";

	/** */
	static final String NewLineMode = "NewLineMode";

	/** */
	static final String PrintMarginColumn = "PrintMarginColumn";

	/** */	
	static final String ShowGutter = "ShowGutter";

	/** */
	static final String ShowInvisibles = "ShowInvisibles";

	/** */
	static final String TabSize = "TabSize";

	/** */	
	static final String UseSoftTabs = "UseSoftTabs";

	/** */
	static final String WrapText = "WrapText";

	/** */
	static final String WrapLimit = "WrapLimit";

	/** */	
	static final String ShowPrintMargin = "ShowPrintMargin";

	/** */
	static final String ShowLineNumbers = "ShowLineNumbers";

	/** */
	// =============================================================================
	/** Hot-keys settings */
	static final String SETTINGS_KEYBINDINGS = "KeyBindings";

	/** */
	static final String NewFile = "NewFile";

	/** */
	static final String OpenFile = "OpenFile";

	/** */
	static final String SaveFile = "SaveFile";

	/** */
	static final String SaveFileAs = "SaveFileAs";

	/** */
	static final String SaveAll = "SaveFileAll";

	/** */
	static final String Close = "Close";

	/** */
	static final String HelpHome = "HelpHome";

	/** */
	static final String Run = "Run";

	/** */
	static final String Pause = "Pause";

	/** */
	static final String Stop = "Stop";

	/** */
	static final String Stepin = "Stepin";

	/** */
	static final String Stepover = "Stepover";

	/** */
	static final String ClearConsole = "ClearConsole";

	/** */
	static final String Search = "Search";

	/** */
	static final String ResetZoom = "ResetZoom";

	/** */
	static final String Copy = "Copy";

	/** */
	static final String Cut = "Cut";

	/** */
	static final String Paste = "Paste";

	/** */
	static final String DuplicateLines = "DuplicateLines";

	/** */
	static final String OpenSettings = "OpenSettings";
	// =============================================================================
}