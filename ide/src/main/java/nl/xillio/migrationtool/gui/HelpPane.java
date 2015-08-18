package nl.xillio.migrationtool.gui;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import nl.xillio.migrationtool.Loader;

/**
 * This pane contains the documentation information.
 * @author Thomas Biesaart
 */
public class HelpPane extends AnchorPane {
	@FXML
	private WebView webFunctionDoc;

	@FXML
	private HelpSearchBar helpSearchBar;

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
			e.printStackTrace();
		}
		helpSearchBar.setHelpPane(this);

		Loader.getInitializer().getOnLoadComplete().addListener(init -> {
			home();
			webFunctionDoc.getEngine().getHistory().setMaxSize(0);
			webFunctionDoc.getEngine().getHistory().setMaxSize(100);
			helpSearchBar.setSearcher(init.getSearcher());
		});

		//Load splash page
		webFunctionDoc.getEngine().load(getClass().getResource("/docgen/resources/splash.html").toExternalForm());
	}

	private void home() {
		try {
			this.display(new File("helpfiles", "index.html").toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Display the page corresponding to a keyword.
	 *
	 * @param pluginPackage The package the function we want to display comes from
	 * @param keyword       The name of the function in the package
	 */
	public void display(final String pluginPackage, final String keyword) {
		File file = new File("helpfiles/" + pluginPackage + "/" + keyword + ".html");

		Platform.runLater(() -> webFunctionDoc.getEngine().load(file.toURI().toString()));
	}

	/**
	 * Load the passed resource.
	 *
	 * @param resource the resource to display
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
		home();
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
	private void buttonHelpInfo() {
	}

	@Override
	public void requestFocus() {
		webFunctionDoc.requestFocus();
	}
}
