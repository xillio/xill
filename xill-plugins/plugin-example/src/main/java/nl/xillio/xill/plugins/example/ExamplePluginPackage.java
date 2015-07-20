package nl.xillio.xill.plugins.example;

import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.plugins.example.constructs.CopyConstruct;
import nl.xillio.xill.plugins.example.constructs.LifeConstuct;
import nl.xillio.xill.plugins.example.constructs.WebPreviewConstruct;

/**
 * This package includes all example constructs
 */
public class ExamplePluginPackage extends PluginPackage{

	@Override
	public void load(PluginPackage[] dependencies) {
		add(new LifeConstuct(), new CopyConstruct(), new WebPreviewConstruct());
	}

	@Override
	public String getName() {
		return "Example";
	}
}
