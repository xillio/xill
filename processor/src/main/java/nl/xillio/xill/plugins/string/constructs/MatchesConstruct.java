package nl.xillio.xill.plugins.string.constructs;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
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
	private RegexService regexService;

	/**
	 * Create a new {@link MatchesConstruct}
	 */
	public MatchesConstruct() {}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(valueVar, regexVar, timeoutVar) -> process(valueVar, regexVar, timeoutVar, regexService),
			new Argument("value", ATOMIC),
			new Argument("regex", ATOMIC),
			new Argument("timeout", fromValue(RegexConstruct.REGEX_TIMEOUT), ATOMIC));
	}

	@SuppressWarnings("squid:S1166")
	static MetaExpression process(final MetaExpression valueVar, final MetaExpression regexVar, final MetaExpression timeoutVar, final RegexService regexService) {
		String value = valueVar.getStringValue();
		String regex = regexVar.getStringValue();

		int timeout = (int) timeoutVar.getNumberValue().doubleValue() * 1000;

		try {
			Matcher matcher = regexService.getMatcher(regex, value, timeout);
			return fromValue(regexService.matches(matcher));
		} catch (PatternSyntaxException p) {
			throw new RobotRuntimeException("Invalid pattern in matches.");
		} catch (IllegalArgumentException | FailedToGetMatcherException e) {
			throw new RobotRuntimeException("Illegal argument given.");
		}

	}
}
