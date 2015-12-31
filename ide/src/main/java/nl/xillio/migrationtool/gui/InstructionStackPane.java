package nl.xillio.migrationtool.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotPausedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This pane can show the current position in the stack
 */
public class InstructionStackPane extends AnchorPane implements RobotTabComponent, ChangeListener<InstructionStackPane.Wrapper<Instruction>> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_STACK = 40;
    @FXML
    private ComboBox<Wrapper<Instruction>> cbxStackPos;
    private RobotTab tab;
    private DebugPane debugPane;

    public InstructionStackPane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InstructionStackPane.fxml"));
            loader.setClassLoader(getClass().getClassLoader());
            loader.setController(this);
            Node ui = loader.load();
            getChildren().add(ui);
        } catch (IOException e) {
            LOGGER.error("Error while loading instruction stack pane: " + e.getMessage(), e);
        }

        cbxStackPos.getSelectionModel().selectedItemProperty().addListener(this);
    }

    /**
     * Get the ComboBox that contains the instruction stack.
     *
     * @return A ComboBox with the string representations of the instructions.
     */
    public ComboBox<Wrapper<Instruction>> getInstructionBox() {
        return cbxStackPos;
    }

    /**
     * Refresh the position
     */
    public void refresh() {
        List<Instruction> stackTrace = tab.getProcessor().getDebugger().getStackTrace();
        List<Instruction> items;

        if (stackTrace.size() > MAX_STACK) {
            // The stack is too large to display, show a smaller one
            items = new ArrayList<>(MAX_STACK);

            // Dummy instruction for info line
            items.add(new DummyInstruction(tab.getProcessor().getRobotID(), stackTrace.size() - MAX_STACK));

            // Top MAX
            for (int i = 0; i < MAX_STACK; i++) {
                items.add(stackTrace.get(stackTrace.size() - MAX_STACK + i));
            }

        } else {
            items = stackTrace;
        }

        cbxStackPos.setItems(FXCollections.observableArrayList(items.stream().map(Wrapper::new).collect(Collectors.toList())));
        cbxStackPos.getSelectionModel().clearSelection();
        cbxStackPos.getSelectionModel().selectLast();
    }

    private void onRobotPause(final RobotPausedAction action) {
        Platform.runLater(this::refresh);
    }

    private void onRobotStop(final RobotStoppedAction action) {
        Platform.runLater(() -> cbxStackPos.getItems().clear());
    }

    @Override
    public void initialize(final RobotTab tab) {
        this.tab = tab;

        getDebugger().getOnRobotPause().addListener(this::onRobotPause);
        getDebugger().getOnRobotStop().addListener(this::onRobotStop);
    }

    @Override
    public void changed(final ObservableValue<? extends Wrapper<Instruction>> observable, final Wrapper<Instruction> oldValue,
                        final Wrapper<Instruction> newValue) {
        if (newValue != null) {
            tab.display(newValue.getValue().getRobotID(), newValue.getValue().getLineNumber());
            debugPane.getVariablepane().refresh();
        }
    }

    public void setDebugPane(DebugPane debugPane) {
        this.debugPane = debugPane;
    }

    private class DummyInstruction implements Instruction {

        private final RobotID robot;
        private final int size;

        public DummyInstruction(final RobotID robot, final int size) {
            this.robot = robot;
            this.size = size;
        }

        @Override
        public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
            return null;
        }

        @Override
        public Collection<Processable> getChildren() {
            return null;
        }

        @Override
        public void close() throws Exception {
        }

        @Override
        public int getLineNumber() {
            return -1;
        }

        @Override
        public RobotID getRobotID() {
            return robot;
        }

        @Override
        public String toString() {
            return size + " more entries...";
        }


    }

    private Debugger getDebugger() {
        return tab.getProcessor().getDebugger();
    }

    private RobotID getRobotID() {
        return tab.getProcessor().getRobotID();
    }


    public static class Wrapper<T> {
        private final T value;

        public Wrapper(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public T getValue() {
            return value;
        }
    }
}
