package nl.xillio.xill.util.settings;

/**
 * Just list of categories and simple variable names
 * 
 * @author Zbynek Hochmann
 */
public class Settings {

	// =============================================================================
	/** Layout category */
	public static String LAYOUT = "Layout";

	/** Width of the left panel */
	public static String LeftPanelWidth = "LeftPanelWidth";

	/** The collapsed-state of the left panel */
	public static String LeftPanelCollapsed = "LeftPanelCollapsed";

	/** The height of the project panel */
	public static String ProjectHeight = "ProjectHeight";

	/** Width of the right panel for the specified currentRobot */
	public static String RightPanelWidth_ = "RightPanelWidth_";

	/** The collapsed-state of the right panel for the specified currentRobot */
	public static String RightPanelCollapsed_ = "RightPanelCollapsed_";

	/** The height of the editor */
	public static String EditorHeight_ = "EditorHeight_";

	/** The height of the preview panel */
	public static String PreviewHeight_ = "PreviewHeight_";

	/** The zoom factor of the code editor.*/
	public static String AceZoom_ = "AceZoom_";

	// =============================================================================
	/** File category */
	public static String FILE = "File";

	/** The last folder a file was opened from or saved to */
	public static String LastFolder = "LastFolder";

	// =============================================================================
	/** Warning category */
	public static String WARNING = "Warning";

	/** Show warning dialogs for debug messages */
	public static String DialogDebug =  "DialogDebug";

	/** Show warning dialogs for info messages */
	public static String DialogInfo = "DialogInfo";

	/** Show warning dialogs for warning messages */
	public static String DialogWarning = "DialogWarning";

	/** Show warning dialogs for error messages */
	public static String DialogError = "DialogError";

	// =============================================================================
	/** Server category */
	public static String SERVER = "Server";

	/** Optional username to access XMTS */
	public static String ServerUsername = "ServerUsername";

	/** Optional password to access XMTS */
	public static String ServerPassword = "ServerPassword";

	/** Location XMTS is running on */
	public static String ServerHost = "ServerHost";

	// =============================================================================
	/** Info category */
	public static String INFO = "Info";

	/** Last version that was run */
	public static String LastVersion = "LastVersion";

	// =============================================================================
	/** License category */
	public static String LICENSE = "License";

	/** ?TBS */
	public static String License = "License";

	/** ?TBS */
	public static String LicenseCheck = "LicenseCheck";

	// =============================================================================
	/** Workspace category */
	public static String WORKSPACE = "Workspace";

	/** List of last time open tabs */
	public static String OpenTabs = "OpenTabs";

	/** Last time active tab */
	public static String ActiveTab = "ActiveTab";

	// =============================================================================
}