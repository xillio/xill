package nl.xillio.migrationtool.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import nl.xillio.migrationtool.Loader;
import nl.xillio.migrationtool.dialogs.CloseTabStopRobotDialog;
import nl.xillio.migrationtool.dialogs.SaveBeforeClosingDialog;
import nl.xillio.migrationtool.gui.EditorPane.DocumentState;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;

/**
 * A tab containing the editor, console and debug panel attached to a specific currentRobot.
 */
public class RobotTab extends Tab implements Initializable, ChangeListener<DocumentState> {
	private static final Logger log = LogManager.getLogger();
	private static final SettingsHandler settings = SettingsHandler.getSettingsHandler();

	private static final String PATH_STATUSICON_RUNNING = "M256,92.481c44.433,0,86.18,17.068,117.553,48.064C404.794,171.411,422,212.413,422,255.999 s-17.206,84.588-48.448,115.455c-31.372,30.994-73.12,48.064-117.552,48.064s-86.179-17.07-117.552-48.064 C107.206,340.587,90,299.585,90,255.999s17.206-84.588,48.448-115.453C169.821,109.55,211.568,92.481,256,92.481 M256,52.481 c-113.771,0-206,91.117-206,203.518c0,112.398,92.229,203.52,206,203.52c113.772,0,206-91.121,206-203.52 C462,143.599,369.772,52.481,256,52.481L256,52.481z M206.544,357.161V159.833l160.919,98.666L206.544,357.161z";
	private static final String PATH_STATUSICON_PAUSED = "M256,92.481c44.433,0,86.18,17.068,117.553,48.064C404.794,171.411,422,212.413,422,255.999 s-17.206,84.588-48.448,115.455c-31.372,30.994-73.12,48.064-117.552,48.064s-86.179-17.07-117.552-48.064 C107.206,340.587,90,299.585,90,255.999s17.206-84.588,48.448-115.453C169.821,109.55,211.568,92.481,256,92.481 M256,52.481 c-113.771,0-206,91.117-206,203.518c0,112.398,92.229,203.52,206,203.52c113.772,0,206-91.121,206-203.52 C462,143.599,369.772,52.481,256,52.481L256,52.481z M240.258,346h-52.428V166h52.428V346z M326.17,346h-52.428V166h52.428V346z";
	private static final Group STATUSICON_RUNNING = createIcon(PATH_STATUSICON_RUNNING);
	private static final Group STATUSICON_PAUSED = createIcon(PATH_STATUSICON_PAUSED);

	static {
		STATUSICON_RUNNING.setAutoSizeChildren(true);
		STATUSICON_PAUSED.setAutoSizeChildren(true);
	}

	@FXML
	private HBox hbxBot;
	@FXML
	private EditorPane editorPane;
	@FXML
	private SplitPane spnBotPanes, spnBotLeft;
	@FXML
	private VBox vbxDebugHidden, vbxDebugpane;
	@FXML
	private ConsolePane consolePane;

	private XillProcessor processor;

	private final FXController globalController;
	private RobotID currentRobot;

	/**
	 * Create a new robottab that holds a currentRobot
	 *
	 * @param projectPath
	 * @param documentPath
	 *        The full path to the currentRobot (absolute)
	 * @param globalController
	 * @throws IOException
	 */
	public RobotTab(final File projectPath, final File documentPath, final FXController globalController) throws IOException {
		this.globalController = globalController;

		if (!documentPath.isAbsolute()) {
			throw new IllegalArgumentException("The provided document must be an absolute path.");
		}

		loadProcessor(documentPath, projectPath);
		currentRobot = getProcessor().getRobotID();

		// Load the FXML
		try {
			setContent(Loader.load(getClass().getResource("/fxml/RobotTabContent.fxml"), this));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Add close request event handler
		setOnCloseRequest(this::onClose);

		initializeSettings(documentPath);
		initializeTab(documentPath);
		
		// Add the context menu.
		addContextMenu(globalController);
	}

	private static void initializeSettings(final File documentPath) {
		settings.simple().register(Settings.LAYOUT, Settings.RightPanelWidth_ + documentPath.getAbsolutePath(), "0.7", "Width of the right panel for the specified currentRobot");
		settings.simple().register(Settings.LAYOUT, Settings.RightPanelCollapsed_ + documentPath.getAbsolutePath(), "true", "The collapsed-state of the right panel for the specified currentRobot");
		settings.simple().register(Settings.LAYOUT, Settings.EditorHeight_ + documentPath.getAbsolutePath(), "0.6", "The height of the editor");
	}

	private void initializeTab(final File documentPath) {
		// First we set the tab name
		setText(getName());

		// Set the tab dividers
		double editorHeight = Double.parseDouble(settings.simple().get(Settings.LAYOUT, Settings.EditorHeight_ + documentPath.getAbsolutePath()));

		spnBotLeft.setDividerPosition(0, editorHeight);

		// Save the position on change
		spnBotLeft.getDividers().get(0).positionProperty().addListener(
			(observable, oldPos, newPos) -> {
				double height = newPos.doubleValue();
				settings.simple().save(Settings.LAYOUT, Settings.EditorHeight_ + documentPath.getAbsolutePath(), Double.toString(height));
			});

		// Subscribe to start/stop for icon change
		processor.getDebugger().getOnRobotStart().addListener(e -> Platform.runLater(() -> setGraphic(STATUSICON_RUNNING)));
		processor.getDebugger().getOnRobotStop().addListener(e -> Platform.runLater(() -> setGraphic(null)));
		processor.getDebugger().getOnRobotPause().addListener(e -> Platform.runLater(() -> setGraphic(STATUSICON_PAUSED)));
		processor.getDebugger().getOnRobotContinue().addListener(e -> Platform.runLater(() -> setGraphic(STATUSICON_RUNNING)));
	}
	
	private void addContextMenu(FXController controller) {
		// Close this tab.
		MenuItem closeThis = new MenuItem("Close");
		closeThis.setOnAction(e -> controller.closeTab(this));
		
		// Close all other tabs.
		MenuItem closeOther = new MenuItem("Close all other tabs");
		closeOther.setOnAction(e -> controller.closeAllTabsExcept(this));
		
		// Save
		MenuItem saveTab = new MenuItem("Save");
		saveTab.setOnAction(e -> save());
		
		// Save
		MenuItem saveAs = new MenuItem("Save as...");
		saveAs.setOnAction(e -> save(true));
		
		// Create the context menu.
		ContextMenu menu = new ContextMenu(closeThis, closeOther, saveTab, saveAs);
		this.setContextMenu(menu);
	}

	private void loadProcessor(final File document, final File projectPath) {
		if (processor == null) {
			processor = Loader.getXill().createProcessor(document, projectPath, Loader.getInitializer().getLoader());
		} else {
			processor = Loader.getXill().createProcessor(document, projectPath, Loader.getInitializer().getLoader(), processor.getDebugger());
		}
	}

	@Override
	public void initialize(final URL arg0, final ResourceBundle arg1) {

		Platform.runLater(() -> {
			// FX graphical child items initialization (CTC-713)
			editorPane.initialize(this);
			consolePane.initialize(this);
			vbxDebugpane.getChildrenUnmodifiable().filtered(node -> (node instanceof DebugPane)).forEach(node -> ((DebugPane)node).initialize(this)); 

			// Remove the left hidden bar from dom
			// This must be done after initialization otherwise the debugpane won't receive the tab
			boolean showRightPanel = Boolean.parseBoolean(settings.simple().get(Settings.LAYOUT, Settings.RightPanelCollapsed_ + getDocument().getAbsolutePath()));

			if (showRightPanel) {
				hideButtonPressed();
			} else {
				showButtonPressed();
			}
		});

		setText(getName());

		// Load code
		File document = processor.getRobotID().getPath();
		if (document.exists()) {
			try {
				String code = FileUtils.readFileToString(document);
				editorPane.setLastSavedCode(code);
				editorPane.getEditor().setCode(code);
			} catch (IOException e) {
				log.info("Could not open " + document.getAbsolutePath());
			}
		}

		// Subscribe to events
		editorPane.getDocumentState().addListener(this);
	}

	/**
	 * Hide the debugpane
	 */
	@FXML
	private void hideButtonPressed() {
		File document = processor.getRobotID().getPath();
		if (document != null) {
			settings.simple().save(Settings.LAYOUT, Settings.RightPanelCollapsed_ + document.getAbsolutePath(), "true");
			if (!spnBotPanes.getDividers().isEmpty()) {
				settings.simple().save(Settings.LAYOUT, Settings.RightPanelWidth_ + document.getAbsolutePath(), Double.toString(spnBotPanes.getDividerPositions()[0]));
			}
		}

		// Hide debug
		spnBotPanes.getItems().remove(vbxDebugpane);

		// Show the small bar
		if (!hbxBot.getChildren().contains(vbxDebugHidden)) {
			hbxBot.getChildren().add(vbxDebugHidden);
		}
	}

	/**
	 * Show the debug pane
	 */
	@FXML
	private void showButtonPressed() {
		File document = processor.getRobotID().getPath();
		settings.simple().save(Settings.LAYOUT, Settings.RightPanelCollapsed_ + document.getAbsolutePath(), "false");

		// Hide small bar
		hbxBot.getChildren().remove(vbxDebugHidden);

		// Show debugpane
		if (!spnBotPanes.getItems().contains(vbxDebugpane)) {
			spnBotPanes.getItems().add(vbxDebugpane);
		}

		// Add splitpane position listener
		spnBotPanes.setDividerPosition(0, Double.parseDouble(settings.simple().get(Settings.LAYOUT, Settings.RightPanelWidth_ + document.getAbsolutePath())));
		spnBotPanes.getDividers().get(0).positionProperty().addListener((position, oldPos, newPos) -> {
			if (spnBotPanes.getItems().contains(vbxDebugpane)) {
				settings.simple().save(Settings.LAYOUT, Settings.RightPanelWidth_ + document.getAbsolutePath(), newPos.toString());
			}
		});
	}

	/**
	 * Transfers the focus to the editor pane.
	 */
	public void requestFocus() {
		globalController.showTab(this);
		editorPane.requestFocus();
	}

	/**
	 * Save this document.
	 *
	 * @return whether the document was saved successfully.
	 */
	public boolean save() {
		return save(editorPane.getDocumentState().getValue() == DocumentState.NEW);
	}

	/**
	 * Save this document.
	 *
	 * @param showDialog
	 *        whether a "Save as..." dialog should be shown
	 * @return whether the document was saved successfully.
	 */
	protected boolean save(final boolean showDialog) {
		// Reset to root robot
		resetCode();
		// Clear editor highlights
		getEditorPane().getEditor().clearHighlight();

		File document = getDocument();
		File projectPath = getProjectPath();

		if (showDialog) {
			// Show file picker
			FileChooser chooser = new FileChooser();
			chooser.setInitialDirectory(projectPath);
			chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Xill Robot (*." + Xill.FILE_EXTENSION + ")", "*." + Xill.FILE_EXTENSION));
			File selected = chooser.showSaveDialog(getContent().getScene().getWindow());

			if (selected == null) {
				return false;
			}

			document = selected;
		}

		// Actually save
		try {
			String code = editorPane.getEditor().getCodeProperty().get();
			FileUtils.write(document, code);
			editorPane.setLastSavedCode(code);
			log.info("Saved currentRobot to " + document.getAbsolutePath());

		} catch (IOException e) {
			new Alert(AlertType.ERROR, e.getMessage());
		}

		loadProcessor(document, projectPath);

		return true;
	}

	/**
	 * @return the project path
	 */
	private File getProjectPath() {
		return processor.getRobotID().getProjectPath();
	}

	/**
	 * @return the document
	 */
	public File getDocument() {
		return processor.getRobotID().getPath();
	}

	/**
	 * @return the name of the tab
	 */
	public String getName() {
		String filename = getDocument().getName();
		if (filename.endsWith("." + Xill.FILE_EXTENSION)) {
			filename = filename.substring(0, filename.length() - Xill.FILE_EXTENSION.length() - 1);
		}
		return filename;
	}

	private static Group createIcon(final String shape) {
		SVGPath path = new SVGPath();
		path.setFill(Color.DARKGRAY);
		path.setScaleX(0.04);
		path.setScaleY(0.04);
		path.setContent(shape);
		return new Group(path);
	}

	/**
	 * @return the globalController
	 */
	public FXController getGlobalController() {
		return globalController;
	}

	/**
	 * @param event
	 */
	private void onClose(final Event event) {
		// Check if the robot is saved, else show the save before closing dialog.
		if (editorPane.getDocumentState().getValue() == DocumentState.CHANGED) {
			SaveBeforeClosingDialog dlg = new SaveBeforeClosingDialog(this, event); 
			dlg.showAndWait();
			if (dlg.isCancelPressed()) {
				globalController.setCancelClose(true);
			}
		}
		
		// Check if the robot is running, else show the stop robot dialog.
		if (editorPane.getControls().robotRunning()) {
			CloseTabStopRobotDialog dlg = new CloseTabStopRobotDialog(this, event);
			dlg.showAndWait();
		}
	}

	/**
	 * Runs the currentRobot
	 *
	 * @throws XillParsingException
	 */
	public void runRobot() throws XillParsingException {
		save();

		try {
			processor.compile();

			Robot robot = processor.getRobot();

			Thread robotThread = new Thread(() -> {
				try {
					robot.process(processor.getDebugger());
				} catch (Exception e) {
					Platform.runLater(() -> {
						Alert error = new Alert(AlertType.ERROR);
						error.setTitle(e.getClass().getSimpleName());
						error.setContentText(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
						error.setHeaderText("Exception while processing");
						error.setResizable(true);
						error.getDialogPane().setPrefWidth(1080);
						error.show();
					});
				}
			});

			robotThread.start();
		} catch (IOException e) {
			e.printStackTrace();
			errorPopup(-1, e.getLocalizedMessage(), e.getClass().getSimpleName(), "Exception while compiling.");

		} catch (XillParsingException e) {
			errorPopup(e.getLine(), e.getLocalizedMessage(), e.getClass().getSimpleName(), "Exception while compiling " + e.getRobot().getPath().getAbsolutePath());
			throw e;

		}

	}

	private void errorPopup(final int line, final String message, final String title, final String context) {
		Alert error = new Alert(AlertType.ERROR);
		error.setTitle(title);
		error.setContentText(message);
		error.setHeaderText(context);
		error.show();

		getEditorPane().getEditor().highlightLine(line, "error");
	}

	/**
	 * <b>NOTE: </b> Do not save this processor over a long period as it will be swapped out often.
	 *
	 * @return the processor for this tab
	 */
	public XillProcessor getProcessor() {
		return processor;
	}

	@Override
	public void changed(final ObservableValue<? extends DocumentState> source, final DocumentState oldValue, final DocumentState newValue) {
		// This needs to happen in JFX Thread
		Platform.runLater(() -> {
			String name = getName();
			if (newValue == DocumentState.CHANGED) {
				name += "*";
			}
			if (currentRobot != getProcessor().getRobotID()) {
				String filename = currentRobot.getPath().getName();
				name += " > " + FilenameUtils.getBaseName(filename);
			}
			setText(name);
		});
	}

	/**
	 * @return the {@link EditorPane} in this tab
	 */
	public EditorPane getEditorPane() {
		return editorPane;
	}

	/**
	 * Show a different currentRobot in this tab and highlight the line
	 *
	 * @param robot
	 * @param line
	 */
	public void display(final RobotID robot, final int line) {

		// Update the code
		if (currentRobot != robot) {

			currentRobot = robot;
			String code;
			try {
				code = FileUtils.readFileToString(robot.getPath());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			// Load the code
			editorPane.getEditor().setCode(code);
			editorPane.getEditor().refreshBreakpoints(robot);

			// Blocker
			editorPane.getEditor().setEditable(currentRobot == getProcessor().getRobotID());

			// Remove the 'edited' state
			Platform.runLater(() -> {
				editorPane.getDocumentState().setValue(DocumentState.SAVED);
			});
		}

		if (line > 0) {
			// Highlight the line
			Platform.runLater(() -> {
				editorPane.getEditor().clearHighlight();
				editorPane.getEditor().highlightLine(line, "highlight");
			});
		}

	}

	/**
	 * Display the code from this tab's main currentRobot
	 */
	public void resetCode() {
		display(getProcessor().getRobotID(), -1);
	}

	/**
	 * @return The robot the is currently being displayed
	 */
	public RobotID getCurrentRobot() {
		return currentRobot;
	}

	/**
	 * Clear the console content related to this robot
	 */
	public void clearConsolePane() {
		this.consolePane.clear();
	}

}
