package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.migrationtool.documentation.DocumentSearcher;

/**
 * A search bar, with the defined options and behavior.
 */
public class HelpSearchBar extends AnchorPane{

	private HelpPane helpPane;


	//Implement the FXML for this
	@FXML
	private ComboBox<String> box;
	private DocumentSearcher searcher;

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
		
		//The searcher
		searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
		box.setEditable(true);
		
		//Handle click
		box.setOnAction((event) -> {
			try{
			String[] s = box.getSelectionModel().getSelectedItem().toString().split("\\.");
			box.hide();
			this.helpPane.display(s[0], s[1]);
			}
			catch(Exception e)
			{}
		});
		
		//Handle text getting edited.
		box.getEditor().textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		    	if(oldValue != newValue)
		    		runSearch(newValue);
		    }
		});	
		this.getChildren().add(box);
	}
	
	/**
	 * @param help The help pane in which the search bar is embedded
	 */
	public void setHelpPane(HelpPane help) {
		this.helpPane = help;
	}
	
	//Runs the search
	private void runSearch(String query) {
		if(query != null)
		{
		//Search for a list of possible functions and store the result
		String[] results = searcher.search(query);
		ObservableList<String> options = FXCollections.observableArrayList();
		for(String result : results)
			options.add(result);
		
		//Adjust the combobox accordingly
		Platform.runLater(new Runnable() {
		    @Override public void run() {	    
			box.getItems().clear();
			box.getItems().addAll(results);
			box.autosize();
			if(!box.getItems().isEmpty())
				box.show();
			else
				box.hide();
			}});
		}
	}
}
