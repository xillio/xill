package nl.xillio.migrationtool.gui;

import javafx.collections.FXCollections;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

/**
 * A search bar, with the defined options and behavior.
 * @author Thomas Biesaart
 */
public class HelpSearchBar extends AnchorPane {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int ROW_HEIGHT = 26;
	private final ListView<String> listView;
	private HelpPane helpPane;
	private final Tooltip hoverToolTip;

	@FXML
	private TextField searchField;

	private ObservableList<String> data = FXCollections.observableArrayList();
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

		//Result list
		listView = new ListView<>(data);
		listView.setOnMouseClicked(this::onClick);
		listView.setOnKeyPressed(this::onKeyPressed);

		//Result wrapper
		hoverToolTip = new Tooltip();
		hoverToolTip.setGraphic(listView);
		hoverToolTip.prefWidthProperty().bind(searchField.widthProperty());

		//Listen to search changes
		searchField.textProperty().addListener(this::searchTextChanged);
	}

	private void onKeyPressed(KeyEvent keyEvent) {
		if(keyEvent.getCode() == KeyCode.ENTER) {
			openSelected();
		}
	}

	private void onClick(MouseEvent mouseEvent) {
		openSelected();
	}


	void openSelected() {
		String selected = listView.getSelectionModel().getSelectedItem();

		if(selected == null) {
			return;
		}

		String[] parts = selected.split("\\.");
		helpPane.display(parts[0], parts[1]);
		hoverToolTip.hide();
		searchField.clear();
		helpPane.requestFocus();
	}

	/**
	 * Set the searcher that should be used.
	 * @param searcher the searcher
	 */
	public void setSearcher(DocumentationSearcher searcher) {
		this.searcher = searcher;
	}

	/**
	 * Set the HelpPane.
	 * @param help The help pane in which the search bar is embedded
	 */
	public void setHelpPane(final HelpPane help) {
		helpPane = help;
	}


	// Runs the search
	private void runSearch(final String query) {
		if(searcher == null) {
			data.clear();
			return;
		}

		String[] results = searcher.search(query);

		data.clear();
		data.addAll(results);
	}

	private void searchTextChanged(Object source, String oldValue, String newValue) {
		if(newValue == null || newValue.isEmpty()) {
			hoverToolTip.hide();
			return;
		}

		runSearch(searchField.getText());

		showResults();
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
