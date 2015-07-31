package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

import com.google.inject.Inject;

/**
 *
 * <p>
 * Returns whether the first string starts with the second string.
 * </p>
 *
 *
 * @author Sander
 *
 */
public class StartsWithConstruct extends Construct {
	@Inject
	StringUtilityService stringService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(string, prefix) -> process(string, prefix, stringService),
			new Argument("string", ATOMIC),
			new Argument("prefix", ATOMIC));
	}

	static MetaExpression process(final MetaExpression string1, final MetaExpression string2, final StringUtilityService stringService) {
		assertNotNull(string1, "string1");
		assertNotNull(string2, "string2");

		return fromValue(stringService.startsWith(string1.getStringValue(), string2.getStringValue()));
	}
}
