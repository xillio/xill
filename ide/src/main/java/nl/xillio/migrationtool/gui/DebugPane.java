package nl.xillio.migrationtool.gui;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import nl.xillio.sharedlibrary.settings.SettingsHandler;

/**
 * A collapsible debug pane. Contains the variable and the preview panes.
 */
public class DebugPane extends AnchorPane implements EventHandler<KeyEvent>, RobotTabComponent {
	private static final SettingsHandler settings = SettingsHandler.getSettingsHandler();

	@FXML
	private VariablePane variablepane;
	@FXML
	private PreviewPane previewpane;
	@FXML
	private SplitPane spnBotRight;
	@FXML
	private InstructionStackPane instructionstackpane;

	/**
	 * Default constructor.
	 */
	public DebugPane() {
		super();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DebugPane.fxml"));
			loader.setClassLoader(getClass().getClassLoader());
			loader.setController(this);
			Node ui = loader.load();
			getChildren().add(ui);

		} catch (IOException e) {
			e.printStackTrace();
		}

		variablepane.setPreviewPane(previewpane);

		this.addEventHandler(KeyEvent.KEY_PRESSED, this);
	}

	@Override
	public void handle(final KeyEvent event) {
		if (KeyCombination.valueOf(FXController.HOTKEY_FIND).match(event)) {
			previewpane.openSearch();
		}
	}

	@Override
	public void initialize(final RobotTab tab) {
		String fullPath = tab.getProcessor().getRobotID().getPath().getAbsolutePath();
		settings.registerSimpleSetting("Layout", "PreviewHeight_" + fullPath, "0.6", "The height of the preview panel");
		// Load the divider position
		spnBotRight.setDividerPosition(0, Double.parseDouble(settings.getSimpleSetting("PreviewHeight_" + fullPath)));
		spnBotRight.getDividers().get(0).positionProperty().addListener((observable, prevPos, newPos) -> settings.saveSimpleSetting("PreviewHeight_" + fullPath, Double.toString(newPos.doubleValue())));
		
		initializeChildren(tab);
	}
	
	/**
	 * Initialize graphical FX items that belongs to DebugPane (it's because of problem with Tab.getContent() on Linux, see CTC-713)  
	 * @param tab currently active RobotTab
	 */
	private void initializeChildren(final RobotTab tab) {
		variablepane.initialize(tab);
		previewpane.initialize(tab);
		instructionstackpane.initialize(tab);
	}

}
