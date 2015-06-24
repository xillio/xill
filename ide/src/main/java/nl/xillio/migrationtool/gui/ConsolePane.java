package nl.xillio.migrationtool.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.xillio.migrationtool.ElasticConsole.Counter;
import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient.LogType;
import nl.xillio.migrationtool.ElasticConsole.LogEntry;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.preview.Searchable;

import com.sun.javafx.scene.control.behavior.CellBehaviorBase;

/**
 * This pane displays the console log stored in elasticsearch
 */
public class ConsolePane extends AnchorPane implements Searchable, EventHandler<KeyEvent>,RobotTabComponent {
	@FXML
	private SearchBar apnConsoleSearchBar;
	@FXML
	private TableView<LogEntry> tblConsoleOut;
	@FXML
	private TableColumn<LogEntry, String> colLogTime, colLogType, colLogMessage;
	@FXML
	private ToggleButton tbnToggleLogsInfo, tbnToggleLogsDebug, tbnToggleLogsWarn, tbnToggleLogsError, tbnConsoleSearch;

	//private Robot robot;

	// Log entry lists. Master contains everything, filtered contains only selected entries.
	private final ObservableList<LogEntry> masterLog = FXCollections.observableArrayList();
	private final FilteredList<LogEntry> filteredLog = new FilteredList<>(masterLog, e -> true);

	// Updating the console
	private final Timeline updateTimeline;
	private final int maxEntries = 1000;

	// Time format
	private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	// Filters
	private final Map<LogType, Boolean> filters = new HashMap<>(LogType.values().length);
	private final Map<LogType, Integer> count = new Counter<>();

	private final List<Integer> occurences = new ArrayList<>();
	private RobotTab tab;

	/**
	 * Create an initialize a ConsolePane
	 */
	public ConsolePane() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConsolePane.fxml"));
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(this);
			Node ui = loader.load();
			getChildren().add(ui);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Search bar
		apnConsoleSearchBar.setSearchable(this);
		apnConsoleSearchBar.setButton(tbnConsoleSearch, 1);

		// Console updater timeline
		updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateLog()));
		updateTimeline.play();

		// Set the cell factories
		DragSelectionCellFactory fac = new DragSelectionCellFactory();
		colLogMessage.setCellFactory(fac);
		colLogTime.setCellFactory(fac);
		colLogType.setCellFactory(fac);

		// Set the column cell value factories
		colLogTime.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("showTime"));
		colLogType.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("showType"));
		colLogMessage.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("line"));

		// Restrain column resizing
		tblConsoleOut.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Allow multiple selects
		tblConsoleOut.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// Remove the default empty table text
		tblConsoleOut.setPlaceholder(new Label(""));

		// Add log filters
		addFilterListeners();
		tblConsoleOut.setItems(filteredLog);

		// Update the filter labels
		resetLabels();
		updateLabels();
		
		addEventHandler(KeyEvent.KEY_PRESSED, this);
		
	}

	@Override
	public void handle(final KeyEvent e) {
		// Copy
		if (KeyCombination.valueOf(FXController.HOTKEY_COPY).match(e) && tblConsoleOut.isFocused()) {
			// Get all selected entries
			StringBuilder text = new StringBuilder();
			ObservableList<LogEntry> selected = tblConsoleOut.getSelectionModel().getSelectedItems();

			// Append the text from all entries
			selected.forEach(entry -> text.append(entry.getLine()).append('\n'));

			// Set the clipboard content
			final ClipboardContent content = new ClipboardContent();
			content.putString(text.toString());
			Clipboard.getSystemClipboard().setContent(content);

			e.consume();
		}
		// Clear
		else if (KeyCombination.valueOf(FXController.HOTKEY_CLEARCONSOLE).match(e)) {
			clear();
			e.consume();
		}
		// Search
		else if (KeyCombination.valueOf(FXController.HOTKEY_FIND).match(e)) {
			apnConsoleSearchBar.open(1);
			e.consume();
		}
	}

	/**
	 * Clears the console.
	 */
	public void clear() {
		buttonClearConsole();
	}
	@FXML
	private void buttonClearConsole() {
		// Clear the log in elasticsearch
		String robotId = getRobotID().toString();
		ESConsoleClient.getInstance().clearLog(robotId);

		// Clear the log entries
		masterLog.clear();

		// Reset all counts
		resetLabels();
		updateLabels();
	}

	private void updateLog() {
		Platform.runLater(() -> {
			// Clear the log
			masterLog.clear();

			// Reset the filter counts
			resetLabels();

			// Get the last log entries
			List<Map<String, Object>> entries = ESConsoleClient.getInstance().getLastEntries(getRobotID().toString(), maxEntries);
			for (Map<String, Object> entry : entries) {
				// Get all properties
				String time = timeFormat.format(new Date((long) entry.get("timestamp")));
				LogType type = LogType.valueOf(entry.get("type").toString().toUpperCase());
				String[] lines = entry.get("message").toString().split("\n");

				// Check if the message is empty
				if (lines.length == 0) {
					lines = new String[] {""};
				}

				// Add the first entry with type and time, and the message of all new lines
				addTableEntry(time, type, lines[0], false);
				for (int i = 1; i < lines.length; i++) {
					addTableEntry(time, type, lines[i], true);
				}
			}

			// Scroll to the bottom of the table
			int last = tblConsoleOut.getItems().size() - 1;
			if (last >= 0) {
				tblConsoleOut.scrollTo(last);
			}

			updateLabels();
		});
	}

	private void addTableEntry(final String time, final LogType type, final String line, final boolean newLine) {
		// Add the entry to the master log and add to the count
		masterLog.add(new LogEntry(time, type, line, newLine));
		if (!newLine) {
			count.put(type, count.get(type) + 1);
		}
	}

	/* Filters */

	private void addFilterListeners() {
		// Add default filters
		filters.put(LogType.INFO, tbnToggleLogsInfo.isSelected());
		filters.put(LogType.DEBUG, tbnToggleLogsDebug.isSelected());
		filters.put(LogType.WARN, tbnToggleLogsWarn.isSelected());
		filters.put(LogType.ERROR, tbnToggleLogsError.isSelected());
		filters.put(LogType.FATAL, tbnToggleLogsError.isSelected());

		// Add listeners for the toggle filter buttons
		tbnToggleLogsInfo.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != newValue) {
				filters.put(LogType.INFO, newValue);
				updateFilters();
			}
		});
		tbnToggleLogsDebug.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != newValue) {
				filters.put(LogType.DEBUG, newValue);
				updateFilters();
			}
		});
		tbnToggleLogsWarn.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != newValue) {
				filters.put(LogType.WARN, newValue);
				updateFilters();
			}
		});
		tbnToggleLogsError.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != newValue) {
				filters.put(LogType.ERROR, newValue);
				filters.put(LogType.FATAL, newValue);
				updateFilters();
			}
		});
	}

	private void updateFilters() {
		// Update the filter predicate
		filteredLog.setPredicate(entry -> {
			LogType type = entry.getType();
			return filters.get(type);
		});
	}

	/* Filter labels */

	private void resetLabels() {
		count.clear();
	}

	private void updateLabels() {
		// Set all labels for the filter buttons
		tbnToggleLogsInfo.setText(count.get(LogType.INFO).toString());
		tbnToggleLogsDebug.setText(count.get(LogType.DEBUG).toString());
		tbnToggleLogsWarn.setText(count.get(LogType.WARN).toString());
		tbnToggleLogsError.setText(Integer.toString(count.get(LogType.ERROR) + count.get(LogType.FATAL)));
	}

	/* Searching */

	@Override
	public void searchPattern(final String pattern, final boolean caseSensitive) {
		// Clear the list
		occurences.clear();

		// Check if the pattern is valid
		Pattern validPattern;
		try {
			validPattern = caseSensitive ? Pattern.compile(pattern) : Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			// Search over all log entries
			for (int i = 0; i < tblConsoleOut.getItems().size(); i++) {
				if (validPattern.matcher(tblConsoleOut.getItems().get(i).getLine()).find()) {
					occurences.add(i);
				}
			}
			checkSelection();
		} catch (PatternSyntaxException e) {
			// nothing to do
		}
	}

	@Override
	public void search(final String needle, final boolean caseSensitive) {
		// Clear the list
		occurences.clear();

		// Search over all log entries
		for (int i = 0; i < tblConsoleOut.getItems().size(); i++) {
			String line = tblConsoleOut.getItems().get(i).getLine();
			// Check case sensitivity
			if (!caseSensitive) {
				line = line.toLowerCase();
			}

			if (line.contains(caseSensitive ? needle : needle.toLowerCase())) {
				occurences.add(i);
			}
		}

		checkSelection();
	}

	private void checkSelection() {
		// Clear the selection if we have no matches
		if (occurences.isEmpty()) {
			tblConsoleOut.getSelectionModel().clearSelection();
		}
	}

	@Override
	public int getOccurrences() {
		return occurences.size();
	}

	@Override
	public void highlight(final int occurrence) {
		// Clear and select, scroll to the occurrence
		int line = occurences.get(occurrence);
		tblConsoleOut.getSelectionModel().clearAndSelect(line);
		tblConsoleOut.scrollTo(line);
	}

	@Override
	public void highlightAll() {
		// Clear the selection and highlight all occurrences
		tblConsoleOut.getSelectionModel().clearSelection();
		occurences.forEach(i -> tblConsoleOut.getSelectionModel().select(i));
	}

	@Override
	public void clearSearch() {
		tblConsoleOut.getSelectionModel().clearSelection();
	}

	/* Drag selection */

	/**
	 * The cell factory which creates DragSelectionCells for the console table.
	 */
	private class DragSelectionCellFactory implements Callback<TableColumn<LogEntry, String>, TableCell<LogEntry, String>> {
		@Override
		public TableCell<LogEntry, String> call(final TableColumn<LogEntry, String> col) {
			return new DragSelectionCell();
		}
	}

	/**
	 * A selection cell which can be selected by dragging over it with the mouse.
	 */
	private class DragSelectionCell extends TableCell<LogEntry, String> {
		public DragSelectionCell() {
			// Set event handlers
			setOnDragDetected(new DragDetectedEventHandler(this));
			setOnMouseDragEntered(new DragEnteredEventHandler(this));
		}

		@Override
		public void updateItem(final String item, final boolean empty) {
			// Update the content
			super.updateItem(item, empty);
			setText(empty ? null : item);
			setTooltip(new Tooltip(item));
		}
	}

	private class DragDetectedEventHandler implements EventHandler<MouseEvent> {
		private final TableCell<LogEntry, String> tableCell;

		public DragDetectedEventHandler(final TableCell<LogEntry, String> tableCell) {
			this.tableCell = tableCell;
		}

		@Override
		public void handle(final MouseEvent event) {
			// Start dragging
			tableCell.startFullDrag();
		}
	}

	private class DragEnteredEventHandler implements EventHandler<MouseDragEvent> {
		private final TableCell<LogEntry, String> tableCell;

		public DragEnteredEventHandler(final TableCell<LogEntry, String> tableCell) {
			this.tableCell = tableCell;
		}

		@Override
		public void handle(final MouseDragEvent event) {
			// When the mouse drag enters the cell, perform a selection
			performSelection(tableCell.getTableView(), tableCell.getIndex());
		}
	}

	private static void performSelection(final TableView<LogEntry> table, final int index) {
		// Get the table anchor
		@SuppressWarnings("unchecked")
		final TablePositionBase<TableColumn<LogEntry, String>> anchor = CellBehaviorBase.getAnchor(table, table.getFocusModel().getFocusedCell());

		// Get the min and max row index and select the rows in that range
		int minRowIndex = Math.min(anchor.getRow(), index);
		int maxRowIndex = Math.max(anchor.getRow(), index);
		table.getSelectionModel().selectRange(minRowIndex, maxRowIndex + 1);

		// Set the focus
		table.getFocusModel().focus(index);
	}

	@Override
	public void initialize(RobotTab tab) {
		this.tab = tab;
		
		this.tab.getProcessor().getDebugger().getOnRobotStart().addListener(start -> updateLog());
		ESConsoleClient.getLogEvent(tab.getProcessor().getRobotID()).addListener(msg -> updateTimeline.play());
	}
	
	private RobotID getRobotID() {
		return tab.getProcessor().getRobotID();
	}
}
