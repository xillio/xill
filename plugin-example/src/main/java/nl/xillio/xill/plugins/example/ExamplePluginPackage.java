package nl.xillio.xill.plugins.elasticsearch;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all elasticsearch constructs
 */
public class ExamplePluginPackage extends PluginPackage{

	@Override
	public void load(PluginPackage[] dependencies) {
		add();
	}

	@Override
	public String getName() {
		return "Example";
	}

}
