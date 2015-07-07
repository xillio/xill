package nl.xillio.xill.plugins.string;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
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
public class MatchesConstruct implements Construct {

	private final RegexConstruct regexConstruct;

	public MatchesConstruct(final RegexConstruct regexConstruct) {
		this.regexConstruct = regexConstruct;
	}

	@Override
	public String getName() {

		return "matches";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
				(valueVar, regexVar, timeoutVar) -> process(regexConstruct, valueVar, regexVar, timeoutVar),
				new Argument("valueVar"), new Argument("regexVar"),
				new Argument("timeoutVar", ExpressionBuilder.fromValue(RegexConstruct.REGEX_TIMEOUT)));
	}

	private static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression valueVar,
			final MetaExpression regexVar, final MetaExpression timeoutVar) {

		if (valueVar.getType() != ExpressionDataType.ATOMIC || regexVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		String value = valueVar.getStringValue();
		String regex = regexVar.getStringValue();

		int timeout = (int) timeoutVar.getNumberValue().doubleValue() * 1000;

		try {
			return ExpressionBuilder.fromValue(regexConstruct.getMatcher(regex, value, timeout).matches());
		} catch (Exception e) {
			throw new RobotRuntimeException("Invalid pattern in matches.");
		}

	}
}