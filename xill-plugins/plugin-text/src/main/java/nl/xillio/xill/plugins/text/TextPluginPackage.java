package nl.xillio.xill.plugins.text;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all Text constructs
 */
public class TextPluginPackage extends PluginPackage{

	@Override
	public void load(PluginPackage[] dependencies) {
		add(new AbsoluteURLConstruct());
	}

	@Override
	public String getName() {
		return "Text";
	}

}
