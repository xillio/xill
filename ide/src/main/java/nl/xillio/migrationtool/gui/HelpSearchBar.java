package nl.xillio.migrationtool.gui;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import nl.xillio.xill.docgen.DocumentationSearcher;

/**
 * A search bar, with the defined options and behavior.
 *
 * @author Thomas Biesaart
 */
public class HelpSearchBar extends AnchorPane {
	private static final Logger LOGGER = LogManager.getLogger();
	private static int ROW_HEIGHT = 29;
	private final ListView<String> listView;
	private HelpPane helpPane;
	private final Tooltip hoverToolTip;

	@FXML
	private TextField searchField;

	private final ObservableList<String> data = FXCollections.observableArrayList();
	private DocumentationSearcher searcher;

	/**
	 * Default constructor.
	 */
	public HelpSearchBar() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelpSearchBar.fxml"));
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(this);
			Node ui = loader.load();
			getChildren().add(ui);
		} catch (IOException e) {
			LOGGER.error("Failed to load help pane", e);
		}

		// Result list
		listView = new ListView<>(data);
		listView.setOnMouseClicked(this::onClick);
		listView.setOnKeyPressed(this::onKeyPressed);
		listView.setPrefHeight(ROW_HEIGHT);
		listView.setFixedCellSize(ROW_HEIGHT);

		// Result wrapper
		hoverToolTip = new Tooltip();
		hoverToolTip.setGraphic(listView);
		hoverToolTip.prefWidthProperty().bind(searchField.widthProperty());

		data.addListener((ListChangeListener<Object>) change -> listView.setPrefHeight((Math.min(10, data.size())) * ROW_HEIGHT + 2));

		searchField.setPromptText("Type here to start a search");
		// Listen to search changes
		searchField.textProperty().addListener(this::searchTextChanged);

		// Close on focus lost
		searchField.focusedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				showResults();
			} else {
				hoverToolTip.hide();
			}
		});
	}

	/**
	 * Handles a key press.
	 * Checks for an ENTER and then tries to open a page.
	 *
	 * @param keyEvent
	 */
	public void onKeyPressed(final KeyEvent keyEvent) {
		if (keyEvent.getCode() == KeyCode.ENTER) {
			String content = searchField.getText();

			if (content.isEmpty()) {
				helpPane.displayHome();
				cleanup();
			} else if (content.split(" ").length == 1) {
				tryDisplayIndexOf(content);
			}
		}
	}

	/**
	 * Tries to display the index of a given package name.
	 *
	 * @param packet
	 *        The name of the package we try to display.
	 */
	private void tryDisplayIndexOf(final String packet) {
		try {
			helpPane.display(packet, "_index");
			cleanup();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Handles the clicking mechanism.
	 *
	 * @param mouseEvent
	 *        The mouseEvent.
	 */
	private void onClick(final MouseEvent mouseEvent) {
		openSelected();
	}

	/**
	 * Opens the selected item.
	 */
	void openSelected() {
		String selected = listView.getSelectionModel().getSelectedItem();

		if (selected == null) {
			return;
		}

		String[] parts = selected.split("\\.");
		helpPane.display(parts[0], parts[1]);
		cleanup();
	}

	/**
	 * Cleans the UI (hiding the tooltips, clearing its content etc).
	 */
	void cleanup() {
		hoverToolTip.hide();
		data.clear();
		searchField.clear();
		helpPane.requestFocus();
	}

	/**
	 * Set the searcher that should be used.
	 *
	 * @param searcher
	 *        the searcher
	 */
	public void setSearcher(final DocumentationSearcher searcher) {
		this.searcher = searcher;
	}

	/**
	 * Set the HelpPane.
	 *
	 * @param help
	 *        The help pane in which the search bar is embedded
	 */
	public void setHelpPane(final HelpPane help) {
		helpPane = help;
	}

	// Runs the search
	private void runSearch(final String query) {
		if (searcher == null) {
			data.clear();
			return;
		}

		String[] results = searcher.search(query);
		data.clear();
		data.addAll(results);
	}

	private void searchTextChanged(final Object source, final String oldValue, final String newValue) {
		if (newValue == null || newValue.isEmpty()) {
			hoverToolTip.hide();
			return;
		}

		runSearch(searchField.getText());

		showResults();
	}

	public void handleHeightChange() {
		if (hoverToolTip.isShowing()) {
			showResults();
		}
	}

	private void showResults() {
		Point2D position = searchField.localToScene(0.0, 0.0);
		Scene scene = searchField.getScene();
		Window window = scene.getWindow();

		hoverToolTip.show(
			searchField,
			position.getX() + scene.getX() + window.getX(),
			position.getY() + scene.getY() + window.getY() + searchField.getHeight());
	}
}
