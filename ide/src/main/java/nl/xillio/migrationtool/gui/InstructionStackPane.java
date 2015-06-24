package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

/**
 * This pane can show the current position in the stack
 */
public class InstructionStackPane extends AnchorPane {
	@FXML
	private ComboBox<String> cbxStackPos;


	public InstructionStackPane() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InstructionStackPane.fxml"));
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(this);
			Node ui = loader.load();
			getChildren().add(ui);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the ComboBox that contains the instruction stack.
	 * 
	 * @return A ComboBox with the string representations of the instructions.
	 */
	public ComboBox<String> getInstructionBox() {
		return cbxStackPos;
	}
}
