package nl.xillio.xill.plugins.string;

import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Returns a new string in which occurrences of regex needle found in the text
 * have been replaced by the replacement string. If the parameter 'replaceall'
 * is set to false, the routine will only replace the first occurrence. If the
 * parameter 'useregex' is set to false, the routine will not use a regex.
 *
 * @author Sander
 *
 */
public class ReplaceConstruct implements Construct {

	private final RegexConstruct regexConstruct;

	public ReplaceConstruct(final RegexConstruct regexConstruct) {
		this.regexConstruct = regexConstruct;
	}

	@Override
	public String getName() {

		return "replace";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument args[] = { new Argument("text"), new Argument("needle"), new Argument("replacement"),
				new Argument("useregex", ExpressionBuilder.TRUE), new Argument("replaceall", ExpressionBuilder.TRUE),
				new Argument("timeout", ExpressionBuilder.fromValue(RegexConstruct.REGEX_TIMEOUT)) };

		return new ConstructProcessor((a) -> process(regexConstruct, a), args);

	}

	private static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression[] input) {

		for (int i = 0; i < 5; i++) {
			if (input[i] == ExpressionBuilder.NULL) {
				throw new RobotRuntimeException("Input cannot be null.");
			} else if (input[i].getType() != ExpressionDataType.ATOMIC) {
				throw new RobotRuntimeException("Expected atomic value.");
			}
		}
		String text = input[0].getStringValue();
		String needle = input[1].getStringValue();
		String replacement = input[2].getStringValue();
		boolean useregex = input[3].getBooleanValue();
		boolean replaceall = input[4].getBooleanValue();
		int timeout = (int) input[5].getNumberValue().doubleValue() * 1000;

		if (useregex) {
			Matcher m = regexConstruct.getMatcher(needle, text, timeout);
			if (replaceall) {
				return ExpressionBuilder.fromValue(m.replaceAll(replacement));
			}
			return ExpressionBuilder.fromValue(m.replaceFirst(replacement));
		}
		if (replaceall) {
			return ExpressionBuilder.fromValue(text.replace(needle, replacement));
		}
		return ExpressionBuilder.fromValue(StringUtils.replaceOnce(text, needle, replacement));

	}
}
