package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 *
 * Makes the provided text lower case.
 *
 * @author Sander
 *
 */
public class ToLowerConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ToLowerConstruct::process, new Argument("string"));
	}

	private static MetaExpression process(final MetaExpression string) {
		assertType(string, "string", ATOMIC);

		return fromValue(string.getStringValue().toLowerCase());
	}
}
