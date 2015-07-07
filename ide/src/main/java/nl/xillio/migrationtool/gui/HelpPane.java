package nl.xillio.migrationtool.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

/**
 * This pane contains the documentation information
 */
public class HelpPane extends AnchorPane {
	@FXML
	private WebView webFunctionDoc;

	/**
	 * Instantiate the HelpPane and load the home page
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
		HelpSearchBar helper = new HelpSearchBar();
		helper.setHelpPane(this);
		
		this.getChildren().add(helper);

		home();
	}

	private void home() {
		Platform.runLater(() -> {
			webFunctionDoc.getEngine().load(getClass().getResource("/help/index.html").toString());
		});
	}

	/**
	 * Display the page corresponding to a keyword
	 * @param pluginPackage 
	 * 			The package the function we want to display comes from
	 * @param keyword
	 * 			The name of the function in the package
	 */
	public void display(final String pluginPackage, final String keyword) {
		File file = new File("helpfiles/" + pluginPackage + "/" + keyword + ".html");
		URL url = getClass().getResource("/help/" + keyword + ".html");
		System.out.println(url.toString());
		System.out.println(file.getAbsolutePath());
		if (url != null) {
			Platform.runLater(() -> {
				webFunctionDoc.getEngine().load(file.toURI().toString());
			});
		}
	}
	
	public void display(final String s)
	{
		
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
		/*
		 * FXEditor editor = (FXEditor) tpnEditor.getSelectionModel().getSelectedItem();
		 * if (editor != null) {
		 * String keyword = editor.getKeywordUnderCursor();
		 * if (!"".matches(keyword)) {
		 * helpController.help(keyword);
		 * }
		 * }
		 */
	}
}
