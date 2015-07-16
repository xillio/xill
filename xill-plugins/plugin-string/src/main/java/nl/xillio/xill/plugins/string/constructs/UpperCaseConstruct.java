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
public class UpperCaseConstruct extends Construct {

	@Override
	public String getName() {

		return "toUpper";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(UpperCaseConstruct::process, new Argument("string"));
	}

	private static MetaExpression process(final MetaExpression string) {

		assertType(string, "string", ATOMIC);

		return fromValue(string.getStringValue().toUpperCase());
	}
}
