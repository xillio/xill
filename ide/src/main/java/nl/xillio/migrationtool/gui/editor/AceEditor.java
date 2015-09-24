package nl.xillio.migrationtool.gui.editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.migrationtool.BreakpointPool;
import nl.xillio.migrationtool.gui.FXController;
import nl.xillio.migrationtool.gui.HelpPane;
import nl.xillio.migrationtool.gui.RobotTab;
import nl.xillio.sharedlibrary.settings.SettingsHandler;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.preview.Replaceable;

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
	private XillProcessor processor;
	private RobotTab tab;

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
	 *   This is a workaround for a bug introduced in jdk1.8.0_60 where internal resources cannot reference other
	 *   internal resources. This method should be removed as soon as this bug is fixed.
	 * </p>
	 * @deprecated This is a workaround
	 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8134975?page=com.atlassian.streams.streams-jira-plugin:activity-stream-issue-tab">Bug Report</a>
	 */
	@Deprecated
	private static void deployEditor() throws IOException, TemplateException {
		File editorFile = File.createTempFile("xill_editor", ".html");
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

		load(EDITOR_URL);

		editor.addEventHandler(KeyEvent.KEY_PRESSED, this);
		editor.addEventHandler(ScrollEvent.SCROLL, this);

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
	 * Set the {@link RobotTab}
	 * 
	 * @param tab
	 */
	public void setTab(final RobotTab tab) {
		this.tab = tab;

	}

	private void onDocumentLoad() {
		bindToWindow();
		executeJS("editor.focus()");
	}

	/**
	 * Adds a keyword to the highlighter which should be highlighted as a built-in.
	 *
	 * @param keyword
	 *        the built-in keyword
	 */
	public void addBuiltIn(final String keyword) {
		executeJS("editor.addBuildin('" + keyword + "');");
	}

	/**
	 * Adds a keyword to the highlighter which should be highlighted as a normal keyword.
	 *
	 * @param keyword
	 *        the keyword to add
	 */
	public void addKeyword(final String keyword) {
		executeJS("editor.addKeyword('" + keyword + "');");
	}

	/**
	 * Pulls all built-ins and keywords from a processor
	 * 
	 * @param processor
	 */
	public void addKeywords(final XillProcessor processor) {
		this.processor = processor;

		// Read the zoom
		settings.registerSimpleSetting("Layout", "AceZoom_" + processor.getRobotID().getPath().getAbsolutePath(), "1.0", "The zoom factor of the code editor.");
		String zoomString = settings.getSimpleSetting("AceZoom_" + processor.getRobotID().getPath().getAbsolutePath());
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

		processor.listPackages().forEach(this::addKeyword);
		Arrays.stream(processor.getReservedKeywords()).forEach(this::addBuiltIn);
	}

	/**
	 * Clears all highlighted lines
	 */
	public void clearHighlight() {
		executeJS("editor.clearHighlight();");
	}

	/**
	 * Clears the history by loading a new UndoManager
	 */
	public void clearHistory() {
		executeJS("editor.getSession().setUndoManager(new UndoManager());");
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
	 * Clears all breakpoints.
	 */
	public void clearBreakpoints() {
		executeJS("editor.getSession().clearBreakpoints();");
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
				executeJS("editor.getCurrentWord();",
					result -> {
						// helppane.display((String)result);
					});
			}
		}

		// Scrolling
		if (event instanceof ScrollEvent) {
			ScrollEvent se = (ScrollEvent) event;
			if (se.isMetaDown() || se.isControlDown()) {
				zoomTo(editor.getZoom() * (1 + Math.signum(se.getDeltaY()) * ZOOM_SENSITIVITY / 1000.f));
			}
		}
	}

	private void zoomTo(final double value) {
		editor.setZoom(value);

		if (processor != null) {
			settings.saveSimpleSetting("AceZoom_" + processor.getRobotID().getPath().getAbsolutePath(), Double.toString(value));
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
		executeJS("editor.highlight(" + (line - 1) + ", \"" + type + "\");");
	}

	/**
	 * Pastes the current clipboard at the caret.
	 */
	public void paste() {
		String code = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
		if (code != null) {
			executeJS("var r = editor.session.replace(editor.selection.getRange(), \"" + escape(code) + "\");"
							+ " editor.selection.setSelectionRange(new Range(r.row, r.column, r.row, r.column));");
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
		executeJS("editor.redo();");
	}

	/**
	 * Clears the selected text.
	 */
	public void clearSelection() {
		executeJS("editor.clearSelection();");
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
			executeJS("editor.setValue('" + escape(code) + "',1);");
			this.code.setValue(code);
			clearHistory();

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
		executeJS("editor.session.setBreakpointsAtRows([" +
						StringUtils.join(bps, ",") + "]);");
	}

	/**
	 * Sets whether to show invisible characters (line breaks, spaces, etc...).
	 *
	 * @param show
	 *        whether to display invisible characters
	 */
	public void setShowInvisibles(final boolean show) {
		executeJS("editor.setShowInvisibles(" + show + ");");
	}

	/**
	 * Steps back in edit history.
	 */
	public void undo() {
		executeJS("editor.undo();");
	}

	private void bindToWindow() {
		executeJS("window", result -> {
			JSObject jsobj = (JSObject) result;
			jsobj.setMember("contenttools", this);
			jsobj.setMember("LOGGER", LOGGER);
		});
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
			if (!documentLoaded.get()) {
				throw new IllegalStateException("Cannot run javascript because the editor has not been loaded yet: " + js);
			}
			callback.accept(editor.getEngine().executeScript(js));
		});
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
	private int currenthighlight = 0;
	private int occurrences = 0;

	@Override
	public void searchPattern(final String pattern, final boolean caseSensitive) {
		currenthighlight = 0;
		searchJS(pattern, true, caseSensitive);
	}

	@Override
	public void search(final String needle, final boolean caseSensitive) {
		currenthighlight = 0;
		searchJS(needle, false, caseSensitive);
	}

	@Override
	public int getOccurrences() {
		return occurrences;
	}

	@Override
	public void highlight(final int occurrence) {
		while (currenthighlight != occurrence) {
			if (currenthighlight < occurrence) {
				highlightNext();
			} else {
				highlightPrevious();
			}
		}
	}

	@Override
	public void highlightAll() {
		// Is already done automatically (in javascript) when searchJS is called
		highlight(0);
	}

	@Override
	public void replaceAll(final String replacement) {
		executeJS("editor.replaceAll('" + escape(replacement) + "');");
	}

	@Override
	public void replaceOne(final int occurrence, final String replacement) {
		highlight(occurrence);
		executeJS("editor.replace('" + escape(replacement) + "');");
	}

	private void searchJS(final String needle, final boolean regex, final boolean caseSensitive) {
		String arguments = "'" + escape(needle) + "',"
						+ "{ "
						+ "backwards: true,"
						+ "wrap: true,"
						+ "caseSensitive: " + caseSensitive + ","
						+ "regExp: " + regex + ","
						+ "start: 1"
						+ "},"
						+ "false";

		// Count
		executeJS("editor.findAll(" + arguments + ");",
			result -> {
				occurrences = (Integer) result;

				// If there are no results, clear the search
				if (occurrences == 0) {
					executeJS("editor.clearSearch();");
				}
			});
	}

	private void highlightNext() {
		executeJS("editor.findNext();");
		currenthighlight++;
	}

	private void highlightPrevious() {
		executeJS("editor.findPrevious();");
		currenthighlight--;
	}

	@Override
	public void clearSearch() {
		executeJS("editor.clearSearch();");
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
		executeJS("editor.setReadOnly(" + Boolean.toString(!editable) + ");");
	}
}
