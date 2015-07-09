package nl.xillio.xill.plugins.math;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all example constructs
 */
public class MathPluginPackage extends PluginPackage{

	@Override
	public void load(PluginPackage[] dependencies) {
		add(new LifeConstuct(),
			new AbsConstruct(),
			new AbsHelpComponent(),
		    new HungarianAlgorithmConstruct(),
		    new HungarianAlgorithmHelpComponent(),
		    new RandomConstruct(),
		    new RandomHelpComponent(),
		    new RoundConstruct(),
		    new RoundHelpComponent());
	}

	@Override
	public String getName() {
		return "Math";
	}
}
