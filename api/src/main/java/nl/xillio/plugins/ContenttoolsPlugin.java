package nl.xillio.plugins;

import javafx.stage.Stage;
import nl.xillio.xill.api.XillEnvironment;

/**
 * This interface represents the interface to the top-level Xill IDE plugins.
 */
public interface ContenttoolsPlugin {
    /**
     * Starts the plugin.
     *
     * @param stage the main stage of the UI
     * @param xill  the Xill entry point
     */
    void start(Stage stage, XillEnvironment xill);
}
