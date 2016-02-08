package nl.xillio.plugins;

import javafx.stage.Stage;
import nl.xillio.xill.api.XillEnvironment;

/**
 * This interface represents the interface to the top-level Xill IDE plugins.
 */
public interface ContenttoolsPlugin {
    /**
     * Start the plugin
     *
     * @param stage The main stage of the UI
     * @param xill  The Xill entry point
     */
    void start(Stage stage, XillEnvironment xill);
}
