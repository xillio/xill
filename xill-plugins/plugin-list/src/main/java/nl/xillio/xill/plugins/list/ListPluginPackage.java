package nl.xillio.xill.plugins.list;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all example constructs
 */
public class ListPluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
		add(new ToStringConstruct(), new ReverseConstruct(),new ContainsValueConstruct(),new RemoveConstruct(),new SortConstruct());
	}

	@Override
	public String getName() {
		return "List";
	}
}
