package nl.xillio.xill.plugins.example;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all example constructs
 */
public class ExamplePluginPackage extends PluginPackage{

	@Override
	public void load(PluginPackage[] dependencies) {
		add(new LifeConstuct(), new CopyConstruct());
	}

	@Override
	public String getName() {
		return "Example";
	}
}
