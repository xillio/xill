package nl.xillio.xill.constructs.System;

import nl.xillio.xill.api.PluginPackage;

/**
 * This {@link PluginPackage} represents the System package
 */
public class SystemPluginPackage extends PluginPackage {

	/**
	 * Create a new {@link SystemPluginPackage} and instantiate the required constructs
	 */
	public SystemPluginPackage() {
		add(new PrintConstruct());
	}

	@Override
	public String getName() {
		return "System";
	}

	@Override
	public void load(final PluginPackage[] dependencies) {

	}

}
