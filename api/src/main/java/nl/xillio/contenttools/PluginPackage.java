package nl.xillio.contenttools;

import javafx.stage.Stage;
import nl.xillio.plugins.interfaces.Loadable;
import nl.xillio.xill.api.Xill;

/**
 * This interface represents the interface to the top-level contenttools plugins.
 */
public interface PluginPackage extends Loadable<PluginPackage> {
	/**
	 * Start the plugin
	 * @param stage The main stage of the UI
	 * @param xill The Xill entrypoint
	 */
	public void start(Stage stage, Xill xill);
	/**
	 * Every plugin has a chance to server an object to the host
	 * @return The object to serve
	 */
	public Object serve();
}
