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
	List<String> functions = new ArrayList<>();
	XMLparser parser = new XMLparser();
	DocumentSearcher searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
	
	
	for (Construct construct : plugin.getConstructs()) {
	    if (construct instanceof HelpComponent) {
			HelpComponent documentedConstruct = (HelpComponent) construct;
			
			//If the version of the allready indexed function different from the version of the package
			//or the function is non-existant in the database, we parse the new xml and generate html.
			if(needsUpdate(plugin, construct))
			{
				try {
					FunctionDocument docu = parser.parseXML(documentedConstruct.openDocumentationStream(), plugin.getName(), plugin.getVersion());
					
					if(plugin.getName() != null && docu.getName() != null){
						FileUtils.write(new File("./helpfiles/" + plugin.getName() + "/" + docu.getName() + ".html"), docu.toHTML());
						functions.add(docu.getName());
						searcher.index(docu);
					}
					else
						log.error("Please enter valid names for your package and functions");
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println(construct.getName());
					e.printStackTrace();
				}
			}
	    }
		 try {
			FileUtils.write(new File("./helpfiles/packages/" + plugin.getName() + ".html"), this.packageDocumentation(plugin.getName(), functions).toPackageHTML());
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
    }
    
    private boolean needsUpdate(PluginPackage plugin, Construct construct) {
	String version = searcher.getDocumentVersion(plugin.getName(), construct.getName());
	if(version == null) {
	    return true;
	}
	
	return !plugin.getVersion().equals(version);
    }
    
    private FunctionDocument packageDocumentation(String packageName, List<String> functions)
    {
    	FunctionDocument docu = new FunctionDocument();
    	docu.setName(packageName);
    	for(String func : functions)
    	{
    		docu.addLink(packageName, func);
    	}
    	
    	return docu;
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
