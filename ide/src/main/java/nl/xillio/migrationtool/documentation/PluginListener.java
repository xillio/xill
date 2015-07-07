package nl.xillio.migrationtool.documentation;

import java.io.File;
import java.io.IOException;

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
 * @author Ivor
 *
 */
public class PluginListener {
    private DocumentSearcher searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
    private static final Logger log = Logger.getLogger(PluginListener.class);
    /**
     * @param plugin The plugin that we load
     * 
     */
    public void pluginLoaded(final PluginPackage plugin) {
	plugin.getName();
	XMLparser parser = new XMLparser();
	
	
	for (Construct construct : plugin.getConstructs()) {
	    if (construct instanceof HelpComponent) {
			HelpComponent documentedConstruct = (HelpComponent) construct;
			
			//If the version of the allready indexed function different from the version of the package
			//or the function is non-existant in the database, we 
			if(needsUpdate(construct, plugin.getVersion()))
			{
				try {
					FunctionDocument docu = parser.parseXML(documentedConstruct.openDocumentationStream(), plugin.getVersion());
					FileUtils.write(new File("./helpfiles/" + plugin.getName() + "/" + docu.getName() + ".html"), docu.toHTML());
					log.error("I had an error");
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	}
    }
    
    private boolean needsUpdate(Construct construct, String pluginVersion) {
	String version = searcher.getDocumentVersionById(construct.getName());
	if(version == null) {
	    return false;
	}
	
	return pluginVersion.equals(version);
    }

    /**
     * A method which listens to all the loaded plugins.
     * @param pluginLoader The loader that tries to load the plugins from jars
     */
    public static void Attach(final PluginLoader<PluginPackage> pluginLoader) {
	PluginListener listener = new PluginListener();

	// Listen to all loaded plugins
	pluginLoader.getPluginManager().onPluginAccepted().addListener(listener::pluginLoaded);
    }
}
