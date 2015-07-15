package nl.xillio.migrationtool.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import nl.xillio.migrationtool.Loader;
import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.migrationtool.documentation.PluginListener;
import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.sharedlibrary.license.License;
import nl.xillio.sharedlibrary.license.License.LicenseType;
import nl.xillio.sharedlibrary.license.License.SoftwareModule;
import nl.xillio.sharedlibrary.settings.SettingsHandler;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.Xill;

/**
 * This class is the global controller for the application
 */
public class FXController implements Initializable, EventHandler<Event> {
	private static final Logger log = Logger.getLogger("XMT");
	private static final SettingsHandler settings = SettingsHandler.getSettingsHandler();

	private static final File DEFAULT_OPEN_BOT = new File("scripts/Hello-Xillio." + Xill.FILE_EXTENSION);
	private static final File PLUGIN_FOLDER = new File("plugins");

	// Shortcut is Ctrl on Windows and Meta on Mac.
	@SuppressWarnings("javadoc")
	public static final String HOTKEY_SAVE = "Shortcut+S",
		HOTKEY_SAVEAS = "Shortcut+Alt+S",
		HOTKEY_SAVEALL = "Shortcut+Shift+S",
		HOTKEY_NEW = "Shortcut+N",
		HOTKEY_OPEN = "Shortcut+O",
		HOTKEY_CLOSE = "Shortcut+W",
		HOTKEY_HELP = "F1",
		HOTKEY_RUN = "F6",
		HOTKEY_PAUSE = "F7",
		HOTKEY_STOP = "F8",
		HOTKEY_STEPIN = "F9",
		HOTKEY_STEPOVER = "F10",
		HOTKEY_CLEARCONSOLE = "Shortcut+L",
		HOTKEY_COPY = "Shortcut+C",
		HOTKEY_CUT = "Shortcut+X",
		HOTKEY_PASTE = "Shortcut+V",
		HOTKEY_RESET_ZOOM = "Shortcut+0",
		HOTKEY_FIND = "Shortcut+F";

	/*
	 * *******************************************************
	 * Buttons, fields, etc.
	 */

	@FXML
	private Button btnOpenFile, btnSave, btnSaveAs, btnSaveAll,
		btnRemoveAllBreakpoints, btnEvaluate, btnRun, btnStepOver, btnStepIn, btnPause,
		btnStop, btnSearch, btnBrowser, btnRegexTester, btnClearConsole, btnPreviewOpenBrowser, btnPreviewOpenRegex,
		btnHideLeftPane, btnShowLeftPane;
	@FXML
	private TabPane tpnBots;
	@FXML
	private AnchorPane apnRoot, apnPreviewPane, apnLeft;
	@FXML
	private VBox vbxLeftHidden;
	@FXML
	private TreeView<Pair<File, String>> trvProjects;
	@FXML
	private WebView webFunctionDoc;
	@FXML
	private HBox hbxMain;
	@FXML
	private SplitPane spnMain, spnLeft;
	@FXML
	private HelpPane helppane;

	@FXML
	private ProjectPane projectpane;

	private final PluginLoader<PluginPackage> pluginLoader = PluginLoader.load(PluginPackage.class);

	/**
	 * Initialize custom components
	 */
	@Override
	public void initialize(final URL url, final ResourceBundle bundle) {
	settings.registerSimpleSetting("File", "LastFolder", System.getProperty("user.dir"), "The last folder a file was opened from or saved to.");
	settings.registerSimpleSetting("Warning", "DialogDebug", "false", "Show warning dialogs for debug messages.");
	settings.registerSimpleSetting("Warning", "DialogInfo", "false", "Show warning dialogs for info messages.");
	settings.registerSimpleSetting("Warning", "DialogWarning", "false", "Show warning dialogs for warning messages.");
	settings.registerSimpleSetting("Warning", "DialogError", "true", "Show warning dialogs for error messages.");
	settings.registerSimpleSetting("Server", "ServerHost", "http://localhost:10000", "Location XMTS is running on.");
	settings.registerSimpleSetting("Server", "ServerUsername", "", "Optional username to access XMTS.");
	settings.registerSimpleSetting("Server", "ServerPassword", "", "Optional password to access XMTS.");
	settings.registerSimpleSetting("Info", "LastVersion", "0.0.0", "Last version that was run.");
	settings.registerSimpleSetting("Layout", "LeftPanelWidth", "0.2", "Width of the left panel");
	settings.registerSimpleSetting("Layout", "LeftPanelCollapsed", "false", "The collapsed-state of the left panel");
	settings.registerSimpleSetting("Layout", "ProjectHeight", "0.5", "The height of the project panel");

	// Initialize layout and layout listeners
	Platform.runLater(() -> {
		// Add splitpane position listener
		spnMain.getDividers().get(0).positionProperty().addListener((observable, oldPos, newPos) -> {
		if (spnMain.getItems().contains(apnLeft)) {
			settings.saveSimpleSetting("LeftPanelWidth", newPos.toString());
		}
		});

		spnMain.setDividerPosition(0, Double.parseDouble(settings.getSimpleSetting("LeftPanelWidth")));
		// Remove the left hidden bar from dom
		if (Boolean.parseBoolean(settings.getSimpleSetting("LeftPanelCollapsed"))) {
		btnHideLeftPane();
		} else {
		btnShowLeftPane();
		}

		spnLeft.setDividerPosition(0, Double.parseDouble(settings.getSimpleSetting("ProjectHeight")));
		spnLeft.getDividers().get(0).positionProperty().addListener((observable, oldPos, newPos) -> settings.saveSimpleSetting("ProjectHeight", Double.toString(newPos.doubleValue())));
	});

	// Start the elasticsearch console
	Platform.runLater(ESConsoleClient::getInstance);

	tpnBots.getTabs().clear();
	projectpane.setGlobalController(this);

	// Hide left collapsed panel from the dom at startup
	hbxMain.getChildren().remove(vbxLeftHidden);

	// Add window handler
	Platform.runLater(() -> apnRoot.getScene().getWindow().setOnCloseRequest(event -> {
		System.out.println("Shutting down application");
		closeApplication();
	}));

	apnRoot.addEventHandler(KeyEvent.KEY_PRESSED, this);

	// Add listener for window shown
	Platform.runLater(() -> {
		String workspace = settings.getSimpleSetting("Workspace");
		if (workspace == null) {
		workspace = DEFAULT_OPEN_BOT.getAbsolutePath();
		}
		if (workspace != null && !"".equals(workspace)) {
		String[] files = workspace.split(";");
		ArrayUtils.reverse(files); // Reverse the list to ensure same tab order as original.
		for (final String filename : files) {
			openFile(new File(filename));
		}
		}
	});

	Platform.runLater(() -> {
		verifyLicense();
		showReleaseNotes();
	});

	
	// Subscribe to plugin events
	pluginLoader.getPluginManager().onPluginAccepted().addListener(p -> {
		log.info("Loaded Xill Package: " + p.getName());
	});
	pluginLoader.getPluginManager().onPluginDenied().addListener(p -> {
		log.error("Failed to load Xill Package: " + p.getName());
	});
	PluginListener.Attach(pluginLoader);

	// Initialize plugin loader
	PLUGIN_FOLDER.mkdirs();
	pluginLoader.addFolder(PLUGIN_FOLDER);
	try {
		pluginLoader.load();
	} catch (CircularReferenceException e) {
		throw new RuntimeException(e);
	}
	}

	/**
	 * Create a new robot file.
	 */
	@FXML
	private void buttonNewFile() {

	// Select project path
	File projectfile = null;
	if (projectpane.getCurrentProject() != null) {
		projectfile = projectpane.getCurrentProject().getValue().getKey();
	} else {
		projectfile = new File(System.getProperty("user.home"));
	}

	// Select initial directory
	File initialFolder = null;
	if (projectpane.getCurrentItem() != null) {
		initialFolder = projectpane.getCurrentItem().getValue().getKey();
	} else {
		initialFolder = projectfile;
	}

	if (initialFolder.isFile()) {
		initialFolder = initialFolder.getParentFile();
	}

	// Select robot file
	FileChooser fileChooser = new FileChooser();
	fileChooser.setInitialDirectory(initialFolder);
	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Xill Robot (*." + Xill.FILE_EXTENSION + ")", "*." + Xill.FILE_EXTENSION));
	fileChooser.setTitle("New Robot");

	File chosen = fileChooser.showSaveDialog(tpnBots.getScene().getWindow());

	if(chosen == null) {
	    //No file was chosen so we abort
	    return;
	}
	
	RobotTab tab;
	try {
		if (!chosen.exists()) {
		chosen.createNewFile();
		}

		tab = new RobotTab(projectfile.getAbsoluteFile(), chosen, this);
		tpnBots.getTabs().add(tab);
	} catch (IOException e) {
		e.printStackTrace();
	}

	}

	@FXML
	private void buttonOpenFile() {
	FileChooser fileChooser = new FileChooser();
	String lastfolder = settings.getSimpleSetting("LastFolder");
	if (lastfolder != null) {
		fileChooser.setInitialDirectory(new File(lastfolder));
	}
	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Xillio scripts (*" + "." + Xill.FILE_EXTENSION + ")", "*" + "." + Xill.FILE_EXTENSION));
	File newfile = fileChooser.showOpenDialog(btnOpenFile.getScene().getWindow());

	if (newfile != null) {
		openFile(newfile);
	}
	}

	/**
	 * @param newfile
	 * @return the tab that was opened or null if something went wrong
	 */
	public RobotTab openFile(final File newfile) {
	// Skip if the file doesn't exist
	if (!newfile.exists() || !newfile.isFile()) {
		log.error("Failed to open file `" + newfile.getAbsolutePath() + "`. File not found.");
		return null;
	}

	// Verify file isn't open already
	for (Tab tab : tpnBots.getTabs()) {
		RobotTab editor = (RobotTab) tab;
		try {
		if (editor.getDocument() != null && editor.getDocument().getCanonicalPath().equals(newfile.getCanonicalPath())) {
			tpnBots.getSelectionModel().select(editor);
			showTab(editor);
			return editor;
		}
		} catch (IOException e) {
		log.error("Error while opening file: " + e.getMessage());
		return null;
		}
	}

	// Tab is not open yet: open new tab
	settings.saveSimpleSetting("LastFolder", newfile.getParent());

	RobotTab tab;
	try {
		tab = new RobotTab(new File(projectpane.getProjectPath(newfile).get()), newfile.getAbsoluteFile(), this);
		tpnBots.getTabs().add(tab);
		showTab(tab);
		return tab;
	} catch (IOException e) {
		e.printStackTrace();
	}
	return null;

	}

	@FXML
	private void buttonSave() {
	if (!btnSave.isDisabled()) {
		Tab tab = tpnBots.getSelectionModel().getSelectedItem();
		if (tab != null) {
		((RobotTab) tab).save();
		}
	}
	}

	@FXML
	private void buttonSaveAs() {
	if (!btnSaveAs.isDisabled()) {
		Tab tab = tpnBots.getSelectionModel().getSelectedItem();
		if (tab != null) {
		((RobotTab) tab).save(true);
		}
	}
	}

	@FXML
	private void buttonSaveAll() {
	if (btnSaveAll.isDisabled()) {
		tpnBots.getTabs().forEach(tab -> ((RobotTab) tab).save());
	}
	}

	@FXML
	private void buttonSearch() {
	if (btnSearch.isDisabled()) {
		return;
	}
	}

	@FXML
	private void buttonBrowser() {
	if (btnBrowser.isDisabled()) {
		return;
	}
	}

	@FXML
	private void buttonRegexTester() {
	if (btnRegexTester.isDisabled()) {
		return;
	}
	}

	@FXML
	private void buttonPreviewOpenBrowser() {
	if (btnPreviewOpenBrowser.isDisabled()) {
		return;
	}
	}

	@FXML
	private void buttonPreviewOpenRegex() {
	if (btnPreviewOpenRegex.isDisabled()) {
		return;
	}
	}

	@FXML
	private void btnHideLeftPane() {
	settings.saveSimpleSetting("LeftPanelWidth", "" + spnMain.getDividerPositions()[0]);
	settings.saveSimpleSetting("LeftPanelCollapsed", "true");
	spnMain.getItems().remove(apnLeft);
	if (!hbxMain.getChildren().contains(vbxLeftHidden)) {
		hbxMain.getChildren().add(0, vbxLeftHidden);
	}
	}

	@FXML
	private void btnShowLeftPane() {
	settings.saveSimpleSetting("LeftPanelCollapsed", "false");

	hbxMain.getChildren().remove(vbxLeftHidden);
	if (!spnMain.getItems().contains(apnLeft)) {
		spnMain.getItems().add(0, apnLeft);
		spnMain.setDividerPosition(0, Double.parseDouble(settings.getSimpleSetting("LeftPanelWidth")));
	}
	}

	private void closeApplication() {
	String openTabs = String.join(";", getTabs().stream().map(tab -> tab.getDocument().getAbsolutePath()).collect(Collectors.toList()));
	// Save all tabs
	settings.saveSimpleSetting("Workspace", openTabs);
	// Close all tabs
	tpnBots.getTabs().forEach(tab -> closeTab(tab, false));

	// Purge plugins
	for (PluginPackage plugin : pluginLoader.getPluginManager().getPlugins()) {
		try {
		plugin.close();
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	ProjectPane.stop();
	Platform.exit();
	System.exit(0);
	}

	private void verifyLicense() {
	License license = new License(settings.getSimpleSetting("license"));
	while (!license.isValid(SoftwareModule.IDE)) {
		TextInputDialog enterLicence = new TextInputDialog();
		enterLicence.setContentText("Copy the contents of the licensefile you received into the textfield.");
		enterLicence.setHeaderText("Please enter a valid Xillio license");
		enterLicence.setTitle("Valid license required");
		Optional<String> licenseNew = enterLicence.showAndWait();

		if (!licenseNew.isPresent()) {
		((Stage) apnRoot.getScene().getWindow()).close();
		closeApplication();
		return;
		}

		license = new License(licenseNew.get());

		settings.saveSimpleSetting("license", license.toString());
		Alert validLicense = new Alert(AlertType.INFORMATION);
		if (license.getLicenseType() == LicenseType.INTERNAL) {
		validLicense.setContentText("Do not distribute this license or your settingsfile to other machines than your personal laptop.");
		validLicense.setHeaderText("This is a Xillio internal license");
		validLicense.setTitle("Info");
		} else if (license.getLicenseType() == LicenseType.DEVELOPER) {
		validLicense.setContentText("Do not use this license for production purposes.");
		validLicense.setHeaderText("This is a Developer-only license");
		validLicense.setTitle("Info");
		} else {
		validLicense.setContentText("This license is invalid. Please enter a valid license.");
		validLicense.setHeaderText("Invalid license");
		validLicense.setTitle("Error");
		}
		validLicense.showAndWait();
		Stage stage = (Stage) apnRoot.getScene().getWindow();
		stage.setTitle("xillio content tools - " + Loader.LONGVERSION + " - Licensed to: " + license.getLicenseName());
	}
	}

	/**
	 * Display the release notes
	 */
	public void showReleaseNotes() {
	String lastVersion = settings.getSimpleSetting("LastVersion");

	if (lastVersion.compareTo(Loader.SHORTVERSION) < 0) {
		String notes = "";
		for (String[] element : Loader.HISTORY) {
		String v = element[0];
		if (lastVersion.compareTo(v) < 0) {
			notes += element[0] + " - " + element[1] + ": " + element[2] + "\n";
		} else {
			break;
		}

		if (notes.length() > 1000) {
			notes += "[...]";
			break;
		}
		}

		settings.saveSimpleSetting("LastVersion", Loader.SHORTVERSION);

		Alert releaseNotes = new Alert(AlertType.INFORMATION);
		releaseNotes.setHeaderText("Current version: " + Loader.SHORTVERSION);
		releaseNotes.setContentText(notes);
		releaseNotes.setTitle("Release notes");
		releaseNotes.show();
	}
	}

	@Override
	public void handle(final Event event) {

	if (event.getEventType() == KeyEvent.KEY_PRESSED) {
		KeyEvent keyEvent = (KeyEvent) event;

		if (KeyCombination.valueOf(FXController.HOTKEY_CLOSE).match(keyEvent)) {
		// We need to close the current tab
		RobotTab tab = (RobotTab) tpnBots.getSelectionModel().getSelectedItem();
		closeTab(tab);
		} else if (KeyCombination.valueOf(HOTKEY_NEW).match(keyEvent)) {
		buttonNewFile();
		} else if (KeyCombination.valueOf(HOTKEY_SAVE).match(keyEvent)) {
		RobotTab tab = (RobotTab) tpnBots.getSelectionModel().getSelectedItem();
		tab.save();
		} else if (KeyCombination.valueOf(HOTKEY_SAVEAS).match(keyEvent)) {
		RobotTab tab = (RobotTab) tpnBots.getSelectionModel().getSelectedItem();
		tab.save(true);
		} else if (KeyCombination.valueOf(HOTKEY_SAVEALL).match(keyEvent)) {
		tpnBots.getTabs().forEach(tab -> {
			if (tab != null && tab instanceof RobotTab) {
			((RobotTab) tab).save();
			}
		});
		} else if (KeyCombination.valueOf(HOTKEY_OPEN).match(keyEvent)) {
		buttonOpenFile();
		} else if (keyEvent.isControlDown() || keyEvent.isMetaDown()) {
		// Check if other key is an integer, if so open that tab
		try {
			int tab = Integer.parseInt(keyEvent.getText()) - 1;
			if (tab < tpnBots.getTabs().size() && tab >= 0) {
			tpnBots.getSelectionModel().select(tab);
			((RobotTab) tpnBots.getTabs().get(tab)).requestFocus();
			}
		} catch (NumberFormatException e) {
			// nevermind...
		}
		}
	}
	}

	/**
	 * Close a tab
	 *
	 * @param tab
	 */
	public void closeTab(final Tab tab) {
	closeTab(tab, true);
	}

	private void closeTab(final Tab tab, final boolean removeTab) {
	// Stop if we don't have a selected tab
	if (tab == null) {
		return;
	}

	// Check for onClose handlers
	EventHandler<Event> handler = tab.getOnCloseRequest();
	Event closeEvent = new Event(Tab.CLOSED_EVENT);
	if (handler != null) {
		handler.handle(closeEvent);
	}

	// Remove the tab
	if (!closeEvent.isConsumed() && removeTab) {
		tpnBots.getTabs().remove(tab);
	}
	}

	/**
	 * @return A list of active tabs
	 */
	public List<RobotTab> getTabs() {
	return tpnBots.getTabs().stream().map(tab -> (RobotTab) tab).collect(Collectors.toList());
	}

	/**
	 * Opens a tab if it can be found.
	 *
	 * @param tab
	 *        a tab to open
	 */
	public void showTab(final RobotTab tab) {
	int index = tpnBots.getTabs().indexOf(tab);

	if (index >= 0) {
		tpnBots.getSelectionModel().clearAndSelect(index);
	}
	}

	/**
	 * @return the pluginLoader
	 */
	public PluginLoader<PluginPackage> getPluginLoader() {
	return pluginLoader;
	}
	
	/**
	 * @return currently selected RobotTab
	 */
	public Tab getSelectedTab() { 
		return (tpnBots.getSelectionModel().getSelectedItem());
	}
	
	/**
	 * Finds the tab according to filePath (~RobotID.path)
	 * @param filePath filepath to robot (.xill) file
	 * @return RobotTab if found, otherwise null
	 */
	public Tab findTab(final File filePath) {
		final RobotTab[] robotTabs = {null};
		tpnBots.getTabs().forEach(tab -> {
			RobotTab robotTab = (RobotTab) tab;
			if (robotTab.getCurrentRobot().getPath().equals(filePath)) {
				robotTabs[0] = robotTab;
			}
		});
		return robotTabs[0];
	}
}
