package nl.xillio.xill.plugins.list;

import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.plugins.list.constructs.ContainsValueConstruct;
import nl.xillio.xill.plugins.list.constructs.RemoveConstruct;
import nl.xillio.xill.plugins.list.constructs.ReverseConstruct;
import nl.xillio.xill.plugins.list.constructs.SortConstruct;
import nl.xillio.xill.plugins.list.constructs.ToStringConstruct;

/**
 * This package includes all list constructs
 */
public class ListPluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
		add(new ToStringConstruct(), new ReverseConstruct(),new ContainsValueConstruct(),new RemoveConstruct(),new SortConstruct());
	}
}
