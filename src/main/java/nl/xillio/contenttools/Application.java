package nl.xillio.contenttools;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.stage.Stage;
import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.ContenttoolsPlugin;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.Xill;

/**
 * This is the main class of the contenttools application
 */
public class Application extends javafx.application.Application {
	private static List<ContenttoolsPlugin> plugins;
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Main method
	 *
	 * @param args
	 *        the main arguments to run the program
	 */
	public static void main(final String... args) {
		logger.info("Starting host application");
		PluginLoader<ContenttoolsPlugin> loader = PluginLoader.load(ContenttoolsPlugin.class);

		File pluginFolder = new File("plugins");
		pluginFolder.mkdirs();
		loader.addFolder(pluginFolder);

		logger.info("Loading ide and xill");
		try {
			loader.load();
		} catch (CircularReferenceException e) {
			throw new RuntimeException("Error while loading plugins.", e);
		}

		plugins = loader.getPluginManager().getPlugins();

		if (plugins.size() < 2) {
			throw new RuntimeException("Could not find the Xill and Contenttools plugins.");
		}

		launch(args);

	}

	private Xill xill;

	@Override
	public void start(final Stage stage) throws Exception {

		// Find served objects
		plugins.forEach(plugin -> {
			Object served = plugin.serve();

			if (served instanceof Xill) {
				xill = (Xill) served;
			}
		});

		// Boot plugins
		plugins.forEach(plugin -> {
			logger.info("Starting " + plugin.getClass().getPackage().getImplementationTitle());
			plugin.start(stage, xill);
		});

		setUserAgentStylesheet(STYLESHEET_MODENA);
	}
}
