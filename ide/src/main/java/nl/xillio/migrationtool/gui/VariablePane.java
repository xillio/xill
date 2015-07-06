package nl.xillio.migrationtool.gui;

import java.io.IOException;
import java.util.List;

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
import nl.xillio.xill.api.components.MetaExpression;

/**
 * This class represents a list of variables and their names
 */
public class VariablePane extends AnchorPane implements RobotTabComponent, ListChangeListener<ObservableVariable> {
	private final ObservableList<ObservableVariable> observableStateList = FXCollections.observableArrayList();

	@FXML
	private TableView<ObservableVariable> tblVariables;

	@FXML
	private TableColumn<ObservableVariable, String> colVariableName, colVariableType, colVariableValue;
	@FXML
	private TableColumn<ObservableVariable, Boolean> colVariableGlobal;

	private Debugger debugger;

	private PreviewPane previewpane;

	/**
	 *  Create a new {@link VariablePane}
	 */
	public VariablePane() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VariablePane.fxml"));
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(this);
			Node ui = loader.load();
			getChildren().add(ui);
		} catch (IOException e) {
			e.printStackTrace();
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
		
		debugger.getVariables().forEach(var -> {
			String name = debugger.getVariableName(var);
			MetaExpression value = debugger.getVariableValue(var);
			
			observableStateList.add(new ObservableVariable(name, value, var));
		});
	}
	
	private void clear() {
		observableStateList.clear();
	}

	@Override
	public void initialize(RobotTab tab) { 
		debugger = tab.getProcessor().getDebugger();
		debugger.getOnRobotPause().addListener(e -> refresh());
		debugger.getOnRobotStop().addListener(e -> clear());
	}

	/**
	 * Set the preview pane
	 * @param previewpane
	 */
	public void setPreviewPane(PreviewPane previewpane) {
		this.previewpane = previewpane;
		
	}

	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends ObservableVariable> change) {
		List<? extends ObservableVariable> selected = change.getList();
		
		if(!selected.isEmpty()) {
			previewpane.preview(selected.get(0));
		}
	}
}
