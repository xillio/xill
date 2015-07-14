package nl.xillio.migrationtool.documentation;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.HelpComponent;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * A class which listens to plugins and tries to extract helpfiles.
 *
 * @author Ivor
 *
 */
public class PluginListener {
	private final DocumentSearcher searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
	private static final Logger log = Logger.getLogger(PluginListener.class);
	private final EventHost<URL> onDeployedFiles = new EventHost<>();
	private static final File HELP_FOLDER = new File("helpfiles");

	/**
	 * Listens to a pluginPackage and extracts its xml-files.
	 *
	 * @param plugin
	 *        The plugin that we load
	 *
	 */
	public void pluginLoaded(final PluginPackage plugin) {
		plugin.getName();

		XMLparser parser = new XMLparser();
		DocumentSearcher searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());

		for (Construct construct : plugin.getConstructs()) {
			if (construct instanceof HelpComponent) {
				HelpComponent documentedConstruct = (HelpComponent) construct;

				// If the version of the allready indexed function different
				// from the version of the package
				// or the function is non-existant in the database, we parse the
				// new xml and generate html.
				if (needsUpdate(plugin, construct)) {
					try {
						// Parse the XML
						FunctionDocument docu = parser.parseXML(documentedConstruct.openDocumentationStream(),
							plugin.getName(), plugin.getVersion());

						// Write an html file
						if (plugin.getName() != null && docu.getName() != null) {
							// We write the HTML file
							FileUtils.write(
								new File(HELP_FOLDER, plugin.getName() + "/" + docu.getName() + ".html"),
								docu.toHTML());
							// We set the version of the document
							docu.setVersion(plugin.getVersion());

							// We index the document
							searcher.index(docu);
							log.info("Generated html documentation for " + plugin.getName() + "." + construct.getName());
						} else {
							log.error("Invalid name found for the package or a function in the package.");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						log.error("Invalid XML file found in the package", e);
					}
				}
			}
		}
		
		searcher.setIndex(HELP_FOLDER);
	}

	/**
	 * Deploy the required files for the documentation system
	 */
	public void deployFiles() {
		// Css
		try {
			FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/helpCss/style.css"), new File(HELP_FOLDER, "style/style.css"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean needsUpdate(final PluginPackage plugin, final Construct construct) {
		// Check if version is changed
		String name = construct.getName();
		String version = searcher.getDocumentVersion(plugin.getName(), name);
		if (version == null) {
			return true;
		}
		return !plugin.getVersion().equals(version);
	}

	/**
	 * A method which listens to all the loaded plugins.
	 *
	 * @param pluginLoader
	 *        The loader that tries to load the plugins from jars
	 * @return the created {@link PluginListener}
	 */
	public static PluginListener Attach(final PluginLoader<PluginPackage> pluginLoader) {
		PluginListener listener = new PluginListener();

		// Listen to all loaded plugins
		pluginLoader.getPluginManager().onPluginAccepted().addListener(listener::pluginLoaded);

		return listener;
	}

	/**
	 * An event that is called when the loader is done deploying files. The argument is the resource that indicates the home page.
	 * 
	 * @return the onDeployedFiles
	 */
	public Event<URL> getOnDeployedFiles() {
		return onDeployedFiles.getEvent();
	}
}
