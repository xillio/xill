package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import nl.xillio.xill.api.components.Instruction;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;

/**
 * This pane can show the current position in the stack
 */
public class InstructionStackPane extends AnchorPane implements RobotTabComponent, ChangeListener<Instruction> {
    @FXML
    private ComboBox<Instruction> cbxStackPos;
    private RobotTab tab;

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

	cbxStackPos.getSelectionModel().selectedItemProperty().addListener(this);
    }

    /**
     * Get the ComboBox that contains the instruction stack.
     * 
     * @return A ComboBox with the string representations of the instructions.
     */
    public ComboBox<Instruction> getInstructionBox() {
	return cbxStackPos;
    }

    /**
     * Refresh the position
     */
    public void refresh() {
	Platform.runLater(() -> {
	    cbxStackPos.setItems(FXCollections.observableArrayList(tab.getProcessor().getDebugger().getStackTrace()));
	    cbxStackPos.getSelectionModel().select(cbxStackPos.getItems().size() - 1);
	});
    }

    private void onRobotPause(final RobotPausedAction action) {
	refresh();
    }

    private void onRobotStop(final RobotStoppedAction action) {
	Platform.runLater(() -> {
	    cbxStackPos.getItems().clear();
	});
    }

    @Override
    public void initialize(final RobotTab tab) {
	this.tab = tab;

	tab.getProcessor().getDebugger().getOnRobotPause().addListener(this::onRobotPause);
	tab.getProcessor().getDebugger().getOnRobotStop().addListener(this::onRobotStop);
    }

    @Override
    public void changed(final ObservableValue<? extends Instruction> observable, final Instruction oldValue,
	    final Instruction newValue) {
	if (newValue != null) {
	    tab.display(newValue.getRobotID(), newValue.getLineNumber());
	}
    }
}
