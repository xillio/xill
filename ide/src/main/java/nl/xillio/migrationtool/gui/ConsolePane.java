package nl.xillio.migrationtool.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import nl.xillio.migrationtool.elasticconsole.Counter;
import nl.xillio.migrationtool.elasticconsole.ESConsoleClient;
import nl.xillio.migrationtool.elasticconsole.ESConsoleClient.LogType;
import nl.xillio.migrationtool.elasticconsole.LogEntry;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.preview.Searchable;

import com.sun.javafx.scene.control.behavior.CellBehaviorBase;

/**
 * This pane displays the console log stored in elasticsearch
 */
public class ConsolePane extends AnchorPane implements Searchable, EventHandler<KeyEvent>, RobotTabComponent {
	@FXML
	private SearchBar apnConsoleSearchBar;
	@FXML
	private TableView<LogEntry> tblConsoleOut;
	@FXML
	private TableColumn<LogEntry, String> colLogTime;
	@FXML
	private TableColumn<LogEntry, String> colLogType;
	@FXML
	private TableColumn<LogEntry, String> colLogMessage;
	@FXML
	private ToggleButton tbnToggleLogsInfo;
	@FXML
	private ToggleButton tbnToggleLogsDebug;
	@FXML
	private ToggleButton tbnToggleLogsWarn;
	@FXML
	private ToggleButton tbnToggleLogsError;
	@FXML
	private ToggleButton tbnConsoleSearch;
	@FXML
	private Label tbnLogsCount;
	@FXML
	private Button btnNavigateBack;
	@FXML
	private Button btnNavigateForward;
	@FXML
	private Slider sldNavigation;

	public static enum Scroll {
		NONE, START, END, TOTALEND, CLEAR
	}

	// private Robot robot;

	// Log entry lists. Master contains everything, filtered contains only selected entries.
	private final ObservableList<LogEntry> masterLog = FXCollections.observableArrayList();
	private final FilteredList<LogEntry> filteredLog = new FilteredList<>(masterLog, e -> true);

	// Updating the console
	private final Timeline updateTimeline;
	private final int maxEntries = 1000; // the number of lines in one "page"
	private int startEntry = 0; // first entry to show
	private int entriesCount = 0; // number of total entries in ES
	private boolean updateSlider = true; // if changes on slider value has to invoke updateLog()
	private final Timeline sliderTimeline; // update cycle for slider changes
	private boolean sliderChanged = false; // if slider value has changed from outside - because of some user activity

	// Time format
	private final DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// Filters
	private final Map<LogType, Boolean> filters = new HashMap<>(LogType.values().length);
	private final Map<LogType, Integer> count = new Counter<>();

	private final List<Integer> occurences = new ArrayList<>();
	private RobotTab tab;

	private boolean searchRegExp = false;
	private String searchNeedle = "";

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
		updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateLog(Scroll.TOTALEND, false)));
		updateTimeline.play();

		// Log updater - when some slider changes happen
		sliderTimeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
			if (this.sliderChanged) {
				Platform.runLater(() -> {
					this.updateLog(Scroll.START, true);
				});
			}
		}));
		sliderTimeline.setCycleCount(Timeline.INDEFINITE);
		sliderTimeline.play();

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

		// Listener for slider value changes
		sldNavigation.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (this.updateSlider) {
				this.startEntry = (this.entriesCount * newValue.intValue()) / 100;
				this.sliderChanged = true;
			}
		});

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
	private void buttonNavigateBack() {
		this.startEntry -= this.maxEntries;
		if (this.startEntry < 0) {
			this.startEntry = 0;
		}
		this.updateLog(Scroll.END, false);
	}

	@FXML
	private void buttonNavigateForward() {
		this.startEntry += this.maxEntries;
		this.updateLog(Scroll.START, false);
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
		updateLog(Scroll.CLEAR, false);
	}

	private void updateLog(Scroll scroll, boolean fromSlider) {
		Platform.runLater(() -> {
			// Clear the log
			masterLog.clear();

			// Reset the filter counts
			resetLabels();

			if (scroll == Scroll.CLEAR) {
				// this explicit variant is here because if you click Clear then the ES starts to remove all items,
				// but when runtime come here, ES still can have some values "not deleted yet" and
				// because the clear operation on ES is asynchronous
				this.startEntry = 0;
				this.entriesCount = 0;
				tbnLogsCount.setText("0-0/0");
				updateLabels();
				return;
			}

			ESConsoleClient.SearchFilter filter = ESConsoleClient.getInstance().createSearchFilter(searchNeedle, this.searchRegExp, this.filters);
			;

			this.entriesCount = ESConsoleClient.getInstance().countFilteredEntries(getRobotID().toString(), filter);

			int lastEntry = this.startEntry + this.maxEntries - 1;
			if ((this.entriesCount <= lastEntry) || (scroll == Scroll.TOTALEND)) {
				lastEntry = this.entriesCount - 1;
				this.startEntry = lastEntry - this.maxEntries + 1;
				if (this.startEntry < 0) {
					this.startEntry = 0;
				}
			}
			int showCount = lastEntry - this.startEntry + 1;

			if (!fromSlider) {
				this.updateSlider = false;
				if (this.entriesCount == 0) {
					this.sldNavigation.setValue(0);
				} else {
					this.sldNavigation.setValue((this.startEntry * 100) / this.entriesCount);
				}
				this.updateSlider = true;
			}

			if (this.entriesCount == 0) {
				tbnLogsCount.setText("0-0/0");
			} else {
				tbnLogsCount.setText(String.format("%1$d-%2$d/%3$d", this.startEntry + 1, lastEntry + 1, this.entriesCount));
			}

			List<Map<String, Object>> entries = ESConsoleClient.getInstance().getFilteredEntries(
				getRobotID().toString(), this.startEntry, lastEntry, filter);

			for (Map<String, Object> entry : entries) {
				// Get all properties
				String time = timeFormat.format(new Date((long) entry.get("timestamp")));
				LogType type = LogType.valueOf(entry.get("type").toString().toUpperCase());
				String text = entry.get("message").toString();
				addTableEntry(time, type, text, false);
			}

			// Do scroll
			if ((scroll == Scroll.END) || (scroll == Scroll.TOTALEND)) {
				tblConsoleOut.scrollTo(tblConsoleOut.getItems().size() - 1);
			} else if (scroll == Scroll.START) {
				tblConsoleOut.scrollTo(0);
			}

			updateLabels();
			this.sliderChanged = false;
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
		this.updateLog(Scroll.NONE, false);
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

		this.searchNeedle = pattern;
		this.searchRegExp = true;
		this.startEntry = 0;
		this.updateLog(Scroll.START, false);
	}

	@Override
	public void search(final String needle, final boolean caseSensitive) {
		// Clear the list
		occurences.clear();

		this.searchNeedle = needle;
		this.searchRegExp = false;
		this.startEntry = 0;
		this.updateLog(Scroll.START, false);
	}

	@Override
	public int getOccurrences() {
		return occurences.size();
	}

	@Override
	public void highlight(final int occurrence) {
		// Clear and select, scroll to the occurrence
		int line = occurrence; // only selected lines are shown in tblConsoleOut so line = occurrence
		tblConsoleOut.getSelectionModel().clearAndSelect(line);
		tblConsoleOut.scrollTo(line);
	}

	@Override
	public void highlightAll() {
		// not used
	}

	@Override
	public void clearSearch() {
		this.searchNeedle = "";
		this.updateFilters(); // show all lines (without searching affected)
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

			// show a TextArea on double click, for selecting text partially
			setOnMouseClicked(new DoubleClickEventHandler(this));

			createText();

			// Update width
			Platform.runLater(() -> setWidth(getWidth() + 0.1));
		}

		/**
		 * Create the contents of this cell as {@link Text}
		 */
		public void createText() {
			Text text = new Text();
			text.wrappingWidthProperty().bind(this.widthProperty());
			text.textProperty().bind(this.itemProperty());

			setGraphic(text);
		}

		/**
		 * Create the contents of this cell as {@link TextArea}, useful for selecting text partially
		 */
		public void createTextArea() {
			TextArea textArea = new TextArea();
			textArea.setWrapText(true);
			textArea.textProperty().bind(this.itemProperty());
			// Keep the same height as before
			textArea.setPrefHeight(getHeight());

			// Just show text when focus is lost
			textArea.focusedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
				if (!newValue) {
					createText();
				}
			});

			setGraphic(textArea);

			// Request focus to prevent TextArea from staying visible
			textArea.requestFocus();
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

	private class DoubleClickEventHandler implements EventHandler<MouseEvent> {
		private final DragSelectionCell tableCell;

		public DoubleClickEventHandler(final DragSelectionCell tableCell) {
			this.tableCell = tableCell;
		}

		@Override
		public void handle(MouseEvent event) {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
				tableCell.createTextArea();
			}
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
	public void initialize(final RobotTab tab) {
		this.tab = tab;

		this.tab.getProcessor().getDebugger().getOnRobotStart().addListener(start -> updateLog(Scroll.TOTALEND, false));
		ESConsoleClient.getLogEvent(tab.getProcessor().getRobotID()).addListener(msg -> updateTimeline.play());
	}

	private RobotID getRobotID() {
		return tab.getProcessor().getRobotID();
	}
}
