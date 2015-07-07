package nl.xillio.migrationtool.documentation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.xml.sax.SAXException;

import nl.xillio.migrationtool.ElasticConsole.ESConsoleClient;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.HelpComponent;
import nl.xillio.xill.api.errors.NotImplementedException;

/**
 * @author Ivor
 *
 */
public class PluginListener {

    /**
     * @param plugin The plugin that we load
     * 
     */
    public void pluginLoaded(final PluginPackage plugin) {
	plugin.getName();
	XMLparser parser = new XMLparser();
	DocumentSearcher searcher = new DocumentSearcher(ESConsoleClient.getInstance().getClient());
	
	for (Construct construct : plugin.getConstructs()) {
	    if (construct instanceof HelpComponent) {
			HelpComponent documentedConstruct = (HelpComponent) construct;
			
			//If the version of the allready indexed function different from the version of the package
			//or the function is non-existant in the database, we 
			if(searcher.getDocumentVersionById(documentedConstruct.getName()) == null || 
			   searcher.getDocumentVersionById(documentedConstruct.getName()) == plugin.getVersion())
			{
				try {
					FunctionDocument docu = parser.parseXML(documentedConstruct.openDocumentationStream(), plugin.getVersion());
					FileWriter writer = new FileWriter(new File("./helpfiles/" + docu.getName() + ".html").getAbsolutePath(),false);
					writer.write(docu.toHTML());
					writer.close();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }
	}
    }

    public static void Attach(final PluginLoader<PluginPackage> pluginLoader) {
	PluginListener listener = new PluginListener();

	// Listen to all loaded plugins
	pluginLoader.getPluginManager().onPluginAccepted().addListener(listener::pluginLoaded);
    }
}
