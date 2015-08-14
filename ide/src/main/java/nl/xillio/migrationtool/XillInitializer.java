package nl.xillio.migrationtool;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.docgen.DocGen;
import nl.xillio.xill.docgen.DocumentationEntity;
import nl.xillio.xill.docgen.DocumentationGenerator;
import nl.xillio.xill.docgen.DocumentationParser;
import nl.xillio.xill.docgen.exceptions.ParsingException;
import nl.xillio.xill.docgen.impl.XillDocGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Module;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.services.inject.InjectorUtils;
import nl.xillio.xill.services.inject.PluginInjectorModule;

/**
 * This {@link Thread} is responsible for loading the plugins an and initializing the language
 */
public class XillInitializer extends Thread {
	private final DocGen docGen;
	private DocumentationParser parser;
	private static final Logger log = LogManager.getLogger();
	private static final File PLUGIN_FOLDER = new File("plugins");
	private PluginLoader<XillPlugin> pluginLoader;
	private final EventHost<URL> onLoadComplete = new EventHost<>();

	public XillInitializer() {
		docGen = new XillDocGen(null);
	}

	@Override
	public void run() {
		parser = getParser();

		log.info("Loading Xill language plugins");

		// Deploy documentation system static files
		deployResources();

		// Initialize the loader
		initializeLoader();

		// Load
		loadPlugins();

		// We are done loading now set up the injector
		initializeInjector();

		// Load the constructs
		initializePlugins();

		// Now we generate documentation
		generateDocumentation();

		// And finally generate the index
		generateIndex();

		log.info("Done loading plugins");
		/*try {
			onLoadComplete.invoke(new File(DocumentationGenerator.HELP_FOLDER, "index.html").toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}*/

	}

	private void generateIndex() {
		try {
			docGen.generateIndex();
		} catch (ParsingException e) {
			log.error("Failed to generate index", e);
		}
	}

	/**
	 * Deploy the static resources for the documentation system
	 */
	void deployResources() {

	}

	DocumentationParser getParser() {
		try {
			return docGen.getParser();
		} catch (ParsingException e) {
			throw new RuntimeException("Failed get parser", e);
		}
	}

	private void initializeInjector() {
		Module module = new PluginInjectorModule(getPlugins());
		InjectorUtils.initialize(module);
	}

	private void initializePlugins() {
		for (XillPlugin plugin : getPlugins()) {
			try {
				plugin.initialize();	
			} catch (Exception e) {
				log.error("Exception while initializing " + plugin, e);
			}
		}
	}

	private void generateDocumentation() {
		getPlugins().forEach(this::generateDocumentation);
	}

	private void generateDocumentation(XillPlugin plugin) {
		try(DocumentationGenerator generator = docGen.getGenerator(plugin.getName())) {
			plugin.getConstructs().forEach(construct -> generateDocumentation(construct, generator));
		}catch(Exception e) {
			throw new RuntimeException("Failed to generate documentation for " + plugin.getClass().getName(), e);
		}
	}

	private void generateDocumentation(Construct construct, DocumentationGenerator generator) {
		URL url = construct.getDocumentationResource();

		if(url == null) {
			return;
		}

		try {
			DocumentationEntity entity = parser.parse(url, construct.getName());
			generator.generate(entity);
		} catch (ParsingException e) {
			log.error("Failed to generate documentation from " + url, e);
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
