package nl.xillio.migrationtool.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import nl.xillio.migrationtool.gui.searching.SearchTextArea;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.preview.PreviewComponent;

/**
 * This pane can show a visual representation of the contenttools Variable
 * classes.
 *
 */
public class PreviewPane extends AnchorPane implements RobotTabComponent {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @FXML
    private AnchorPane apnPreviewPane;
    @FXML
    private SearchBar apnPreviewSearchBar;
    @FXML
    private ToggleButton tbnPreviewSearch;
    private Debugger debugger;
    private final SearchTextArea textView = new SearchTextArea();

    /**
     * Create a new PreviewPane
     */
    public PreviewPane() {
	try {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PreviewPane.fxml"));
	    loader.setClassLoader(getClass().getClassLoader());
	    loader.setController(this);
	    getChildren().add(loader.load());
	} catch (IOException e) {
	    e.printStackTrace();
	}

	apnPreviewSearchBar.setSearchable(textView);
	apnPreviewSearchBar.setButton(tbnPreviewSearch, 1);
	
	AnchorPane.setBottomAnchor(textView, 0.0);
	AnchorPane.setTopAnchor(textView, 0.0);
	AnchorPane.setLeftAnchor(textView, 0.0);
	AnchorPane.setRightAnchor(textView, 0.0);
    }

    /**
     * @param observableVariable
     */
    public void preview(final ObservableVariable observableVariable) {
	MetaExpression value = debugger.getVariableValue(observableVariable.getSource());

	apnPreviewPane.getChildren().clear();

	Node node = getPreview(value);
	if (node instanceof Text) {
	    textView.setText(((Text) node).getText());
	    apnPreviewPane.getChildren().add(textView);
	} else {
	    apnPreviewPane.getChildren().add(new HBox(getPreview(value)));
	}
    }

    private boolean renderingPreview = false;

    private Node getPreview(final MetaExpression expression) {

	// First allow the expression to provide a preview
	if (expression instanceof PreviewComponent) {
	    PreviewComponent preview = (PreviewComponent) expression;

	    // Only set searchable on the root node
	    if (!renderingPreview) {
		apnPreviewSearchBar.setSearchable(preview);
	    }
	    return preview.getPreview();
	}

	renderingPreview = true;
	Node result = null;
	switch (expression.getType()) {
	case LIST:
	    // TODO Listview implementation
	    result = new Text(gson.toJson(gson.fromJson(expression.toString(), List.class)));
	    break;
	case OBJECT:
	    // TODO Browsable Object (Treeview) implementation
	    result = new Text(gson.toJson(gson.fromJson(expression.toString(), LinkedHashMap.class)));
	    break;
	case ATOMIC:
	    result = new Text(expression.toString());
	    break;
	}

	renderingPreview = false;
	return result;
    }

    /**
     * Open the search bar
     */
    public void openSearch() {
	apnPreviewSearchBar.open(1);
	apnPreviewSearchBar.requestFocus();
    }

    @Override
    public void initialize(final RobotTab tab) {
	debugger = tab.getProcessor().getDebugger();
    }

}
