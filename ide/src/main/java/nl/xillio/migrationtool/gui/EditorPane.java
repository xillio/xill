package nl.xillio.migrationtool.gui;

import java.io.IOException;

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
import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.migrationtool.ElasticConsole.RobotLogMessage;
import nl.xillio.migrationtool.gui.editor.AceEditor;

import org.apache.log4j.Logger;

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
		NEW,
		/**
		 * State of a saved robot that has been modified and not saved yet.
		 */
		CHANGED,
		/**
		 * State of a robot that is saved and not modified.
		 */
		SAVED
	}

	private static final Logger log = Logger.getLogger("XMT");

	@FXML
	private Button btnUndo, btnRedo, btnBrowser, btnRegexTester, btnPreviewOpenBrowser, btnPreviewOpenRegex;

	@FXML
	private CheckMenuItem cmiDebug, cmiInfo, cmiWarning, cmiError;

	@FXML
	private Button btnRemoveAllBreakpoints, btnRun, btnStepOver, btnStepIn, btnPause, btnStop;

	@FXML
	private ReplaceBar editorReplaceBar;

	private final AceEditor editor;

	private final Property<DocumentState> documentState = new SimpleObjectProperty<>(DocumentState.NEW);

	private RobotControls controls;

	private RobotTab tab;

	/**
	 * Default constructor. Just sets up the UI and the listener.
	 */
	public EditorPane() {
		super();
		try {
			getChildren().add(Loader.load(getClass().getResource("/fxml/EditorPane.fxml"), this));
		} catch (IOException e) {
			e.printStackTrace();
		}

		editor = new AceEditor((WebView) lookup("#webCode"));

		ToolBar tlbSearchToolBar = (ToolBar) lookup("#tlbSearchToolBar");
		ToggleButton tbnEditorSearch = (ToggleButton) tlbSearchToolBar.getItems().stream().filter(child -> child.getId().equals("tbnEditorSearch")).findAny().get();

		editorReplaceBar.setSearchable(editor);
		editorReplaceBar.setButton(tbnEditorSearch, 1);
		editor.getCodeProperty().addListener(this);

		addEventHandler(KeyEvent.KEY_PRESSED, this);
	}

	@Override
	public void initialize(final RobotTab tab) {

		this.tab = tab;
		controls = new RobotControls(tab, btnRun, btnPause, btnStop, btnStepIn, btnStepOver, cmiError);
		editor.setTab(tab);
		editor.addKeywords(tab.getProcessor());

		ESConsoleClient.getLogEvent(tab.getProcessor().getRobotID()).addListener(this::onLogMessage);
	}

	@FXML
	private void buttonUndo() {
		if (!btnUndo.isDisabled()) {
			editor.undo();
			getDocumentState().setValue(DocumentState.CHANGED);
		}
	}

	@FXML
	private void buttonRedo() {
		if (!btnRedo.isDisabled()) {
			editor.redo();
			getDocumentState().setValue(DocumentState.CHANGED);
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
				log.debug("Unimplemented loglevel: " + message.getLevel());
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
		if (KeyCombination.valueOf(FXController.HOTKEY_FIND).match(event)) {
			editorReplaceBar.open(1);
		}
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
		tab.getGlobalController().getTabs().forEach(editorTab -> {
			editorTab.getEditorPane().getEditor().clearBreakpoints();
		});
	}

	@Override
	public void changed(final ObservableValue<? extends String> source, final String oldValue, final String newValue) {
		if (oldValue == null) {
			return;
		}

		if (!oldValue.equals(newValue)) {
			getDocumentState().setValue(DocumentState.CHANGED);
		}
	}
}
