package nl.xillio.xill.plugins.list;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all example constructs
 */
public class ListPluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
		add(new ToStringConstruct(), new ToCSVConstruct(), new ReverseConstruct());
	}

	@Override
	public String getName() {
		return "List";
	}
}
