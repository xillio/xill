package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

import com.google.inject.Inject;

/**
 * Wraps a piece of text to a certain width
 */
public class WrapConstruct extends Construct {
	@Inject
	StringUtilityService stringService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(text, width, wrapLongWords) -> process(text, width, wrapLongWords, stringService),
			new Argument("text", ATOMIC),
			new Argument("width", ATOMIC),
			new Argument("wrapLongWords", ATOMIC));
	}

	static MetaExpression process(final MetaExpression text, final MetaExpression width, final MetaExpression wrapLong, final StringUtilityService stringService) {

		String result = stringService.wrap(text.getStringValue(), width.getNumberValue().intValue(), wrapLong.getBooleanValue());
		return fromValue(result);
	}
}
