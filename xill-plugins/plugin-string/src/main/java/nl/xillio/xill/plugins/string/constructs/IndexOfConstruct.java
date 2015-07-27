package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 * Returns the first index of the needle in the provided text. Optionally an
 * alternative start position can be specified.
 *
 *
 * </br>
 *
 * @author Sander
 *
 */
public class IndexOfConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			IndexOfConstruct::process, 
			new Argument("string1", ATOMIC), 
			new Argument("string2", ATOMIC), 
			new Argument("value", fromValue(0), ATOMIC));
	}

	private static MetaExpression process(final MetaExpression string1, final MetaExpression string2, final MetaExpression value) {
		assertNotNull(string1, "string1");
		assertNotNull(string2, "string2");

		return fromValue(string1.getStringValue().indexOf(string2.getStringValue(), value.getNumberValue().intValue()));
	}
}
