package nl.xillio.migrationtool.gui.editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.Cursor;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import me.biesaart.utils.FileUtils;
import netscape.javascript.JSObject;
import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.migrationtool.BreakpointPool;
import nl.xillio.migrationtool.gui.FXController;
import nl.xillio.migrationtool.gui.HelpPane;
import nl.xillio.migrationtool.gui.RobotTab;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.preview.Replaceable;
import nl.xillio.xill.util.HighlightSettings;
import nl.xillio.xill.util.settings.Settings;
import nl.xillio.xill.util.settings.SettingsHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This class wraps around a webview object containing an ace editor in Xill mode.
 * It *should* extend {@link WebView}, but this class is final...
 */
public class AceEditor implements EventHandler<javafx.event.Event>, Replaceable {
	private static String EDITOR_URL;
	private static final SettingsHandler settings = SettingsHandler.getSettingsHandler();
	private static final double ZOOM_SENSITIVITY = 100;
	private static final Clipboard clipboard = Clipboard.getSystemClipboard();
	private static final Logger LOGGER = LogManager.getLogger();
	private final StringProperty code = new SimpleStringProperty();
	private final WebView editor;
	private final SimpleBooleanProperty documentLoaded = new SimpleBooleanProperty(false);
	private final EventHost<Boolean> onDocumentLoaded = new EventHost<>();
	private HelpPane helppane;
	private JSObject ace;
	private XillProcessor processor;
	private RobotTab tab;
	private ContextMenu rightClickMenu;
	private HighlightSettings highlightSettings;

	static {
		try {
			deployEditor();
		} catch (IOException | TemplateException e) {
			throw new RuntimeException("Failed to deploy editor", e);
		}
	}

	/**
	 * Deploy the editor file.
	 * <p>
	 * This is a workaround for a bug introduced in jdk1.8.0_60 where internal resources cannot reference other internal resources. This method should be removed as soon as this bug is fixed.
	 * </p>
	 * 
	 * @deprecated This is a workaround
	 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8134975?page=com.atlassian.streams.streams-jira-plugin:activity-stream-issue-tab">Bug Report</a>
	 */
	@Deprecated
	private static void deployEditor() throws IOException, TemplateException {
		File editorFile = File.createTempFile("xill_editor", ".html");
		FileUtils.forceDeleteOnExit(editorFile);
		Configuration config = new Configuration(Configuration.VERSION_2_3_23);
		config.setClassForTemplateLoading(AceEditor.class, "/");
		Template template = config.getTemplate("editor.html");
		Map<String, Object> model = new HashMap<>();
		model.put("jarFile", AceEditor.class.getResource("/editor.html").toExternalForm().replaceAll("editor\\.html", ""));
		template.process(model, new FileWriter(editorFile));
		LOGGER.info("Deployed editor as JavaFX workaround");
		EDITOR_URL = editorFile.toURI().toURL().toExternalForm();
	}

	/**
	 * Default constructor. Takes a {@link WebView} since we can't extend it.
	 *
	 * @param editor
	 *        the {@link WebView} to wrap in
	 */
	public AceEditor(final WebView editor) {
		this.editor = editor;
		this.highlightSettings = new HighlightSettings();
		// Add our own context menu.
		editor.setContextMenuEnabled(false);
		createContextMenu();


		// Add event handlers.
		editor.addEventHandler(KeyEvent.KEY_PRESSED, this);
		editor.addEventHandler(ScrollEvent.SCROLL, this);
		editor.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, this);
		editor.addEventHandler(MouseEvent.MOUSE_PRESSED, this);

		documentLoaded.addListener(
			(obs, oldDoc, newDoc) -> {
				if (newDoc != null) {
					onDocumentLoad();
					onDocumentLoaded.invoke(newDoc);
				}
			});
		
		// Disable drag-and-drop, set the cursor graphic when dragging.
		editor.setOnDragDropped(null);
		editor.setOnDragOver(e -> editor.sceneProperty().get().setCursor(Cursor.DISAPPEAR));
			
	}
	
	/**
	 * Build the right-click context menu. 
	 */
	private void createContextMenu() {
		// Cut menu item.
		MenuItem cut = new MenuItem("Cut");
		cut.setOnAction(e -> callOnAce("onCut"));
		
		// Copy menu item.
		MenuItem copy = new MenuItem("Copy");
		copy.setOnAction(e -> copyToClipboard((String)callOnAceBlocking("getCopyText")));
		
		// Paste menu item.
		MenuItem paste = new MenuItem("Paste");
		paste.setOnAction(e -> paste());
		
		// Create the menu with all items.
		rightClickMenu = new ContextMenu(cut, copy, paste);
	}
	
	/**
	 * Set the {@link RobotTab}
	 * 
	 * @param tab
	 */
	public void setTab(final RobotTab tab) {
		this.tab = tab;

	}

	private void onDocumentLoad() {
		// Set members
		bindToWindow();
		// Actually load Ace editor
		executeJSBlocking("loadEditor();");
		// Set code, if it was set before editor loads
		String codeString = code.get();
		if (codeString != null) {
			setCode(codeString);
		}
		// get focus
		callOnAce("focus");
	}

	/**
	 * Pulls all built-ins and keywords from a processor
	 * 
	 * @param processor
	 */
	public void addKeywords(final XillProcessor processor) {
		this.processor = processor;

		// Read the zoom
		settings.simple().register(Settings.LAYOUT, Settings.AceZoom_ + processor.getRobotID().getPath().getAbsolutePath(), "1.0", "The zoom factor of the code editor.");
		String zoomString = settings.simple().get(Settings.LAYOUT, Settings.AceZoom_ + processor.getRobotID().getPath().getAbsolutePath());
		if (zoomString != null) {
			double zoom = Double.parseDouble(zoomString);
			editor.setZoom(zoom);
		}

		// If the document has not been loaded yet load the robot later
		if (!documentLoaded.get()) {
			documentLoaded.addListener(
				(obs, oldVal, newVal) -> {
					if (newVal != null) {
						addKeywords(processor);
					}
				});

			return;
		}

		highlightSettings.addKeywords(processor.listPackages());
		highlightSettings.addBuiltins(processor.getReservedKeywords());
	}

	/**
	 * Clears all highlighted lines
	 */
	public void clearHighlight() {
		callOnAce("clearHighlight");
	}

	/**
	 * Clears the history by loading a new UndoManager
	 */
	public void clearHistory() {
		executeJS("new UndoManager();", (u) ->
			((JSObject) callOnAceBlocking("getSession"))
				.call("setUndoManager", u));
	}

	/**
	 * This method is called from javascript whenever cut or copy is performed, to copy the selected text to the clipboard.
	 * 
	 * @param text
	 *        The text to copy to the clipboard.
	 */
	public void copyToClipboard(final String text) {
		final ClipboardContent content = new ClipboardContent();
		content.putString(text);
		clipboard.setContent(content);
	}

	/**
	 * @return the helppane
	 */
	public HelpPane getHelppane() {
		return helppane;
	}

	/**
	 * @param helppane
	 *        the helppane to set
	 */
	public void setHelppane(final HelpPane helppane) {
		this.helppane = helppane;
	}

	/**
	 * 
	 * @return The ace editor Javascript object
	 */
	public JSObject getAce() {
		return ace;
	}

	/**
	 * @param ace
	 *        The ace editor Javascript object
	 */
	public void setAce(JSObject ace) {
		this.ace = ace;
	}

	/**
	 * Clears all breakpoints.
	 */
	public void clearBreakpoints() {
		callOnAce((session) -> ((JSObject) session).call("clearBreakpoints"), "getSession");
	}

	/**
	 * Returns the current code property
	 *
	 * @return code property
	 */
	public StringProperty getCodeProperty() {
		return code;
	}

	/**
	 * Handles input events
	 */
	@Override
	public void handle(final javafx.event.Event event) {
		// Hotkeys
		if (event instanceof KeyEvent) {
			KeyEvent ke = (KeyEvent) event;

			if (KeyCombination.valueOf(FXController.HOTKEY_PASTE).match(ke)) {
				paste();
			} else if (KeyCombination.valueOf(FXController.HOTKEY_RESET_ZOOM).match(ke)) {
				zoomTo(1);
			} else if (helppane != null && KeyCombination.valueOf(FXController.HOTKEY_HELP).match(ke)) {
				callOnAce(
					result -> {
						// helppane.display((String)result);
				}, "getCurrentWord");
			}
		}

		// Scrolling
		if (event instanceof ScrollEvent) {
			ScrollEvent se = (ScrollEvent) event;
			if (se.isMetaDown() || se.isControlDown()) {
				zoomTo(editor.getZoom() * (1 + Math.signum(se.getDeltaY()) * ZOOM_SENSITIVITY / 1000.f));
			}
		}
		
		// Context menu
		if (event instanceof ContextMenuEvent) {
			ContextMenuEvent ce = (ContextMenuEvent) event;
			rightClickMenu.show(editor, ce.getScreenX(), ce.getScreenY());
			event.consume();
		}
		
		// Mouse click, close context menu.
		if (event instanceof MouseEvent) {
			rightClickMenu.hide();
		}
	}

	private void zoomTo(final double value) {
		editor.setZoom(value);

		if (processor != null) {
			settings.simple().save(Settings.LAYOUT, Settings.AceZoom_ + processor.getRobotID().getPath().getAbsolutePath(), Double.toString(value));
		}
	}

	/**
	 * Highlights a line.
	 *
	 * @param line
	 *        the line to highlight
	 * @param type
	 *        type of highlighting to be used( "error" or "highlight" )
	 */
	public void highlightLine(final int line, final String type) {
		callOnAce("highlight", line - 1, type);
	}

	/**
	 * Pastes the current clipboard at the caret.
	 */
	public void paste() {
		String code = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
		if (code != null) {
			Platform.runLater(() -> {
				JSObject session = (JSObject) callOnAceBlocking("getSession");
				JSObject selection = (JSObject) callOnAceBlocking("getSelection");
				JSObject r = (JSObject) session.call("replace", selection.call("getRange"), code);
				Object row = r.getMember("row");
				Object column = r.getMember("column");
				JSObject range = (JSObject) executeJSBlocking(String.format("new Range(%d, %d, %d, %d)", row, column, row, column));
				selection.call("setSelectionRange", range);
			});
		}

		/*
		 * The editor.setSelectionRange makes sure the caret gets move to the end of the pasted text.
		 * Otherwise when pasting text over equal text (e.g.: copy-paste-paste) it would stay selected
		 * and thus keep pasting over it.
		 */
	}

	/**
	 * Steps forward in the edit history.
	 */
	public void redo() {
		callOnAce("redo");
	}

	/**
	 * Clears the selected text.
	 */
	public void clearSelection() {
		callOnAce("clearSelection");
	}

	/**
	 * Wrapper around {@link WebView#requestFocus()}.
	 *
	 * @see WebView#requestFocus()
	 */
	public void requestFocus() {
		Platform.runLater(() -> {
			editor.requestFocus();
		});

	}

	/**
	 * Sets the code.
	 *
	 * @param code
	 *        the code to set
	 */
	public void setCode(final String code) {
		if (documentLoaded.get()) {
			if (ace != null) {
				callOnAce("setValue", code, 1);
				clearHistory();
			}
			this.code.setValue(code);
		} else {

			// Run this later
			documentLoaded.addListener(
				(obs, oldDoc, newDoc) -> {
					if (newDoc != null) {
						setCode(code);
					}
				});
		}
	}

	/**
	 * Load breakpoints from a breakpoint pool for a particular robot
	 * 
	 * @param robot
	 */
	public void refreshBreakpoints(final RobotID robot) {
		List<Integer> bps = BreakpointPool.INSTANCE.get(robot).stream().map(bp -> bp - 1).collect(Collectors.toList());
		callOnAce((s) -> ((JSObject) s).call("setBreakpointAtRows", bps), "getSession");
	}

	/**
	 * Sets whether to show invisible characters (line breaks, spaces, etc...).
	 *
	 * @param show
	 *        whether to display invisible characters
	 */
	public void setShowInvisibles(final boolean show) {
		callOnAce("setShowInvisibles", show);
	}

	/**
	 * Steps back in edit history.
	 */
	public void undo() {
		callOnAce("undo");
	}

	private void bindToWindow() {
		// Do not use executeJS here, it needs to be done immediately
		JSObject jsobj = (JSObject) executeJSBlocking("window");
		jsobj.setMember("highlightSettings", highlightSettings);
		jsobj.setMember("contenttools", this);
		jsobj.setMember("LOGGER", LOGGER);
	}

	/**
	 * This method is called by the javascript editor whenever the code has been changed
	 *
	 * @param newCode
	 */
	public void codeChanged(final String newCode) {
		code.setValue(newCode);
	}

	/**
	 * This method is called by the javascript editor whenever the list of breakpoints has changed
	 *
	 * @param jsObject
	 */
	public void breakpointsChanged(final JSObject jsObject) {
		int length = ((Number) jsObject.getMember("length")).intValue();

		List<Integer> breakpoints = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			if (Boolean.parseBoolean(jsObject.getSlot(i).toString())) {
				breakpoints.add(i + 1);
			}
		}

		BreakpointPool.INSTANCE.clear(tab.getCurrentRobot());

		breakpoints.forEach(bp -> {
			BreakpointPool.INSTANCE.add(tab.getCurrentRobot(), bp);
		});
	}

	/**
	 * Fail-safe way to execute a javascript code on a document that may not have been fully loaded yet.
	 * Wrapper around {@link WebEngine#executeScript(String)}, that returns null in case of an error not caused by the document not being loaded.
	 *
	 * Defers execution of code using {@link Platform#runLater(Runnable)}.
	 * 
	 * @param js
	 *        the script to execute.
	 *
	 * @see WebEngine#executeScript(String)
	 */
	private void executeJS(final String js) {
		executeJS(js, r -> {});
	}

	private void executeJS(final String js, final Consumer<Object> callback) {
		Platform.runLater(() -> {
			callback.accept(executeJSBlocking(js));
		});
	}

	/**
	 * Fail-safe way to execute a javascript code on a document that may not have been fully loaded yet.
	 * Wrapper around {@link WebEngine#executeScript(String)}, that returns null in case of an error not caused by the document not being loaded.
	 *
	 * Runs code immediately.
	 *
	 * @param js
	 *        the script to execute.
	 *
	 * @see WebEngine#executeScript(String)
	 */
	private Object executeJSBlocking(final String js) {
		if (!documentLoaded.get()) {
			throw new IllegalStateException("Cannot run javascript because the editor has not been loaded yet: " + js);
		}
		return editor.getEngine().executeScript(js);
	}

	/**
	 * Call a method on the javascript ace editor object
	 * 
	 * Defers execution using {@link Platform#runLater(Runnable)}.
	 * 
	 * @param callback
	 *        Callback that is called with the return value of the call
	 * @param method
	 *        Method name
	 * @param args
	 *        Arguments to pass to the method
	 * @see JSObject#call(String, Object...)
	 */
	private void callOnAce(String method, Object... args) {
		callOnAce((o) -> {}, method, args);
	}

	/**
	 * Call a method on the javascript ace editor object
	 * 
	 * Defers execution using {@link Platform#runLater(Runnable)}.
	 * 
	 * @param callback
	 *        Callback that is called with the return value of the call
	 * @param method
	 *        Method name
	 * @param args
	 *        Arguments to pass to the method
	 * @see JSObject#call(String, Object...)
	 */
	private void callOnAce(final Consumer<Object> callback, String method, Object... args) {
		Platform.runLater(() -> callback.accept(ace.call(method, args)));
	}

	/**
	 * Call a method on the javascript ace editor object
	 *
	 * @param method
	 *        Method name
	 * @param args
	 *        Arguments to pass to the method
	 * @see JSObject#call(String, Object...)
	 */
	private Object callOnAceBlocking(String method, Object... args) {
		return ace.call(method, args);
	}

	/**
	 * Load Ace editor in the {@link WebView}
	 */
	public void loadEditor() {
		load(EDITOR_URL);
	}

	private void load(final String path) {
		editor.getEngine().load(path);
		editor.getEngine().documentProperty().addListener(
			(obs, oldDoc, newDoc) -> {
				documentLoaded.setValue(true);
			});
	}

	private static String escape(final String raw) {
		return raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'").replace("\n", "\\n").replace("\r", "");
	}

	// ///////// SEARCH BAR //////////////////
	private int occurrences = 0;

	private String needle;
	private boolean regex;
	private boolean caseSensitive;

	@Override
	public void searchPattern(final String pattern, final boolean caseSensitive) {
		this.needle = pattern;
		this.caseSensitive = caseSensitive;
		this.regex = true;
		searchJS(pattern, true, caseSensitive, 0);
	}

	@Override
	public void search(final String needle, final boolean caseSensitive) {
		this.needle = needle;
		this.caseSensitive = caseSensitive;
		this.regex = false;
		searchJS(needle, false, caseSensitive, 0);
	}

	@Override
	public int getOccurrences() {
		return occurrences;
	}

	@Override
	public void highlight(final int occurrence) {
		this.searchJS(needle, regex, caseSensitive, occurrence);
	}

	@Override
	public void highlightAll() {
		// nothing to do
	}

	@Override
	public void replaceAll(final String replacement) {
		callOnAce("replaceAll", replacement);
	}

	@Override
	public void replaceOne(final int occurrence, final String replacement) {
		highlight(occurrence);
		callOnAce("replace", replacement);
	}

	private void searchJS(final String needle, final boolean regex, final boolean caseSensitive, final int occurence) {
		JSObject flags = (JSObject) executeJSBlocking("var flags={ "
				+ "backwards: false,"
				+ "wrap: true,"
				+ "caseSensitive: " + caseSensitive + ","
				+ "regExp: " + regex + ","
				+ "occurence: " + occurence
				+ "}; flags;");

		// Count
		callOnAce(result -> {
			occurrences = (Integer) result;

			// If there are no results, clear the search
			if (occurrences == 0) {
				callOnAceBlocking("clearOccurences");
			}
		}, "findOccurences", needle, flags);
	}

	@Override
	public void clearSearch() {
		callOnAce("clearOccurences");
	}

	/**
	 * @return the onDocumentLoaded
	 */
	public Event<Boolean> getOnDocumentLoaded() {
		return onDocumentLoaded.getEvent();
	}

	/**
	 * @param editable
	 */
	public void setEditable(final boolean editable) {
		callOnAce("setReadOnly", !editable);
	}
}
