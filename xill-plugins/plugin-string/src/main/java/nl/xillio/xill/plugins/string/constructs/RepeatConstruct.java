package nl.xillio.xill.plugins.string.constructs;

import org.apache.commons.lang3.StringUtils;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 * Repeats the string the provided number of times.
 *
 * @author Sander
 *
 */
public class RepeatConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			RepeatConstruct::process,
			new Argument("string", ATOMIC),
			new Argument("value", ATOMIC));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression value) {
		assertNotNull(string, "string");
		assertNotNull(value, "value");

		String repeatedString = StringUtils.repeat(string.getStringValue(), value.getNumberValue().intValue());

		return fromValue(repeatedString);
	}
}
