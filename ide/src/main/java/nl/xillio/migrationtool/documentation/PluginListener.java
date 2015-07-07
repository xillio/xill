package nl.xillio.migrationtool.documentation;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.HelpComponent;
import nl.xillio.xill.api.errors.NotImplementedException;

public class PluginListener {

    public void pluginLoaded(final PluginPackage plugin) {
	plugin.getName();

	for (Construct construct : plugin.getConstructs()) {
	    if (construct instanceof HelpComponent) {
		HelpComponent documentedConstruct = (HelpComponent) construct;

		throw new NotImplementedException(documentedConstruct + " not impl");
	    }
	}
    }

    public static void Attach(final PluginLoader<PluginPackage> pluginLoader) {
	PluginListener listener = new PluginListener();

	// Listen to all loaded plugins
	pluginLoader.getPluginManager().onPluginAccepted().addListener(listener::pluginLoaded);
    }
}
