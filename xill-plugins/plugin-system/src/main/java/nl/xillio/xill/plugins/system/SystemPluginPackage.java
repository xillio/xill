package nl.xillio.xill.plugins.system;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all system constructs
 */
public class SystemPluginPackage extends PluginPackage{

	@Override
	public void load(PluginPackage[] dependencies) {
		add(new PrintConstruct(), new TypeOfConstruct(), new WaitConstruct());
	}

	@Override
	public String getName() {
		return "System";
	}

}
