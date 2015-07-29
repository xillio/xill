package nl.xillio.xill.plugins.string.constructs;

import java.util.regex.Matcher;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import com.google.inject.Inject;

/**
 * Returns whether the provided value matches the specified regex.
 *
 * @author Sander
 *
 */
public class MatchesConstruct extends Construct {

	@Inject
	private RegexConstruct regexConstruct;
	@Inject
	private RegexService regexService;

	/**
	 * Create a new {@link MatchesConstruct}
	 */
	public MatchesConstruct() {}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(valueVar, regexVar, timeoutVar) -> process(regexConstruct, valueVar, regexVar, timeoutVar, regexService),
			new Argument("value", ATOMIC),
			new Argument("regex", ATOMIC),
			new Argument("timeout", fromValue(RegexConstruct.REGEX_TIMEOUT)));
	}

	private static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression valueVar, final MetaExpression regexVar, final MetaExpression timeoutVar, RegexService regexService) {
		String value = valueVar.getStringValue();
		String regex = regexVar.getStringValue();

		int timeout = (int) timeoutVar.getNumberValue().doubleValue() * 1000;

		try {
			Matcher matcher = regexConstruct.getMatcher(regex, value, timeout);
			return fromValue(regexService.matches(matcher));
		} catch (Exception e) {
			throw new RobotRuntimeException("Invalid pattern in matches.");
		}

	}
}
