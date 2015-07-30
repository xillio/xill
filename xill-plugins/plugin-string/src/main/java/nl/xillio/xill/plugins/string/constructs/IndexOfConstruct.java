package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringService;

import com.google.inject.Inject;

/**
 *
 * <p>
 * Returns the first index of the needle in the provided text.
 * </p>
 * <p>
 * Optionally an alternative start position can be specified.
 * </p>
 *
 * @author Sander
 *
 */
public class IndexOfConstruct extends Construct {
	@Inject
	private StringService stringService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(string1, string2, value) -> process(string1, string2, value, stringService),
			new Argument("string1", ATOMIC),
			new Argument("string2", ATOMIC),
			new Argument("value", fromValue(0), ATOMIC));
	}

	static MetaExpression process(final MetaExpression string1, final MetaExpression string2, final MetaExpression value, final StringService stringService) {
		assertNotNull(string1, "string1");
		assertNotNull(string2, "string2");

		return fromValue(stringService.indexOf(
			string1.getStringValue(), string2.getStringValue(), value.getNumberValue().intValue()));
	}
}
