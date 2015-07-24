package nl.xillio.migrationtool;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Module;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.migrationtool.documentation.DocumentationGenerator;
import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.inject.InjectorUtils;
import nl.xillio.xill.api.inject.PluginInjectorModule;

/**
 * This {@link Thread} is responsible for loading the plugins an and initializing the language
 */
public class XillInitializer extends Thread {
	private static final Logger log = LogManager.getLogger();
	private final DocumentationGenerator generator = new DocumentationGenerator();
	private static final File PLUGIN_FOLDER = new File("plugins");
	private PluginLoader<XillPlugin> pluginLoader;
	private final EventHost<URL> onLoadComplete = new EventHost<>();

	@Override
	public void run() {
		log.info("Loading Xill language plugins");

		// Deploy documentation system static files
		generator.deployFiles();

		// Initialize the loader
		initializeLoader();

		// Load
		loadPlugins();

		// We are done loading now set up the injector
		initializeInjector();
		
		// Load the constructs
		initializePlugins();

		//Now we generate documentation
		generateDocumentation();
		generator.forceGenerateIndex();

		log.info("Done loading plugins");
		try {
			onLoadComplete.invoke(new File(DocumentationGenerator.HELP_FOLDER, "index.html").toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	private void initializeInjector() {
		Module module = new PluginInjectorModule(getPlugins());
		InjectorUtils.initialize(module);
	}

	private void initializePlugins() {
		for(XillPlugin plugin : getPlugins()) {
			plugin.initialize();
		}
	}

	private void generateDocumentation() {
		for(XillPlugin plugin : getPlugins()) {
			generator.generate(plugin);
		}
	}

	private void initializeLoader() {
		pluginLoader = PluginLoader.load(XillPlugin.class);
		PLUGIN_FOLDER.mkdirs();
		pluginLoader.addFolder(PLUGIN_FOLDER);
		
		pluginLoader.getPluginManager().onPluginAccepted().addListener(plugin -> {
			log.info("Loaded " + plugin.getClass().getSimpleName());
		});
	}

	private void loadPlugins() {
		try {
			pluginLoader.load();
		} catch (CircularReferenceException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return a list of all loaded plugins
	 */
	public List<XillPlugin> getPlugins() {
		return pluginLoader.getPluginManager().getPlugins();
	}

	/**
	 * @return the last created {@link PluginLoader}
	 */
	public PluginLoader<XillPlugin> getLoader() {
		return pluginLoader;
	}

	/**
	 * This event is called every time the initializer is done loading plugins
	 *
	 * @return the event
	 */
	public Event<URL> getOnLoadComplete() {
		return onLoadComplete.getEvent();
	}
}
