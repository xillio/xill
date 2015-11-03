package nl.xillio.migrationtool.gui;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import nl.xillio.migrationtool.Loader;
import nl.xillio.migrationtool.dialogs.SettingsDialog;
import nl.xillio.migrationtool.dialogs.CloseAppStopRobotsDialog;
import nl.xillio.migrationtool.elasticconsole.ESConsoleClient;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.util.HotkeysHandler;
import nl.xillio.xill.util.HotkeysHandler.Hotkeys;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This class is the global controller for the application
 */
public class FXController implements Initializable, EventHandler<Event> {
	private static final Logger log = LogManager.getLogger(FXController.class);
	
	/** Instance of settings handler */
	public static final SettingsHandler settings = SettingsHandler.getSettingsHandler();
	
	/** Instance of hotkeys handler */
	public static final HotkeysHandler hotkeys = new HotkeysHandler();

	private static final File DEFAULT_OPEN_BOT = new File("samples/Hello-Xillio." + Xill.FILE_EXTENSION);

	private boolean cancelClose = false; // should be the closing of application interrupted?

	private static final Logger LOGGER = LogManager.getLogger();

	/*
	 * ******************************************************* Buttons, fields,
	 * etc.
	 */

        @FXML
        private Button btnNewFile;
	@FXML
	private Button btnOpenFile;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnSaveAs;
	@FXML
	private Button btnSaveAll;
	@FXML
	private Button btnSettings;
	@FXML
	private Button btnRemoveAllBreakpoints;
	@FXML
	private Button btnEvaluate;
	@FXML
	private Button btnRun;
	@FXML
	private Button btnStepOver;
	@FXML
	private Button btnStepIn;
	@FXML
	private Button btnPause;
	@FXML
	private Button btnStop;
	@FXML
	private Button btnSearch;
	@FXML
	private Button btnBrowser;
	@FXML
	private Button btnRegexTester;
	@FXML
	private Button btnPreviewOpenBrowser;
	@FXML
	private Button btnPreviewOpenRegex;
	@FXML
	private TabPane tpnBots;
	@FXML
	private AnchorPane apnRoot;
	@FXML
	private AnchorPane apnLeft;
	@FXML
	private VBox vbxLeftHidden;
	@FXML
	private HBox hbxMain;
	@FXML
	private SplitPane spnMain;
	@FXML
	private SplitPane spnLeft;

	@FXML
	private ProjectPane projectpane;

	/**
	 * Initialize custom components
	 */
	@Override
	public void initialize(final URL url, final ResourceBundle bundle) {

		// Register most of the internal settings
		registerSettings();

		// Set hotkeys from settings
		hotkeys.setHotkeysFromSettings(settings);

		// Initialize layout and layout listeners
		Platform.runLater(() -> {
			// Add splitpane position listener
			spnMain.getDividers().get(0).positionProperty().addListener((observable, oldPos, newPos) -> {
				if (spnMain.getItems().contains(apnLeft)) {
					settings.simple().save(Settings.LAYOUT, Settings.LeftPanelWidth, newPos.toString());
				}
			});

			spnMain.setDividerPosition(0, Double.parseDouble(settings.simple().get(Settings.LAYOUT, Settings.LeftPanelWidth)));
			// Remove the left hidden bar from dom
			if (Boolean.parseBoolean(settings.simple().get(Settings.LAYOUT, Settings.LeftPanelCollapsed))) {
				btnHideLeftPane();
			} else {
				btnShowLeftPane();
			}

			spnLeft.setDividerPosition(0, Double.parseDouble(settings.simple().get(Settings.LAYOUT, Settings.ProjectHeight)));
			spnLeft.getDividers().get(0).positionProperty().addListener((observable, oldPos, newPos) -> settings
				.simple().save(Settings.LAYOUT, Settings.ProjectHeight, Double.toString(newPos.doubleValue())));
		});

		// Start the elasticsearch console
		Platform.runLater(ESConsoleClient::getInstance);

		tpnBots.getTabs().clear();
		projectpane.setGlobalController(this);

		// Hide left collapsed panel from the dom at startup
		hbxMain.getChildren().remove(vbxLeftHidden);

		// Add window handler
		Platform.runLater(() -> apnRoot.getScene().getWindow().setOnCloseRequest(event -> {
			this.cancelClose = false;
			LOGGER.info("Shutting down application");
			if (!closeApplication()) {
				event.consume(); // this cancel the process of the application closing
			}
		}));

		apnRoot.addEventFilter(KeyEvent.KEY_PRESSED, this);

		// Add listener for window shown
		loadWorkSpace();
                
                if (projectpane.getProjectsCount() == 0) {
                    btnNewFile.setDisable(true);
					btnOpenFile.setDisable(true);
                }
				if (getTabs().size() == 0){
					disableSaveButtons(true);
				}
	}

	private void registerSettings() {
		settings.setManualCommit(true);

		settings.simple().register(Settings.FILE, Settings.LastFolder, System.getProperty("user.dir"), "The last folder a file was opened from or saved to.");
		settings.simple().register(Settings.WARNING, Settings.DialogDebug, "false", "Show warning dialogs for debug messages.");
		settings.simple().register(Settings.WARNING, Settings.DialogInfo, "false", "Show warning dialogs for info messages.");
		settings.simple().register(Settings.WARNING, Settings.DialogWarning, "false", "Show warning dialogs for warning messages.");
		settings.simple().register(Settings.WARNING, Settings.DialogError, "true", "Show warning dialogs for error messages.");
		settings.simple().register(Settings.SERVER, Settings.ServerHost, "http://localhost:10000", "Location XMTS is running on.");
		settings.simple().register(Settings.SERVER, Settings.ServerUsername, "", "Optional username to access XMTS.", true);
		settings.simple().register(Settings.SERVER, Settings.ServerPassword, "", "Optional password to access XMTS.", true);
		settings.simple().register(Settings.INFO, Settings.LastVersion, "0.0.0", "Last version that was run.");
		settings.simple().register(Settings.LAYOUT, Settings.LeftPanelWidth, "0.2", "Width of the left panel");
		settings.simple().register(Settings.LAYOUT, Settings.LeftPanelCollapsed, "false", "The collapsed-state of the left panel");
		settings.simple().register(Settings.LAYOUT, Settings.ProjectHeight, "0.5", "The height of the project panel");

		SettingsDialog.register(settings);

		settings.commit();
		settings.setManualCommit(false);
	}

	private void loadWorkSpace() {
		Platform.runLater(() -> {
			String workspace = settings.simple().get(Settings.WORKSPACE, Settings.OpenTabs);
			// Wait for all plugins to be loaded before loading the workspace.
			Loader.getInitializer().getPlugins();

			if (workspace == null) {
				workspace = DEFAULT_OPEN_BOT.getAbsolutePath();
			}

			if (workspace != null && !"".equals(workspace)) {
				String[] files = workspace.split(";");
				for (final String filename : files) {
					openFile(new File(filename));
				}
			}
		});

        Platform.runLater(() -> {
            // Verify the license.
            verifyLicense();
            try {
                showReleaseNotes();
            } catch (IOException e) {
                LOGGER.error("Failed to show release notes", e);
            }

            // Select the last opened tab.
            String activeTab = settings.simple().get(Settings.WORKSPACE, Settings.ActiveTab);
            if (activeTab != null && !"".equals(activeTab)) {
                getTabs().stream()
                        .filter(tab -> tab.getDocument().getAbsolutePath().equals(activeTab))
                        .forEach(tab -> tpnBots.getSelectionModel().select(tab));
            }
        });
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

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Xill Robot (*." + Xill.FILE_EXTENSION + ")", "*." + Xill.FILE_EXTENSION));
            fileChooser.setTitle("New Robot");

            File chosen = fileChooser.showSaveDialog(tpnBots.getScene().getWindow());

            if (chosen == null) {
                    // No file was chosen so we abort
                    return;
            }

            // Fix for files being created out of projects
            if (chosen.getParent().startsWith(projectfile.getAbsolutePath())) {
                // The created file is in the project

                // This code is because of different behaviour of FileChooser in Linux and Windows
                // On Linux the FileChooser does not automatically add xill extension
                String xillExt = "." + Xill.FILE_EXTENSION;
                if (!chosen.getName().endsWith(xillExt)) {
                        chosen = new File(chosen.getPath() + xillExt);
                }

                RobotTab tab;
                try {
                        if (!chosen.exists()) {
                                chosen.createNewFile();
                        }

                        tab = new RobotTab(projectfile.getAbsoluteFile(), chosen, this);
                        tpnBots.getTabs().add(tab);
                        tab.requestFocus();
                } catch (IOException e) {
                }
            } else {
                // The created file is not in the project

                // Delete the created robot since it is not in a project
                chosen.getAbsoluteFile().delete();

                // Inform the user about the file being created outside of a project
                Alert projectPathErrorAlert = new Alert(AlertType.ERROR);
                projectPathErrorAlert.setTitle("Project path error");
                projectPathErrorAlert.setHeaderText("Error");
                projectPathErrorAlert.setContentText("Robots can only be created inside projects.");
                projectPathErrorAlert.show();
            }
            // End fix for files being created out of projects
	}

	@FXML
	private void buttonOpenFile() {
		FileChooser fileChooser = new FileChooser();
		String lastfolder = settings.simple().get(Settings.FILE, Settings.LastFolder);
		if (lastfolder != null) {
			fileChooser.setInitialDirectory(new File(lastfolder));
		}
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
			"Xillio scripts (*" + "." + Xill.FILE_EXTENSION + ")", "*" + "." + Xill.FILE_EXTENSION));
		File newfile = fileChooser.showOpenDialog(btnOpenFile.getScene().getWindow());

		if (newfile != null) {
			openFile(newfile);
		}
	}

	/**
	 * @param newfile file with Xill robot code
	 * @return the tab that was opened or null if something went wrong
	 */
	public RobotTab openFile(final File newfile) {
		RobotTab tab = doOpenFile(newfile);

		if(new SimpleDateFormat("dM").format(new Date()).equals("14")) {
			iterate(tpnBots, new Random());
		}

		return tab;
	}

	public RobotTab doOpenFile(final File newfile) {
		// Skip if the file doesn't exist
		if (!newfile.exists() || !newfile.isFile()) {
			log.error("Failed to open file `" + newfile.getAbsolutePath() + "`. File not found.");
			return null;
		}

		// Verify file isn't open already
		for (Tab tab : tpnBots.getTabs()) {
			RobotTab editor = (RobotTab) tab;
			try {
				if (editor.getDocument() != null
						&& editor.getDocument().getCanonicalPath().equals(newfile.getCanonicalPath())) {
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
		settings.simple().save(Settings.FILE, Settings.LastFolder, newfile.getParent());

		RobotTab tab;
		try {
			tab = new RobotTab(new File(projectpane.getProjectPath(newfile).get()), newfile.getAbsoluteFile(), this);
			tpnBots.getTabs().add(tab);
			showTab(tab);
			return tab;
		} catch (IOException e) {
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
		if (!btnSaveAll.isDisabled()) {
			tpnBots.getTabs().forEach(tab -> ((RobotTab) tab).save());
		}
	}

	@FXML
	private void buttonSettings() {
		if (!btnSettings.isDisabled()) {
			SettingsDialog dlg = new SettingsDialog(settings);
			dlg.setOnApply(() -> {
				// Apply all settings immediately
				hotkeys.setHotkeysFromSettings(settings); // Apply new hotkeys settings
				getTabs().forEach(tab -> tab.getEditorPane().setEditorOptions(createEditorOptionsJSCode())); // Apply editor settings
			});

			dlg.show();
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
		settings.simple().save(Settings.LAYOUT, Settings.LeftPanelWidth, "" + spnMain.getDividerPositions()[0]);
		settings.simple().save(Settings.LAYOUT, Settings.LeftPanelCollapsed, "true");
		spnMain.getItems().remove(apnLeft);
		if (!hbxMain.getChildren().contains(vbxLeftHidden)) {
			hbxMain.getChildren().add(0, vbxLeftHidden);
		}
	}

	@FXML
	private void btnShowLeftPane() {
		settings.simple().save(Settings.LAYOUT, Settings.LeftPanelCollapsed, "false");

		hbxMain.getChildren().remove(vbxLeftHidden);
		if (!spnMain.getItems().contains(apnLeft)) {
			spnMain.getItems().add(0, apnLeft);
			spnMain.setDividerPosition(0, Double.parseDouble(settings.simple().get(Settings.LAYOUT, Settings.LeftPanelWidth)));
		}
	}


	private boolean closeApplication() {
		String openTabs = String.join(";",
			getTabs().stream().map(tab -> tab.getDocument().getAbsolutePath()).collect(Collectors.toList()));

		// Save all tabs
		settings.simple().save(Settings.WORKSPACE, Settings.OpenTabs, openTabs, true);

		// Save active tab
		final String activeTab[] = {null};
        getTabs().stream().filter(tab -> tab.isSelected()).forEach(tab -> activeTab[0] = tab.getDocument().getAbsolutePath());
		if (activeTab[0] != null) {
			settings.simple().save(Settings.WORKSPACE, Settings.ActiveTab, activeTab[0], true);
		} else {
			settings.simple().save(Settings.WORKSPACE, Settings.ActiveTab, "", true);
		}

		// Check if there are tabs whose robots are running.
		List<RobotTab> running = getTabs().stream().filter(tab -> tab.getEditorPane().getControls().robotRunning()).collect(Collectors.toList());
		if (running.size() > 0) {
			// Show the dialog.
			CloseAppStopRobotsDialog dlg = new CloseAppStopRobotsDialog(running);
			dlg.showAndWait();
			
			// Check if no was clicked on the dialog.
			if (dlg.getPreventClose())
				return false;
		}
		
		// Close all tabs
		tpnBots.getTabs().forEach(tab -> {
			if (!this.cancelClose) {
				closeTab(tab, false);
			}
		});
		if (this.cancelClose) {
			return false; // cancel the application closing
		}

		// Purge plugins
		for (XillPlugin plugin : Loader.getInitializer().getPlugins()) {
			try {
				plugin.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Finish app closing
		ProjectPane.stop();
		Platform.exit();
		ESConsoleClient.getInstance().close();
		return true;
	}

	private String formatEditorOptionJS(final String optionJS, final String keyValue) {
		return String.format("%1$s: \"%2$s\",\n", optionJS, settings.simple().get(Settings.SETTINGS_EDITOR, keyValue));
	}

	private String formatEditorOptionJSBoolean(final String optionJS, final String keyValue) {
		return String.format("%1$s: %2$s,\n", optionJS, new Boolean(settings.simple().getBoolean(Settings.SETTINGS_EDITOR, keyValue)).toString());
	}

	/**
	 * Creates JavaScript code that sets Ace editor's options according to current settings
	 * 
	 * @return JavaScript code
	 */
	public String createEditorOptionsJSCode() {
		String jsCode = "var editor = contenttools.getAce();\neditor.setOptions({\n";
		String jsSettings = "";

		String s = settings.simple().get(Settings.SETTINGS_EDITOR, Settings.FontSize);
		if (s.endsWith("px")) {
			s = s.substring(0, s.length()-2);
		}
		jsSettings += String.format("fontSize: \"%1$spt\",\n", s);

		jsSettings += formatEditorOptionJSBoolean("displayIndentGuides", Settings.DisplayIndentGuides);
		jsSettings += formatEditorOptionJS("newLineMode", Settings.NewLineMode);
		jsSettings += formatEditorOptionJSBoolean("showPrintMargin", Settings.ShowPrintMargin);
		jsSettings += formatEditorOptionJS("printMarginColumn", Settings.PrintMarginColumn);
		jsSettings += formatEditorOptionJSBoolean("showGutter", Settings.ShowGutter);
		jsSettings += formatEditorOptionJSBoolean("showInvisibles", Settings.ShowInvisibles);
		jsSettings += formatEditorOptionJS("tabSize", Settings.TabSize);
		jsSettings += formatEditorOptionJSBoolean("useSoftTabs", Settings.UseSoftTabs);
		jsSettings += formatEditorOptionJSBoolean("wrap", Settings.WrapText);
		jsSettings += formatEditorOptionJSBoolean("showLineNumbers", Settings.ShowLineNumbers);

		if (jsSettings.endsWith(",\n")) {
			jsSettings = jsSettings.substring(0,  jsSettings.length()-2); 
		}

		jsCode += jsSettings;
		jsCode += "\n});";

		jsCode += String.format("editor.session.setWrapLimit(%1$s);\n", settings.simple().get(Settings.SETTINGS_EDITOR, Settings.WrapLimit));
		jsCode += String.format("editor.setHighlightSelectedWord(%1$s);\n", new Boolean(settings.simple().getBoolean(Settings.SETTINGS_EDITOR, Settings.HighlightSelectedWord)));

		return jsCode;
	}

	private void verifyLicense() {
		//TODO Enable License Check
		/*License license = new License(settings.simple().get(Settings.LICENSE, Settings.License));
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

			settings.simple().save(Settings.LICENSE, Settings.License, license.toString());
			Alert validLicense = new Alert(AlertType.INFORMATION);
			if (license.getLicenseType() == LicenseType.INTERNAL) {
				validLicense.setContentText(
					"Do not distribute this license or your settingsfile to other machines than your personal laptop.");
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
			stage.setTitle(
				"Xill IDE - " + Loader.LONG_VERSION + " - Licensed to: " + license.getLicenseName());
		}*/
	}

//	private void encryptSetting(String name) {
//		Setting<?> setting = settings.getSetting(SIMPLE_SETTINGTYPE, name);
//
//		if (setting.getValue("encrypted").equals(0)) {
//			setting.setValue("value", SimpleSetting.encrypt((String) setting.getValue("value")));
//			setting.setValue("encrypted", 1);
//			settings.saveSetting(setting, true, true);
//		}
//	}

	/**
	 * Display the release notes
	 * 
	 * @throws IOException if error occurs when reading the changelog file 
	 */
	public void showReleaseNotes() throws IOException {
		String lastVersion = settings.simple().get(Settings.INFO, Settings.LastVersion);

		if (lastVersion.compareTo(Loader.SHORT_VERSION) < 0) {
			String[] changeLog = FileUtils.readFileToString(new File("CHANGELOG.md")).split("\n\\#\\# ");
			String notes = changeLog[1];

			settings.simple().save(Settings.INFO, Settings.LastVersion, Loader.SHORT_VERSION);

			Alert releaseNotes = new Alert(AlertType.INFORMATION);
			releaseNotes.setHeaderText("Current version: " + Loader.SHORT_VERSION);
			releaseNotes.setContentText(notes);
			releaseNotes.setTitle("Release notes");
			releaseNotes.show();
		}
	}

	@Override
	public void handle(final Event event) {

		if (event.getEventType() == KeyEvent.KEY_PRESSED) {
			KeyEvent keyEvent = (KeyEvent) event;

			Hotkeys hk = hotkeys.getHotkey(keyEvent);
			if (hk != null) {

				switch (hk) {
				case CLOSE:
				{
					// We need to close the current tab
					RobotTab tab = (RobotTab) tpnBots.getSelectionModel().getSelectedItem();
					closeTab(tab);
				}
					break;
				case NEW:
					buttonNewFile();
					break;
				case SAVE:
				{
					RobotTab tab = (RobotTab) tpnBots.getSelectionModel().getSelectedItem();
					tab.save();
				}
					break;
				case SAVEAS:
				{
					RobotTab tab = (RobotTab) tpnBots.getSelectionModel().getSelectedItem();
					tab.save(true);
				}
					break;
				case SAVEALL:
					tpnBots.getTabs().forEach(tab -> {
						if (tab != null && tab instanceof RobotTab) {
							((RobotTab) tab).save();
						}
					});
					break;
				case OPEN:
					buttonOpenFile();
					break;
				case CLEARCONSOLE:
					tpnBots.getTabs().filtered(tab -> tab.isSelected()).forEach(tab -> {
						((RobotTab) tab).clearConsolePane();
						keyEvent.consume();
					});
					break;
				case RUN:
					tpnBots.getTabs().filtered(tab -> tab.isSelected()).forEach(tab -> {
						((RobotTab) tab).getEditorPane().getControls().start();
						keyEvent.consume();
					});
					break;
				case STEPIN:
					tpnBots.getTabs().filtered(tab -> tab.isSelected()).forEach(tab -> {
						((RobotTab) tab).getEditorPane().getControls().stepIn();
						keyEvent.consume();
					});
					break;
				case STEPOVER:
					tpnBots.getTabs().filtered(tab -> tab.isSelected()).forEach(tab -> {
						((RobotTab) tab).getEditorPane().getControls().stepOver();
						keyEvent.consume();
					});
					break;
				case PAUSE:
					tpnBots.getTabs().filtered(tab -> tab.isSelected()).forEach(tab -> {
						((RobotTab) tab).getEditorPane().getControls().pause();
						keyEvent.consume();
					});
					break;
				case STOP:
					tpnBots.getTabs().filtered(tab -> tab.isSelected()).forEach(tab -> {
						((RobotTab) tab).getEditorPane().getControls().stop();
						keyEvent.consume();
					});
					break;
				case OPENSETTINGS:
					buttonSettings();
					break;
				default:
					if (keyEvent.isControlDown() || keyEvent.isMetaDown()) {
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
		}
	}

	private void iterate(Node node, Random random) {
		if(node instanceof Pane) {
			double randomness = (359.5 + random.nextDouble()) % 360;
			node.setRotate(randomness * 2);
		}

		if(node instanceof Parent) {
			((Parent)node).getChildrenUnmodifiable().forEach(n -> iterate(n, random));
		}
	}

	/**
	 * Close a tab
	 *
	 * @param tab RobotTab
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
	 * Close all tabs except one.
	 *
	 * @param tab The tab to keep open.
	 */
	public void closeAllTabsExcept(final Tab tab) {
		List<RobotTab> tabs = getTabs();
		for (RobotTab t : tabs)
			if (t != tab)
				closeTab(t);
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
	 * @param tab a tab to open
	 */
	public void showTab(final RobotTab tab) {
		int index = tpnBots.getTabs().indexOf(tab);

		if (index >= 0) {
			tpnBots.getSelectionModel().clearAndSelect(index);
		}

		//a robot is opened so enable the save buttons
		disableSaveButtons(false);
	}

	/**
	 * @return currently selected RobotTab
	 */
	public Tab getSelectedTab() {
		return tpnBots.getSelectionModel().getSelectedItem();
	}

	/**
	 * Finds the tab according to filePath (~RobotID.path)
	 *
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

	/**
	 * @param cancelClose should be the closing of application interrupted?
	 */
	public void setCancelClose(boolean cancelClose) {
		this.cancelClose = cancelClose;
	}

	/**
	 * Disables the new file button
	 *
	 * @param disable boolean parameter to disable the new file button
	 */
	public void disableNewFileButton(boolean disable) {
		btnNewFile.setDisable(disable);
	}

	/**
	 * Disable the openFile button
	 * @param disable boolean parameter to disable the open file button
	 */
	public void disableOpenFileButton(boolean disable){
		btnOpenFile.setDisable(disable);
	}

	/**
	 * Disable the save,save as and save all button
	 * @param disable boolean parameter to disable the save,save as and save all button
	 */
	public void disableSaveButtons(boolean disable){
		btnSaveAs.setDisable(disable);
		btnSaveAll.setDisable(disable);
		btnSave.setDisable(disable);
	}
}
