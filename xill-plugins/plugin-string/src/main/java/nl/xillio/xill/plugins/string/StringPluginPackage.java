package nl.xillio.xill.plugins.string;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all Text constructs
 */
public class StringPluginPackage extends PluginPackage{

	@Override
	public void load(PluginPackage[] dependencies) {
		add(
			new AbsoluteURLConstruct(),
			new ContainsConstruct(),
			new LengthConstruct(),
			new SplitConstruct(),
			new UpperCaseConstruct(),
			new LowerCaseConstruct(),
			new StartsWithConstruct(),
			new EndsWithConstruct(),
			new RepeatConstruct(),
			new IndexOfConstruct()
			);
			
	}

	@Override
	public String getName() {
		return "String";
	}

}
