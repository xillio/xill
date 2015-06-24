package nl.xillio.contenttools;

import java.io.File;
import java.util.List;

import javafx.stage.Stage;
import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.Xill;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This is the main class of the contenttools application
 */
public class Application extends javafx.application.Application{
	private static List<PluginPackage> plugins;
	private static final Logger logger = LogManager.getLogger(Application.class);

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String... args) {
		PluginLoader<PluginPackage> loader = PluginLoader.load(PluginPackage.class);
		
		File pluginFolder = new File("plugins");
		pluginFolder.mkdirs();
		loader.addFolder(pluginFolder);
		
		
		try {
			loader.load();
		} catch (CircularReferenceException e) {
			throw new RuntimeException("Error while loading plugins.", e);
		}
		
		logger.info("Loaded " + loader.getPluginManager().getPlugins().size() + " plugins");
		
		plugins = loader.getPluginManager().getPlugins();
		
		if(plugins.size() < 2) {
			//throw new RuntimeException("Could not find the Xill and Contenttools plugins.");
		}
		
		launch(args);
		
	}

	private Xill xill;

	@Override
	public void start(Stage stage) throws Exception {
		
		//Find served objects
		plugins.forEach(plugin -> {
			Object served = plugin.serve();
			
			if(served instanceof Xill) {
				xill = (Xill)served;
			}
		});
		
		//Boot plugins
		plugins.forEach(plugin -> { 
			plugin.start(stage, xill);
		});
		

		setUserAgentStylesheet(STYLESHEET_MODENA);
	}
}
