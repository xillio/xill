package nl.xillio.xill.plugins.math;

import nl.xillio.xill.api.PluginPackage;

/**
 * This package includes all example constructs
 */
public class MathPluginPackage extends PluginPackage {

    @Override
    public void load(final PluginPackage[] dependencies) {
	add(new PowerConstuct(), new AbsConstruct(), new HungarianAlgorithmConstruct(), new RandomConstruct(),
		new RoundConstruct());
    }

    @Override
    public String getName() {
	return "Math";
    }
}
