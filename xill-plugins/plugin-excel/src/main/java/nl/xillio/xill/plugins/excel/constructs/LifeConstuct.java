package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * This construct will return the meaning of life.
 * This construct is the minimal required implementation for a function in the
 * language.
 */
public class LifeConstuct extends Construct {
	
	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(() -> fromValue(42));
	}
}
