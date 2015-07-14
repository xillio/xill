package nl.xillio.migrationtool.documentation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * A class which listens to plugins and tries to extract helpfiles.
 *
 * @author Ivor
 *
 */
public class PluginListener {
    private final DocumentSearcher searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
    private static final Logger log = Logger.getLogger(PluginListener.class);

    /**
     * Listens to a pluginPackage and extracts its xml-files.
     *
     * @param plugin
     *            The plugin that we load
     *
     */
    public void pluginLoaded(final PluginPackage plugin) {
	plugin.getName();
	List<FunctionDocument> functions = new ArrayList<>();
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
				    new File("./helpfiles/" + plugin.getName() + "/" + docu.getName() + ".html"),
				    docu.toHTML());
			    // We add the document to the plugin (package)
			    docu.setVersion(plugin.getVersion());
			    functions.add(docu);

			    // We index the document
			    searcher.index(docu);
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
	    try {
		// Generate the html file for the package
		FileUtils.write(new File("./helpfiles/packages/" + plugin.getName() + ".html"),
			packageDocumentation(plugin.getName(), functions).toHTML());
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * @param packageName
     *            The name of the packages
     * @param functions
     *            The functions the package contains
     * @return A functiondocument that represents the package
     */
    private static HtmlGenerator packageDocumentation(final String packageName,
	    final List<FunctionDocument> functions) {
	PackageDocument docu = new PackageDocument();
	docu.setName(packageName);
	for (FunctionDocument func : functions) {
	    docu.addDescriptiveLink(func);
	}

	return docu;
    }

    private boolean needsUpdate(final PluginPackage plugin, final Construct construct) {
	String version = searcher.getDocumentVersion(plugin.getName(), construct.getName());
	if (version == null) {
	    return true;
	}

	return !plugin.getVersion().equals(version);
    }

    /**
     * A method which listens to all the loaded plugins.
     *
     * @param pluginLoader
     *            The loader that tries to load the plugins from jars
     */
    public static void Attach(final PluginLoader<PluginPackage> pluginLoader) {
	PluginListener listener = new PluginListener();

	// Listen to all loaded plugins
	pluginLoader.getPluginManager().onPluginAccepted().addListener(listener::pluginLoaded);
    }
}
