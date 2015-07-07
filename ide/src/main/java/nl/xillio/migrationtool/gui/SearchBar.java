package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import nl.xillio.xill.api.preview.Searchable;

/**
 * A search bar, with the defined options and behavior.
 */
public class SearchBar extends AnchorPane implements EventHandler<KeyEvent> {

	private Searchable searchable;
	private String currentSearch = "";

	/**
	 * The current highlighted occurrence
	 */
	protected int currentOccurence;
	private Node storedParent;

	// Nodes
	@FXML
	private TextField tfEditorSearchQuery;
	@FXML
	private Label lblEditorSearchIndex, lblEditorSearchCount;
	@FXML
	private ToggleButton tbnEditorRegexSearch, tbnEditorCaseSensitive;
	private ToggleButton toggleButton;

	/**
	 * Default constructor. The bar won't do anything until {@link #setSearchable(Searchable)} is called.
	 */
	public SearchBar() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchBar.fxml"));
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(this);
			Node ui = loader.load();
			getChildren().add(ui);
		} catch (IOException e) {
			e.printStackTrace();
		}
		reset(true);
		enableSearchAsYouType();
		
		
		
		this.addEventHandler(KeyEvent.KEY_PRESSED, this);
	}

	/**
	 * Resets the search bar: resets the counts and optionally the query.
	 *
	 * @param clearQuery
	 *        whether the query should be erased
	 */
	public void reset(final boolean clearQuery) {
		currentOccurence = 0;
		lblEditorSearchIndex.setText("0");
		lblEditorSearchCount.setText("0");
		if (clearQuery)
			tfEditorSearchQuery.clear();
	}
	
	/**
	 * Set the toggle button for this searchbar
	 * @param toggleButton
	 * @param id The index to pass to {@link SearchBar#open(int)}
	 */
	public void setButton(ToggleButton toggleButton, int id){
		this.toggleButton = toggleButton;
		
		toggleButton.selectedProperty().addListener(
			(obs, oldVal, newVal) -> {
				if(newVal)
					open(id);
				else
					close(true);
			});
	}

	private void enableSearchAsYouType() {
		tfEditorSearchQuery.textProperty().addListener((ChangeListener<String>) (arg0, oldvalue, newvalue) -> runSearch(newvalue));
	}

	private void runSearch(final String query) {
		// A new search query. Clear old data
		reset(false);

		currentSearch = query;

		// Check if the query is empty
		if (query.isEmpty()) {
			searchable.clearSearch();
			return;
		}

		if (isRegexEnabled())
			searchable.searchPattern(query, isCaseSensitive());
		else
			searchable.search(query, isCaseSensitive());

		// Refresh the result counts
		Platform.runLater(() -> lblEditorSearchCount.setText(String.valueOf(searchable.getOccurrences())));
		
		// Highlight 0 to make sure the first result is visible, then highlight all
		if(searchable.getOccurrences() > 0)
			highlight(0);
		searchable.highlightAll();
	}

	/**
	 * Returns whether regex are enabled.
	 *
	 * @return whether regex are enabled
	 */
	public boolean isRegexEnabled() {
		return tbnEditorRegexSearch.selectedProperty().get();
	}

	/**
	 * Returns whether the search should be case sensitive.
	 *
	 * @return whether the search should be case sensitive
	 */
	public boolean isCaseSensitive() {
		return tbnEditorCaseSensitive.selectedProperty().get();
	}
	
	private String getSearchQuery() {
		return tfEditorSearchQuery.getText();
	}

	private void highlight(final int i) {
		int index = i;
		// No ocurrences is no highlight
		if (searchable.getOccurrences() != 0) {
			// Wrap the index
			index %= searchable.getOccurrences();
			if (index < 0)
				index += searchable.getOccurrences();
			
			// Prevent negative index
			index = Math.max(index, 0);

			searchable.highlight(index);
			
			// push to label
			lblEditorSearchIndex.setText(String.valueOf(index + 1));
			
			// Save actual highlight
			currentOccurence = index;
		}
	}

	/**
	 * Attaches a searchable item to this bar.
	 *
	 * @param searchable
	 *        the item to attach to this bar
	 */
	public void setSearchable(final Searchable searchable) {
		this.searchable = searchable;
		// Close on load
		close(false);
	}

	/**
	 * Returns the searchable attached to this bar.
	 *
	 * @return the searchable attached to this bar
	 */
	public Searchable getSearchable() {
		return searchable;
	}

	///////////////////// Controls /////////////////////
	
	@FXML
	private void nextButtonPressed(final ActionEvent actionEvent) {
		if (!currentSearch.isEmpty())
			highlight(++currentOccurence);
	}

	@FXML
	private void previousButtonPressed() {
		if (!currentSearch.isEmpty())
			highlight(--currentOccurence);
	}

	@FXML
	private void caseButtonPressed() {
		runSearch(getSearchQuery());
	}

	@FXML
	private void regexButtonPressed() {
		runSearch(getSearchQuery());
	}

	@FXML
	private void closeButtonPressed() {
		close(true);
	}
	
	///////////////////// Open and close /////////////////////
	
	/**
	 * Hide the searchbar and remove it from the flow.
	 */
	private void close(boolean clear) {
		// Clear the search
		if (clear)
			searchable.clearSearch();
		
		// Remove this from its parent
		if (getParent() != null) {
			storedParent = getParent();
			if (storedParent instanceof Pane)
				((Pane)storedParent).getChildren().remove(this);
		}
		
		// Unselect the toggle button
		if(toggleButton != null)
			toggleButton.setSelected(false);
	}

	/**
	 * Open the searchbar as the first child of it's parent.
	 */
	public void open() {
		open(0);
	}
	/**
	 * Open up the search bar as a child of it's previous parent
	 *
	 * @param index
	 *        the index in the list of it's parent's children.
	 */
	public void open(final int index) {
		// Check if we have a parent
		if (storedParent != null && getParent() == null) {
			close(false);
			
			// Add this as a child
			if (storedParent instanceof Pane)
				((Pane)storedParent).getChildren().add(index, this);
			
			requestFocus();
		}
		
		// Select the toggle button
		if(toggleButton != null)
			toggleButton.setSelected(true);
	}

	@Override
	public void requestFocus() {
		tfEditorSearchQuery.requestFocus();
		tfEditorSearchQuery.selectAll();
	}

	@Override
	public void handle(KeyEvent k) {
		//If enter is pressed, search
		if (tfEditorSearchQuery.isFocused() && !k.isConsumed() && k.getCode() == KeyCode.ENTER && currentSearch != null) {
			runSearch(currentSearch);
			k.consume();
		}
	}
}
