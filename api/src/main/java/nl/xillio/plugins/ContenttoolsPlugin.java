package nl.xillio.plugins;

import javafx.stage.Stage;
import nl.xillio.plugins.interfaces.Loadable;
import nl.xillio.xill.api.Xill;

/**
 * This interface represents the interface to the top-level Xill IDE plugins.
 */
public interface ContenttoolsPlugin extends Loadable<ContenttoolsPlugin> {
    /**
     * Start the plugin
     *
     * @param stage The main stage of the UI
     * @param xill  The Xill entrypoint
     */
    void start(Stage stage, Xill xill);

    /**
     * Every plugin has a chance to server an object to the host
     *
     * @return The object to serve
     */
    Object serve();
}
