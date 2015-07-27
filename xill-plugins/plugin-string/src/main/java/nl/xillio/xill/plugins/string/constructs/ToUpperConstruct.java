package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 *
 * Makes the provided text upper case.
 *
 * @author Sander
 *
 */
public class ToUpperConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			ToUpperConstruct::process, 
			new Argument("string", ATOMIC));
	}

	private static MetaExpression process(final MetaExpression string) {

		return fromValue(string.getStringValue().toUpperCase());
	}
}
