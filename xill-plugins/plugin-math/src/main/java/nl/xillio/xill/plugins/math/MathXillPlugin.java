package nl.xillio.xill.plugins.math;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.math.constructs.AbsConstruct;
import nl.xillio.xill.plugins.math.constructs.HungarianAlgorithmConstruct;
import nl.xillio.xill.plugins.math.constructs.RandomConstruct;
import nl.xillio.xill.plugins.math.constructs.RoundConstruct;

/**
 * This package includes all example constructs
 */
public class MathXillPlugin extends XillPlugin {

	@Override
	public void loadConstructs() {
		add(new AbsConstruct(), new HungarianAlgorithmConstruct(), new RandomConstruct(),
			new RoundConstruct());
	}
}
