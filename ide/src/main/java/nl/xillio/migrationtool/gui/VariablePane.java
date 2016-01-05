package nl.xillio.migrationtool.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.Instruction;
import nl.xillio.xill.api.components.MetaExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * This class represents a list of variables and their names
 */
public class VariablePane extends AnchorPane implements RobotTabComponent, ListChangeListener<ObservableVariable> {
    private final ObservableList<ObservableVariable> observableStateList = FXCollections.observableArrayList();
    private ObservableVariable selectedItem = null;

    private static final Logger LOGGER = LogManager.getLogger(VariablePane.class);

    @FXML
    private TableView<ObservableVariable> tblVariables;

    @FXML
    private TableColumn<ObservableVariable, String> colVariableName;
    @FXML
    private TableColumn<ObservableVariable, String> colVariableType;
    @FXML
    private TableColumn<ObservableVariable, String> colVariableValue;
    @FXML
    private TableColumn<ObservableVariable, Boolean> colVariableGlobal;

    private RobotTab tab;

    private PreviewPane previewpane;
    private InstructionStackPane stackPane;

    /**
     * Create a new {@link VariablePane}
     */
    public VariablePane() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VariablePane.fxml"));
            loader.setClassLoader(getClass().getClassLoader());
            loader.setController(this);
            Node ui = loader.load();
            getChildren().add(ui);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        tblVariables.setItems(observableStateList);

        // Remove the default empty table text
        tblVariables.setPlaceholder(new Label(""));

        colVariableName.setCellValueFactory(new PropertyValueFactory<ObservableVariable, String>("name"));
        colVariableValue.setCellValueFactory(new PropertyValueFactory<ObservableVariable, String>("value"));

        tblVariables.getSelectionModel().getSelectedItems().addListener(this);
    }

    /**
     * @return the table view
     */
    public TableView<ObservableVariable> getTableView() {
        return tblVariables;
    }


    /**
     * Refresh the variable pane.
     */
    public synchronized void refresh() {
        clear();

        InstructionStackPane.Wrapper<Instruction> wrapper = stackPane.getInstructionBox().getValue();

        if (wrapper == null) {
            return;
        }

        int selected = stackPane.getInstructionBox().getSelectionModel().getSelectedIndex();

        if (selected == -1) {
            return;
        }

        getDebugger().getVariables(wrapper.getValue()).forEach(var -> {
            String name = getDebugger().getVariableName(var);

            MetaExpression value = getDebugger().getVariableValue(var, stackPane.getInstructionBox().getItems().size() - selected);
            ObservableVariable observable = new ObservableVariable(name, value, var);
            observableStateList.add(observable);

            // Re-select an item if it was selected before
            if (selectedItem != null && selectedItem.getName().equals(name)) {
                tblVariables.getSelectionModel().select(observable);
            }
        });
    }

    private void clear() {
        observableStateList.clear();

    }

    @Override
    public void initialize(final RobotTab tab) {
        this.tab = tab;
        getDebugger().getOnRobotPause().addListener(e -> refresh());
        getDebugger().getOnRobotStop().addListener(e -> clear());
    }

    public void initialize(final InstructionStackPane pane) {
        this.stackPane = pane;
    }

    /**
     * Set the preview pane
     *
     * @param previewpane
     */
    public void setPreviewPane(final PreviewPane previewpane) {
        this.previewpane = previewpane;

    }

    public Debugger getDebugger() {
        return this.tab.getProcessor().getDebugger();
    }

    @Override
    public void onChanged(final javafx.collections.ListChangeListener.Change<? extends ObservableVariable> change) {
        Platform.runLater(() -> {
            List<? extends ObservableVariable> selected = change.getList();
            ObservableVariable observableVariable = getTableView().getSelectionModel().getSelectedItem();
            if (!selected.isEmpty()) {
                selectedItem = selected.get(0);
            }
            if (selectedItem != null && observableVariable != null) {
                previewpane.preview(observableVariable);
            }

        });
    }
}
