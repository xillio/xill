package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 *
 * Returns the number of characters in the provided string.
 *
 * @author Sander
 *
 */
public class LengthConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			LengthConstruct::process,
			new Argument("value", ATOMIC));
	}

	private static MetaExpression process(final MetaExpression value) {
		int length = value.getStringValue().length();
		return fromValue(length);
	}
}
