package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.migrationtool.documentation.DocumentSearcher;
import nl.xillio.xill.api.preview.Searchable;

/**
 * A search bar, with the defined options and behavior.
 */
public class HelpSearchBar extends AnchorPane implements EventHandler<KeyEvent> {

	private HelpPane helpPane;


	//Implement the FXML for this
	@FXML
	private ComboBox<String> box;
	private DocumentSearcher searcher;

	/**
	 * Default constructor. The bar won't do anything until {@link #setSearchable(Searchable)} is called.
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
		
		
		//The searcher
		searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
		
		//The results combobox
		box = new ComboBox<String>();
		box.setEditable(true);
		//Handle click
		box.setOnAction((event) -> {
			try{
			String s = box.getSelectionModel().getSelectedItem().toString();			
			this.helpPane.display(s);
			}
			catch(Exception e)
			{}
		});
		
		box.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			try
			{
				if(event.getCode() == KeyCode.ENTER)
				{
				runSearch(box.getValue().toString());
				}
				else
					reset();
					
				}
			catch (Exception e)
			{
				
			}
	    });

			
		
		this.getChildren().add(box);
		reset();
		this.addEventHandler(KeyEvent.KEY_PRESSED, this);
	}

	/**
	 * Resets the search bar: resets the counts and optionally the query.
	 *
	 * @param clearQuery
	 *        whether the query should be erased
	 */
	public void reset() {
		box.getItems().clear();
		box.hide();
	}
	
	/**
	 * @param help The help pane in which the search bar is embedded
	 */
	public void setHelpPane(HelpPane help) {
		this.helpPane = help;
	}

	private void runSearch(String query) {
		// A new search query. Clear old data
		reset();
		String[] results = searcher.search(query);
		ObservableList<String> options = FXCollections.observableArrayList();
		for(String result : results)
			options.add(result);
		box.getItems().addAll(results);
		box.autosize();
		if(!box.getItems().isEmpty())
			box.show();
	}

	@Override
	public void handle(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
