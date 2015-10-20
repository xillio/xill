package nl.xillio.migrationtool.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import nl.xillio.migrationtool.gui.searching.SearchTextArea;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.preview.PreviewComponent;
import nl.xillio.xill.api.preview.Searchable;

/**
 * This pane can show a visual representation of the contenttools Variable
 * classes.
 *
 */
public class PreviewPane extends AnchorPane implements RobotTabComponent {
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

		Node node = getPreview(value, apnPreviewSearchBar);

		if (node == null) {
			node = buildTree(value);
		}

		if (node instanceof Text) {
			textView.setText(((Text) node).getText());
			apnPreviewPane.getChildren().add(textView);
		} else {
			apnPreviewPane.getChildren().add(node);
		}
	}

	private void clear() {
		Platform.runLater(() -> {
			apnPreviewPane.getChildren().clear();
		});
	}

	private static Node getPreview(final MetaExpression expression, final SearchBar searchBar) {

		// First allow the expression to provide a preview
		if (expression instanceof PreviewComponent) {
			PreviewComponent preview = (PreviewComponent) expression;

			if (searchBar != null && preview instanceof Searchable) {
				searchBar.setSearchable((Searchable) preview);
			}

			return preview.getPreview();
		}

		if (expression.getType() == ExpressionDataType.ATOMIC) {
			Text preview = new Text(expression.getStringValue());
			Tooltip tooltip = new Tooltip(WordUtils.wrap(expression.getStringValue(), 200, "\n", true));
			tooltip.setWrapText(true);

			Tooltip.install(preview, tooltip);
			return preview;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static TreeTableView<Pair<String, Node>> buildTree(final MetaExpression rootValue) {

		// Create the root node
		TreeItem<Pair<String, Node>> root = buildItem("ROOT", rootValue);

		// Create columns
		TreeTableColumn<Pair<String, Node>, String> keyColumn = new TreeTableColumn<>("Key");
		keyColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getKey()));
		keyColumn.setPrefWidth(200);

		TreeTableColumn<Pair<String, Node>, Node> valueColumn = new TreeTableColumn<>("Value");
		valueColumn.setCellValueFactory(data -> new SimpleObjectProperty<Node>(data.getValue().getValue().getValue()));
		valueColumn.setPrefWidth(600);

		// Build the tree
		TreeTableView<Pair<String, Node>> tableView = new TreeTableView<>(root);
		tableView.getColumns().setAll(keyColumn, valueColumn);
		tableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setShowRoot(false);

		AnchorPane.setBottomAnchor(tableView, 0.0);
		AnchorPane.setTopAnchor(tableView, 0.0);
		AnchorPane.setRightAnchor(tableView, 0.0);
		AnchorPane.setLeftAnchor(tableView, 0.0);

		return tableView;
	}

	@SuppressWarnings("unchecked")
	private static TreeItem<Pair<String, Node>> buildItem(final String key, final MetaExpression value) {
		// First we get the node
		Node node = getPreview(value, null);

		if (node == null) {
			switch (value.getType()) {
				case LIST:
					node = new Text("LIST [" + ((List<?>) value.getValue()).size() + "]");
					break;
				case OBJECT:
					node = new Text("OBJECT [" + ((Map<?, ?>) value.getValue()).size() + "]");
					break;
				default:
					throw new IllegalArgumentException("Cannot parse an ATOMIC value to a tree structure.");
			}
		}

		// Then we make the treeitem
		TreeItem<Pair<String, Node>> currentItem = new TreeItem<>(new Pair<>(key, node));

		// Add the children
		switch (value.getType()) {
			case LIST:
				Iterator<MetaExpression> children = ((List<MetaExpression>) value.getValue()).iterator();
				int i = 0;
				while (children.hasNext() && i < 10000) {
					MetaExpression child = children.next();
					currentItem.getChildren().add(buildItem(Integer.toString(i++), child));
				}
				if (i >= 10000) {
					currentItem.getChildren().add(
						new TreeItem<>(new Pair<>(i + "+", new Text("The preview is limited to " + i + " elements"))));
				}
				break;
			case OBJECT:
				currentItem.getChildren().addAll(((Map<String, MetaExpression>) value.getValue()).entrySet().stream()
					.map(entry -> buildItem(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
				break;
			default:
				break;
		}

		return currentItem;
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
		debugger.getOnRobotStop().addListener(action -> clear());
	}
}
