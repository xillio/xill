package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Returns the type of the input
 */
public class TypeOfConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(TypeOfConstruct::process, new Argument("value"));
	}

	static MetaExpression process(final MetaExpression value) {
		return fromValue(value.getType().toString());
	}
}
