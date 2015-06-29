package nl.xillio.xill.plugins.example;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * This construct will return the meaning of life. <br/>
 * This construct is the minimal required implementation for a function in the language.
 */
public class LifeConstuct implements Construct {

	@Override
	public String getName() {
		return "life";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(() -> ExpressionBuilder.fromValue(42));
	}
}
