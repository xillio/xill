package nl.xillio.xill.plugins.system;

import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.plugins.system.constructs.PrintConstruct;
import nl.xillio.xill.plugins.system.constructs.TypeOfConstruct;
import nl.xillio.xill.plugins.system.constructs.WaitConstruct;

/**
 * This package includes all system constructs
 */
public class SystemPluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
		add(new PrintConstruct(), new TypeOfConstruct(), new WaitConstruct());
	}

	@Override
	public String getName() {
		return "System";
	}

}
