package nl.xillio.xill.plugins.system;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Returns the type of the input
 */
public class TypeOfConstruct implements Construct {

	@Override
	public String getName() {
		return "typeOf";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(TypeOfConstruct::process, new Argument("value"));
	}

	private static MetaExpression process(final MetaExpression value) {
		return ExpressionBuilder.fromValue(value.getType().toString());
	}
}
