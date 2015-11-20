package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import nl.xillio.xill.api.preview.Replaceable;
import nl.xillio.xill.api.preview.Searchable;

public class ReplaceBar extends SearchBar {

	@FXML
	private TextField tfEditorReplaceString;

	public ReplaceBar() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReplaceBar.fxml"));
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(this);
			Node ui = loader.load();
			((VBox) getChildren().get(0)).getChildren().add(ui);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setSearchable(final Searchable searchable) {
		if (searchable instanceof Replaceable) {
			super.setSearchable(searchable);
		}
	}

	public Replaceable getReplaceable() {
		return (Replaceable) super.getSearchable();
	}

	public String getReplacement() {
		return tfEditorReplaceString.getText();
	}

	@FXML
	private void onReplace(final ActionEvent actionEvent) {
		getReplaceable().replaceOne(currentOccurrence, getReplacement());
	}

	@FXML
	private void onReplaceAll(final ActionEvent actionEvent) {
		getReplaceable().replaceAll(getReplacement());
	}

}
