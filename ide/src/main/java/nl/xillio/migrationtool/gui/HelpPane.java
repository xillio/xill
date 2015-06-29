package nl.xillio.migrationtool.gui;

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
	 *
	 * @param keyword
	 */
	public void display(final String keyword) {
		URL url = getClass().getResource("/help/" + keyword + ".html");
		if (url != null) {
			Platform.runLater(() -> {
				webFunctionDoc.getEngine().load(url.toString());
			});
		}
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
