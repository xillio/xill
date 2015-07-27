package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Returns whether the provided value matches the specified regex.
 *
 * @author Sander
 *
 */
public class MatchesConstruct extends Construct {

	private final RegexConstruct regexConstruct;

	/**
	 * Create a new {@link MatchesConstruct}
	 *
	 * @param regexConstruct
	 *        the construct used to find the matches
	 */
	public MatchesConstruct(final RegexConstruct regexConstruct) {
		this.regexConstruct = regexConstruct;
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(valueVar, regexVar, timeoutVar) -> process(regexConstruct, valueVar, regexVar, timeoutVar),
			new Argument("value", ATOMIC),
			new Argument("regex", ATOMIC),
			new Argument("timeout", fromValue(RegexConstruct.REGEX_TIMEOUT)));
	}

	private static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression valueVar, final MetaExpression regexVar, final MetaExpression timeoutVar) {
		String value = valueVar.getStringValue();
		String regex = regexVar.getStringValue();

		int timeout = (int) timeoutVar.getNumberValue().doubleValue() * 1000;

		try {
			return fromValue(regexConstruct.getMatcher(regex, value, timeout).matches());
		} catch (Exception e) {
			throw new RobotRuntimeException("Invalid pattern in matches.");
		}

	}
}
