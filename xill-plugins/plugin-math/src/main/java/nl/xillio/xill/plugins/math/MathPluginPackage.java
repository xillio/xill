package nl.xillio.xill.plugins.math;

import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.plugins.math.constructs.AbsConstruct;
import nl.xillio.xill.plugins.math.constructs.HungarianAlgorithmConstruct;
import nl.xillio.xill.plugins.math.constructs.RandomConstruct;
import nl.xillio.xill.plugins.math.constructs.RoundConstruct;

/**
 * This package includes all example constructs
 */
public class MathPluginPackage extends PluginPackage {

	@Override
	public void load(final PluginPackage[] dependencies) {
		add(new AbsConstruct(), new HungarianAlgorithmConstruct(), new RandomConstruct(),
			new RoundConstruct());
	}
}
