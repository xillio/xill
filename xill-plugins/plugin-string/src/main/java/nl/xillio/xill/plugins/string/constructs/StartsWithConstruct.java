package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 * Returns whether the first string starts with the second string. </br>
 *
 *
 * @author Sander
 *
 */
public class StartsWithConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			StartsWithConstruct::process,
			new Argument("string", ATOMIC),
			new Argument("prefix", ATOMIC));
	}

	private static MetaExpression process(final MetaExpression string1, final MetaExpression string2) {
		assertNotNull(string1, "string1");
		assertNotNull(string2, "string2");

		return fromValue(string1.getStringValue().startsWith(string2.getStringValue()));
	}
}
