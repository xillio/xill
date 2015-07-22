package nl.xillio.migrationtool.documentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.construct.Construct;

/**
 * A class which listens to plugins and tries to extract helpfiles.
 *
 * @author Ivor
 *
 */
public class PluginListener {
	private static final Logger log = Logger.getLogger(PluginListener.class);
	/**
	 * The folder in which the generated documentation files reside
	 */
	public static final File HELP_FOLDER = new File("helpfiles");

	/**
	 * A method which listens to all the loaded plugins.
	 *
	 * @param pluginLoader
	 *        The loader that tries to load the plugins from jars
	 * @return the created {@link PluginListener}
	 */
	public static PluginListener attach(final PluginLoader<PluginPackage> pluginLoader) {
		PluginListener listener = new PluginListener();

		// Listen to all loaded plugins
		pluginLoader.getPluginManager().onPluginAccepted().addListener(listener::pluginLoaded);

		return listener;
	}

	private final DocumentSearcher searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
	private final EventHost<URL> onDeployedFiles = new EventHost<>();
	private final FunctionIndex packages = new FunctionIndex("index");

	private final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), ae -> setupIndex()));

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

	/**
	 * Forcefully generate the index
	 */
	public void forceGenerateIndex() {
		timeline.stop();
		timeline.getKeyFrames().get(0).getOnFinished().handle(null);
	}

	/**
	 * An event that is called when the loader is done deploying files. The argument is the resource that indicates the home page.
	 *
	 * @return the onDeployedFiles
	 */
	public Event<URL> getOnDeployedFiles() {
		return onDeployedFiles.getEvent();
	}

	/**
	 * Listens to a pluginPackage and extracts its xml-files.
	 *
	 * @param plugin
	 *        The plugin that we load
	 */
	public void pluginLoaded(final PluginPackage plugin) {
		timeline.play();

		plugin.getName();

		XMLparser parser = new XMLparser();
		DocumentSearcher searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
		PackageDocument thisPackage = new PackageDocument();
		thisPackage.setName(plugin.getName());

		for (Construct construct : plugin.getConstructs()) {
			// If the version of the allready indexed function different
			// from the version of the package
			// or the function is non-existant in the database, we parse the
			// new xml and generate html.
			if (construct.hideDocumentation() || !needsUpdate(plugin, construct)) {
				continue;
			}
			InputStream stream = null;

			// Get the help component
			stream = construct.openDocumentationStream();

			if (stream == null) {
				log.warn("No documentation file found for " + plugin.getName() + "." + construct.getName());
				continue;
			}

			FunctionDocument docu;
			try {
				// parse the xml and default the name of the functiondocument to the name of the construct
				docu = parser.parseXML(stream, plugin.getName(), plugin.getVersion());
				thisPackage.addDescriptiveLink(docu);
				docu.setName(construct.getName());
				
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
					log.debug("Generated html documentation for " + plugin.getName() + "." + construct.getName());
				} else {
					log.error("Invalid name found for the package or a function in the package.");
				}

				stream.close();
			} catch (IOException e) {
				log.error("Failed to load:" + construct.getName());
				e.printStackTrace();
			}

		}
		packages.addPackageDocument(thisPackage);
	}

	/**
	 * <p>
	 * We check wheter we need to update a helpfile of a construct in a plugin through comparing the version of the construct in the database and comparing it to the version of the plugin and through
	 * checking wheter a helpfile exists.
	 * <p>
	 *
	 * @param plugin
	 *        The {@link PluginPackage} of the plugin.
	 * @param construct
	 *        The {@link Construct} which version we're checking.
	 * @return
	 * 				A boolean value wheter we need to update the helpfile of the construct.
	 */
	private boolean needsUpdate(final PluginPackage plugin, final Construct construct) {
		// Check if version is changed
		String name = construct.getName();
		String version = searcher.getDocumentVersion(plugin.getName(), name);
		if (version == null) {
			return true;
		}
		File f = new File(HELP_FOLDER, plugin.getName() + "/" + construct.getName() + ".html");
		if (!f.exists() || f.isDirectory()) {
			return true;
		}
		return !plugin.getVersion().equals(version);
	}

	/**
	 * This function asks the {@link FunctionIndex} to build the html for all the packages and build its own html.
	 */
	private void setupIndex() {
		packages.buildPackageHTML(HELP_FOLDER);
		getClass().getClassLoader();
		try {
			onDeployedFiles.invoke(new File(HELP_FOLDER, "index.html").toURI().toURL());
		} catch (MalformedURLException e) {
			log.error(e);
		}
	}

}
