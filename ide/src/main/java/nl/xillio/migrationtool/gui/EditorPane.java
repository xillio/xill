package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import nl.xillio.migrationtool.BreakpointPool;
import nl.xillio.migrationtool.Loader;
import nl.xillio.migrationtool.elasticconsole.ESConsoleClient;
import nl.xillio.migrationtool.elasticconsole.RobotLogMessage;
import nl.xillio.migrationtool.gui.editor.AceEditor;
import nl.xillio.xill.util.HotkeysHandler.Hotkeys;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The editor pane. Contains most of the UI, apart from the left panel.
 */
public class EditorPane extends AnchorPane implements EventHandler<KeyEvent>, RobotTabComponent, ChangeListener<String> {

	/**
	 * The different states a robot can have.
	 */
	public enum DocumentState {
		/**
		 * State of a robot that is created but not saved yet.
		 */
		NEW, /**
					 * State of a saved robot that has been modified and not saved yet.
					 */
		CHANGED, /**
							 * State of a robot that is saved and not modified.
							 */
		SAVED
	}

	private static final Logger LOGGER = LogManager.getLogger(EditorPane.class);

	@FXML
	private Button btnUndo;
	@FXML
	private Button btnRedo;
	@FXML
	private Button btnBrowser;
	@FXML
	private Button btnRegexTester;
	@FXML
	private Button btnPreviewOpenBrowser;
	@FXML
	private Button btnPreviewOpenRegex;

	@FXML
	private CheckMenuItem cmiDebug;
	@FXML
	private CheckMenuItem cmiInfo;
	@FXML
	private CheckMenuItem cmiWarning;
	@FXML
	private CheckMenuItem cmiError;

	@FXML
	private Button btnRemoveAllBreakpoints;
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
	private ReplaceBar editorReplaceBar;

	private final AceEditor editor;

	private final Property<DocumentState> documentState = new SimpleObjectProperty<>(DocumentState.NEW);

	private RobotControls controls;

	private RobotTab tab;
	
	/**
	 * Contains the editor's content (Xill source code) that was saved to disc (or whatever medium) last time.
	 */
	private String lastSavedCode = "";

    /**
     * Contains the code content that was detected last time as changed outside the editor (the file with robot was changed outside of editor - this contains last content of that file)
     * It's needed to store this because after one real file content change, the system can fire multiple events for that and this is a way how not to display multiple query dialogs for one file change..
     */
	private String lastChangedCode = "";

	/**
	 * Default constructor. Just sets up the UI and the listener.
	 */
	public EditorPane() {
		super();
		try {
			getChildren().add(Loader.load(getClass().getResource("/fxml/EditorPane.fxml"), this));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		editor = new AceEditor((WebView) lookup("#webCode"));

		ToolBar tlbSearchToolBar = (ToolBar) lookup("#tlbSearchToolBar");
		ToggleButton tbnEditorSearch = (ToggleButton) tlbSearchToolBar.getItems().stream().filter(child -> "tbnEditorSearch".equals(child.getId())).findAny().get();

		editorReplaceBar.setSearchable(editor);
		editorReplaceBar.setButton(tbnEditorSearch, 1);
		editor.getCodeProperty().addListener(this);
        editor.setReplaceBar(editorReplaceBar);

		addEventHandler(KeyEvent.KEY_PRESSED, this);
	}

	@Override
	public void initialize(final RobotTab tab) {

		this.tab = tab;
		controls = new RobotControls(tab, btnRun, btnPause, btnStop, btnStepIn, btnStepOver, cmiError);
		editor.setTab(tab);
		editorReplaceBar.getOnClose().addListener(clear -> {
			if (clear){
				this.requestFocus();
			}
		});

		editor.loadEditor();
		editor.setOptions(tab.getGlobalController().createEditorOptionsJSCode());
		ESConsoleClient.getLogEvent(tab.getProcessor().getRobotID()).addListener(this::onLogMessage);
	}

	@FXML
	private void buttonUndo() {
		if (!btnUndo.isDisabled()) {
			editor.undo();
		}
	}

	@FXML
	private void buttonRedo() {
		if (!btnRedo.isDisabled()) {
			editor.redo();
		}
	}

	private void onLogMessage(final RobotLogMessage message) {
		switch (message.getLevel()) {
			case "debug":
				if (cmiDebug.isSelected()) {
					controls.pause();
				}
				break;
			case "info":
				if (cmiInfo.isSelected()) {
					controls.pause();
				}
				break;
			case "warn":
				if (cmiWarning.isSelected()) {
					controls.pause();
				}
				break;
			case "error":
				if (cmiError.isSelected()) {
					controls.pause();
				}
				break;
			default:
				LOGGER.debug("Unimplemented loglevel: " + message.getLevel());
				break;
		}
	}

	/**
	 * Overrides requestFocus by transferring the focus to the webCode.
	 *
	 * @see javafx.scene.Node#requestFocus()
	 */
	@Override
	public void requestFocus() {
		editor.requestFocus();
	}

	@Override
	@FXML
	public void handle(final KeyEvent event) {
		// Find
		if (KeyCombination.valueOf(FXController.hotkeys.getShortcut(Hotkeys.FIND)).match(event)) {
			event.consume();
			if (editorReplaceBar.isOpen()) {
				editorReplaceBar.close(false);
			} else {
				editorReplaceBar.open(1);
			}
		}
	}

	/**
	 * Perform Ace editor settings
	 * 
	 * @param jsCode JavaScript code with settings to be executed
	 */
	public void setEditorOptions(final String jsCode) {
		Platform.runLater(() -> editor.setOptions(jsCode));
	}

	/**
	 * Returns the editor.
	 *
	 * @return the editor
	 */
	public AceEditor getEditor() {
		return editor;
	}

	/**
	 * Returns the state of the document.
	 *
	 * @return the state of the document
	 */
	public Property<DocumentState> getDocumentState() {
		return documentState;
	}

	@FXML
	private void buttonRemoveAllBreakpoints() {
		BreakpointPool.INSTANCE.clear();
		tab.getGlobalController().getTabs().forEach(editorTab -> editorTab.getEditorPane().getEditor().clearBreakpoints());
	}

	@Override
	public void changed(final ObservableValue<? extends String> source, final String oldValue, final String newValue) {
		if (oldValue == null) {
			return;
		}

		this.updateDocumentState(newValue);
	}
	
	/**
	 * Checks if the @newCode means that document is changed or not.
	 * It compares the @newCode with the last saved editor's content.
	 *  
	 * @param newCode the Xill source code
	 */
	private void updateDocumentState(final String newCode) {
		if (newCode.equals(this.lastSavedCode)) {
			this.getDocumentState().setValue(DocumentState.SAVED);
		} else {
			this.getDocumentState().setValue(DocumentState.CHANGED);
		}
	}
	
	/**
	 * It saves the last saved editor's content - for the later usage when determining the state of the document
	 *  
	 * @param newCode the last saved editor's content
	 */
	public void setLastSavedCode(final String newCode) {
		this.lastSavedCode = newCode;
		this.getDocumentState().setValue(DocumentState.SAVED);
	}

    /**
     * Check if the last code in robot file that was changed outside the editor is different than both current code in editor and the last outside change
     *
     * @param newChangedCode the source code that is currently saved in the robot file
     * @return true if newChangedCode is very new (display gui dialog), false if the new content is not different from editor's content and not different from last changed content
     */
    public boolean checkChangedCode(final String newChangedCode) {
        if (this.getEditor().getCodeProperty().get().equals(newChangedCode)) {
            return false; // The new changed source code is the same as existing
        }

        // The new changed source code is different from existing in editor
        if (this.lastChangedCode.equals(newChangedCode)) {
            // But the new changed source code is the same as the last changed source code - so no change here
            return false;
        } else {
            this.lastChangedCode = newChangedCode;
            return true;
        }
    }

	/**
	 * @return the robot controls which allows to control the active robot
	 */
	public RobotControls getControls() {
		return controls;
	}
}
