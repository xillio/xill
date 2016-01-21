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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import nl.xillio.migrationtool.ApplicationKillThread;
import nl.xillio.migrationtool.LicenseUtils;
import nl.xillio.migrationtool.Loader;
import nl.xillio.migrationtool.dialogs.CloseAppStopRobotsDialog;
import nl.xillio.migrationtool.dialogs.SettingsDialog;
import nl.xillio.migrationtool.elasticconsole.ESConsoleClient;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.RobotID;
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

    /**
     * Instance of settings handler
     */
    public static final SettingsHandler settings = SettingsHandler.getSettingsHandler();

    /**
     * Instance of hotkeys handler
     */
    public static final HotkeysHandler hotkeys = new HotkeysHandler();

    private static final File DEFAULT_OPEN_BOT = new File("samples/Hello-Xillio." + Xill.FILE_EXTENSION);

    private boolean cancelClose = false; // should be the closing of application interrupted?

    private static final Logger LOGGER = LogManager.getLogger();

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
    @FXML
    private Button btnNearExpiry;

    private ReturnFocusListener returnFocusListener;
    private SettingsDialog settingsDialog;

    /**
     * Initialize custom components
     */
    @Override
    public void initialize(final URL url, final ResourceBundle bundle) {

        // Add listener for license change.
        LicenseUtils.getOnLicenseChange().add(this::checkLicenseNearExpiry);

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
            LOGGER.info("Shutting down application.");
            if (!closeApplication()) {
                event.consume(); // this cancel the process of the application closing
            }
        }));

        apnRoot.addEventFilter(KeyEvent.KEY_PRESSED, this);

        // Add listener for window shown
        loadWorkSpace();

        Platform.runLater(() -> {
            returnFocusListener = new ReturnFocusListener(apnRoot.getScene());
            buttonsReturnFocus(apnRoot);
        });

        if (projectpane.getProjectsCount() == 0) {
            btnNewFile.setDisable(true);
            btnOpenFile.setDisable(true);
        }
        if (getTabs().size() == 0) {
            disableSaveButtons(true);
        }
    }

    private void createSettingsWindow() {
        settingsDialog = new SettingsDialog(settings);
        settingsDialog.setOnApply(() -> {
            // Apply all settings immediately
            hotkeys.setHotkeysFromSettings(settings); // Apply new hotkeys settings
            getTabs().forEach(tab -> tab.getEditorPane().setEditorOptions(createEditorOptionsJSCode())); // Apply editor settings
        });
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

            if (!"".equals(workspace)) {
                String[] files = workspace.split(";");
                for (final String filename : files) {
                    openFile(new File(filename));
                }
            }
        });

        Platform.runLater(() -> {
            // Verify the license.
            if (!LicenseUtils.performValidation(false)) {
                closeApplication();
            }

            try {
                showReleaseNotes();
            } catch (IOException e) {
                LOGGER.error("Failed to show release notes: " + e.getMessage(), e);
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

    private void checkLicenseNearExpiry() {
        // Check if the licence is about to expire.
        long daysLeft = LicenseUtils.daysToExpiration();
        if (daysLeft <= LicenseUtils.DAYS_NEAR_EXPIRATION) {
            btnNearExpiry.setText(daysLeft + " Day" + (daysLeft > 1 ? "s" : "") + " until license expires.");
            btnNearExpiry.setVisible(true);
        } else {
            btnNearExpiry.setVisible(false);
        }
    }

    /**
     * Create a new robot file.
     */
    @FXML
    private void buttonNewFile() {
        if (!btnNewFile.isDisabled())
            projectpane.newFile();
    }

    /**
     * Switch focus to an existing tab for a certain robot or create it.
     *
     * @param robotID the robot that should be viewed
     */
    public void viewOrOpenRobot(RobotID robotID) {
        RobotTab tab = tpnBots.getTabs().stream()
                .map(bot -> (RobotTab) bot)
                .filter(robotTab -> robotTab.getProcessor().getRobotID().equals(robotID))
                .map(this::reloadTab)
                .findAny()
                .orElseGet(() -> createTab(robotID.getPath(), robotID.getProjectPath()));

        if (tab != null) {
            tab.requestFocus();
        }

    }

    /**
     * Try to make a tab and add it to the view.
     *
     * @param robotFile     the file that should be visible in the tab
     * @param projectFolder the project folder for the robot
     * @return the tab or null if it could not be created
     */
    private RobotTab createTab(File robotFile, File projectFolder) {
        try {
            RobotTab robotTab = new RobotTab(projectFolder, robotFile, this);
            tpnBots.getTabs().add(robotTab);
            return robotTab;
        } catch (IOException e) {
            LOGGER.error("Could not create robot tab", e);
            return null;
        }
    }

    private RobotTab reloadTab(RobotTab robotTab) {
        robotTab.reload();
        return robotTab;
    }

    @FXML
    private void buttonOpenFile() {
        // Check if the button is enabled.
        if (btnOpenFile.isDisabled()) {
            return;
        }

        // If the last picked folder exists, set the initial directory to that.
        FileChooser fileChooser = new FileChooser();
        File lastFolder = new File(settings.simple().get(Settings.FILE, Settings.LastFolder));
        if (lastFolder.exists()) {
            fileChooser.setInitialDirectory(lastFolder);
        }

        // Only show Xill scripts, show the dialog and open the file.
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Xillio scripts (*" + "." + Xill.FILE_EXTENSION + ")", "*" + "." + Xill.FILE_EXTENSION));
        File newFile = fileChooser.showOpenDialog(btnOpenFile.getScene().getWindow());

        if (newFile != null) {
            openFile(newFile);
        }
    }

    /**
     * @param newfile file with Xill robot code
     * @return the tab that was opened or null if something went wrong
     */
    public RobotTab openFile(final File newfile) {
        RobotTab tab = doOpenFile(newfile);

        if ("14".equals(new SimpleDateFormat("dM").format(new Date()))) {
            iterate(tpnBots, new Random());
        }

        return tab;
    }

    public RobotTab doOpenFile(final File newfile) {
        // Skip if the file doesn't exist
        if (!newfile.exists() || !newfile.isFile()) {
            LOGGER.error("Failed to open file `" + newfile.getAbsolutePath() + "`. File not found.");
            return null;
        }

        // Verify file isn't open already
        for (Tab tab : tpnBots.getTabs()) {
            RobotTab editor = (RobotTab) tab;
            try {
                if (editor.getDocument() != null && editor.getDocument().getCanonicalPath().equals(newfile.getCanonicalPath())) {
                    tpnBots.getSelectionModel().select(editor);

                    showTab(editor);
                    editor.requestFocus();

                    return editor;
                }
            } catch (IOException e) {
                LOGGER.error("Error while opening file: " + e.getMessage(), e);
                return null;
            }
        }

        // Tab is not open yet: open new tab
        settings.simple().save(Settings.FILE, Settings.LastFolder, newfile.getParent());

        RobotTab tab;
        try {
            tab = new RobotTab(new File(projectpane.getProjectPath(newfile).orElse("")), newfile.getAbsoluteFile(), this);
            tpnBots.getTabs().add(tab);
            tab.requestFocus();
            return tab;
        } catch (IOException e) {
            LOGGER.error("Failed to perform operation: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Make all buttons that are children of the given node return focus to the previously focused node after receiving focus
     *
     * @param node The node to search for buttons in
     */
    protected void buttonsReturnFocus(Node node) {
        // For some nodes (like SplitPane) children are added by the skin
        // Apply CSS to make sure all children are present
        node.applyCss();

        node.lookupAll(".button")
                .forEach(n ->
                        n.focusedProperty().addListener(
                                returnFocusListener));

        // MenuButtons do not seem to get children, only items. Handle them as a special case.
        node.lookupAll(".menu-button").forEach(mb -> {
            if (mb instanceof MenuButton) {
                ((MenuButton) mb).showingProperty().not().addListener(returnFocusListener);
            }
        });
        node.lookupAll(".toggle-button").forEach(tb -> tb.focusedProperty().addListener(returnFocusListener));

        //maybe need to add more here... since some things do not yet return focus.
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
            createSettingsWindow();
            settingsDialog.show();
        }
    }

    @FXML
    private void buttonSearch() {
    }

    @FXML
    private void buttonBrowser() {
    }

    @FXML
    private void buttonRegexTester() {
    }

    @FXML
    private void buttonPreviewOpenBrowser() {
    }

    @FXML
    private void buttonPreviewOpenRegex() {
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

        RobotTab selected = (RobotTab) getSelectedTab();
        if (selected != null)
            selected.requestFocus();
    }

    @FXML
    private void buttonNearExpiry() {
        buttonSettings();
        settingsDialog.selectLicenceTab();
    }

    private boolean closeApplication() {
        String openTabs = String.join(";",
                getTabs().stream().map(tab -> tab.getDocument().getAbsolutePath()).collect(Collectors.toList()));

        // Save all tabs
        settings.simple().save(Settings.WORKSPACE, Settings.OpenTabs, openTabs, true);

        // Save active tab
        final String activeTab[] = {null};
        getTabs().stream().filter(Tab::isSelected).forEach(tab -> activeTab[0] = tab.getDocument().getAbsolutePath());
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
                LOGGER.error("Error while closing plugin: " + e.getMessage(), e);
            }
        }

        // Finish app closing
        ProjectPane.stop();
        Platform.exit();
        ESConsoleClient.getInstance().close();
        ApplicationKillThread.exit();
        return true;
    }

    private String formatEditorOptionJSRaw(final String optionJS, final String keyValue) {
        return String.format("%1$s: %2$s,%n", optionJS, settings.simple().get(Settings.SETTINGS_EDITOR, keyValue));
    }

    private String formatEditorOptionJS(final String optionJS, final String keyValue) {
        return String.format("%1$s: \"%2$s\",%n", optionJS, settings.simple().get(Settings.SETTINGS_EDITOR, keyValue));
    }

    private String formatEditorOptionJSBoolean(final String optionJS, final String keyValue) {
        return String.format("%1$s: %2$s,%n", optionJS, Boolean.toString(settings.simple().getBoolean(Settings.SETTINGS_EDITOR, keyValue)));
    }

    /**
     * Creates JavaScript code that sets Ace editor's options according to current settings
     *
     * @return JavaScript code
     */
    public String createEditorOptionsJSCode() {
        String jsCode = "var editor = javaEditor.getAce();\neditor.setOptions({\n";
        String jsSettings = "";

        String s = settings.simple().get(Settings.SETTINGS_EDITOR, Settings.FontSize);
        if (s.endsWith("px")) {
            s = s.substring(0, s.length() - 2);
        }
        jsSettings += String.format("fontSize: \"%1$spt\",%n", s);

        jsSettings += formatEditorOptionJSBoolean("displayIndentGuides", Settings.DisplayIndentGuides);
        jsSettings += formatEditorOptionJS("newLineMode", Settings.NewLineMode);
        jsSettings += formatEditorOptionJSBoolean("showPrintMargin", Settings.ShowPrintMargin);
        jsSettings += formatEditorOptionJS("printMarginColumn", Settings.PrintMarginColumn);
        jsSettings += formatEditorOptionJSBoolean("showGutter", Settings.ShowGutter);
        jsSettings += formatEditorOptionJSBoolean("showInvisibles", Settings.ShowInvisibles);
        jsSettings += formatEditorOptionJSRaw("tabSize", Settings.TabSize);
        jsSettings += formatEditorOptionJSBoolean("useSoftTabs", Settings.UseSoftTabs);
        jsSettings += formatEditorOptionJSBoolean("wrap", Settings.WrapText);
        jsSettings += formatEditorOptionJSBoolean("showLineNumbers", Settings.ShowLineNumbers);

        if (jsSettings.endsWith(",\n")) {
            jsSettings = jsSettings.substring(0, jsSettings.length() - 2);
        }

        jsCode += jsSettings;
        jsCode += "\n});";

        jsCode += String.format("editor.session.setWrapLimit(%1$s);%n", settings.simple().get(Settings.SETTINGS_EDITOR, Settings.WrapLimit));
        jsCode += String.format("editor.setHighlightSelectedWord(%1$s);%n", settings.simple().getBoolean(Settings.SETTINGS_EDITOR, Settings.HighlightSelectedWord));

        return jsCode; //return jsCode;
    }


    /**
     * Display the release notes
     *
     * @throws IOException if error occurs when reading the changelog file
     */
    public void showReleaseNotes() throws IOException {
        String lastVersion = settings.simple().get(Settings.INFO, Settings.LastVersion);

        if (lastVersion.compareTo(Loader.SHORT_VERSION) < 0) {
            String[] changeLog = FileUtils.readFileToString(new File("CHANGELOG.md")).split("\n## ");
            String notes = changeLog[1];

            settings.simple().save(Settings.INFO, Settings.LastVersion, Loader.SHORT_VERSION);

            Alert releaseNotes = new Alert(AlertType.INFORMATION);
            releaseNotes.initModality(Modality.APPLICATION_MODAL);
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
                        closeTab(tpnBots.getSelectionModel().getSelectedItem());
                        break;
                    case NEW:
                        buttonNewFile();
                        break;
                    case SAVE:
                        buttonSave();
                        break;
                    case SAVEAS:
                        buttonSaveAs();
                        break;
                    case SAVEALL:
                        buttonSaveAll();
                        break;
                    case OPEN:
                        buttonOpenFile();
                        break;
                    case CLEARCONSOLE:
                        tpnBots.getTabs().filtered(Tab::isSelected).forEach(tab -> {
                            ((RobotTab) tab).clearConsolePane();
                            keyEvent.consume();
                        });
                        break;
                    case RUN:
                        tpnBots.getTabs().filtered(Tab::isSelected).forEach(tab -> {
                            ((RobotTab) tab).getEditorPane().getControls().start();
                            keyEvent.consume();
                        });
                        break;
                    case STEPIN:
                        tpnBots.getTabs().filtered(Tab::isSelected).forEach(tab -> {
                            ((RobotTab) tab).getEditorPane().getControls().stepIn();
                            keyEvent.consume();
                        });
                        break;
                    case STEPOVER:
                        tpnBots.getTabs().filtered(Tab::isSelected).forEach(tab -> {
                            ((RobotTab) tab).getEditorPane().getControls().stepOver();
                            keyEvent.consume();
                        });
                        break;
                    case PAUSE:
                        tpnBots.getTabs().filtered(Tab::isSelected).forEach(tab -> {
                            ((RobotTab) tab).getEditorPane().getControls().pause();
                            keyEvent.consume();
                        });
                        break;
                    case STOP:
                        tpnBots.getTabs().filtered(Tab::isSelected).forEach(tab -> {
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
        if (node instanceof Pane) {
            double randomness = (359.5 + random.nextDouble()) % 360;
            node.setRotate(randomness * 2);
        }

        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(n -> iterate(n, random));
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
        tabs.stream().filter(t -> t != tab).forEach(this::closeTab);
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
     *
     * @param disable boolean parameter to disable the open file button
     */
    public void disableOpenFileButton(boolean disable) {
        btnOpenFile.setDisable(disable);
    }

    /**
     * Disable the save,save as and save all button
     *
     * @param disable boolean parameter to disable the save,save as and save all button
     */
    public void disableSaveButtons(boolean disable) {
        btnSaveAs.setDisable(disable);
        btnSaveAll.setDisable(disable);
        btnSave.setDisable(disable);
    }
}
