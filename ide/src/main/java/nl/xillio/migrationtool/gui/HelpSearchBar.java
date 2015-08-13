package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

/**
 * A search bar, with the defined options and behavior.
 */
public class HelpSearchBar extends AnchorPane {

	private HelpPane helpPane;

	// Implement the FXML for this
	@FXML
	private ComboBox<String> box;
	//private final DocumentSearcher searcher;
	private int comboBoxLength = 0;

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
			e.printStackTrace();
		}

		/*// The searcher
		searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
		box.setEditable(true);

		// Handle click
		box.setOnAction((event) -> {
			try {
				String[] s = box.getSelectionModel().getSelectedItem().toString().split("\\.");
				box.hide();
				helpPane.display(s[0], s[1]);
			} catch (Exception e) {}
		});

		// Handle text getting edited.
		box.getEditor().textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (oldValue != newValue) {
				runSearch(newValue);
			}
		});
		getChildren().add(box);*/
	}

	/**
	 * @param help
	 *        The help pane in which the search bar is embedded
	 */
	public void setHelpPane(final HelpPane help) {
		helpPane = help;
	}

	// Runs the search
	private void runSearch(final String query) {
		/*if (query != null && !query.isEmpty()) {
			// Search for a list of possible functions and store the result
			String[] results = searcher.search(query);
			ObservableList<String> options = FXCollections.observableArrayList();
			for (String result : results) {
				options.add(result);
			}

			// Adjust the combobox accordingly
			Platform.runLater(() -> {
				box.getItems().clear();
				box.getItems().addAll(results);
				if (!box.getItems().isEmpty()) {
					if (box.getItems().size() != comboBoxLength) {
						box.hide();
						box.show();
						comboBoxLength = box.getItems().size();
					}
				} else {
					box.hide();
				}
			});
		} else {
			box.hide();
		}*/
	}
}
