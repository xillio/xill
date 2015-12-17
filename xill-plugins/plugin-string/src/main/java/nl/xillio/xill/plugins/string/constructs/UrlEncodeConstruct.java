package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

import com.google.inject.Inject;

import java.io.UnsupportedEncodingException;

/**
 *
 * Do URL encoding of the provided string
 *
 */
public class UrlEncodeConstruct extends Construct {
	@Inject
	StringUtilityService stringService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
                (string, xWwwForm) -> process(string, xWwwForm, stringService),
			new Argument("string", ATOMIC),
            new Argument("xWwwForm", FALSE, ATOMIC));
	}

	static MetaExpression process(final MetaExpression string, final MetaExpression xWwwFormVar, final StringUtilityService stringService) {
		try {
			return string.isNull() ? NULL : fromValue(stringService.urlEncode(string.getStringValue(), xWwwFormVar.isNull() ? false : xWwwFormVar.getBooleanValue()));
		} catch (UnsupportedEncodingException e) {
			throw new RobotRuntimeException("Cannot URL encode the string", e);
		}
	}
}
