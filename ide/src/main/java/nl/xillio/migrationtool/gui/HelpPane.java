package nl.xillio.migrationtool.gui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import nl.xillio.migrationtool.Loader;
import nl.xillio.xill.util.HighlightSettings;

/**
 * This pane contains the documentation information.
 *
 * @author Thomas Biesaart
 */
public class HelpPane extends AnchorPane {
	@FXML
	private WebView webFunctionDoc;

	@FXML
	private HelpSearchBar helpSearchBar;

	// the logger
	private static final Logger LOGGER = LogManager.getLogger();

	private final HighlightSettings highlightSettings;

	/**
	 * Instantiate the HelpPane and load the home page.
	 */
	public HelpPane() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelpPane.fxml"));
		loader.setClassLoader(getClass().getClassLoader());
		loader.setController(this);

		try {
			Node ui = loader.load();
			getChildren().add(ui);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		helpSearchBar.setHelpPane(this);

		Loader.getInitializer().getOnLoadComplete().addListener(init -> {
			displayHome();
			webFunctionDoc.getEngine().getHistory().setMaxSize(0);
			webFunctionDoc.getEngine().getHistory().setMaxSize(100);
			helpSearchBar.setSearcher(init.getSearcher());
		});

		// Fill the highlight settings with keywords and builtins
		highlightSettings = new HighlightSettings();
		highlightSettings.addKeywords(Loader.getXill().getReservedKeywords());
		highlightSettings.addBuiltins(Loader.getInitializer().getPlugins().stream()
			.map(p -> p.getName())
			.collect(Collectors.toList()));

		webFunctionDoc.getEngine().documentProperty().addListener((observable, o, n) -> {
			// Set the highlight settings
			JSObject window = (JSObject) webFunctionDoc.getEngine().executeScript("window");
			window.setMember("highlightSettings", highlightSettings);
			// Load the Ace editors if present
			webFunctionDoc.getEngine().executeScript("if (typeof loadEditors !== 'undefined') {loadEditors()}");

		});

		heightProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			helpSearchBar.handleHeightChange();
		});

		// Load splash page
		webFunctionDoc.getEngine().load(getClass().getResource("/docgen/resources/splash.html").toExternalForm());

		// Disable drag-and-drop, set the cursor graphic when dragging.
		webFunctionDoc.setOnDragDropped(null);
		webFunctionDoc.setOnDragOver(e -> getScene().setCursor(Cursor.DISAPPEAR));
	}

	/**
	 * Displays the home page
	 */
	public void displayHome() {
		try {
			this.display(new File("helpfiles", "index.html").toURI().toURL());
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Display the page corresponding to a keyword.
	 *
	 * @param pluginPackage
	 *        The package the function we want to display comes from
	 * @param keyword
	 *        The name of the function in the package
	 */
	public void display(final String pluginPackage, final String keyword) {
		File file = new File("helpfiles/" + pluginPackage + "/" + keyword + ".html");

		Platform.runLater(() -> webFunctionDoc.getEngine().load(file.toURI().toString()));
	}

	/**
	 * Load the passed resource.
	 *
	 * @param resource
	 *        the resource to display
	 */
	public void display(final URL resource) {
		Platform.runLater(() -> webFunctionDoc.getEngine().load(resource.toExternalForm()));
	}

	private void back() {
		webFunctionDoc.getEngine().executeScript("history.back()");
	}

	private void forward() {
		webFunctionDoc.getEngine().executeScript("history.forward()");
	}

	@FXML
	private void buttonHelpHome() {
		displayHome();
	}

	@FXML
	private void buttonHelpBack() {
		back();
	}

	@FXML
	private void buttonHelpForward() {
		forward();
	}

	@FXML
	private void buttonHelpInfo() {}

	@Override
	public void requestFocus() {
		webFunctionDoc.requestFocus();
	}
}
