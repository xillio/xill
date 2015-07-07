package nl.xillio.xill.plugins.string;

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
 *
 * Repeats the string the provided number of times.
 *
 * @author Sander
 *
 */
public class RepeatConstruct implements Construct {

	@Override
	public String getName() {

		return "repeat";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(RepeatConstruct::process, new Argument("string"), new Argument("value"));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression value) {

		if (string.getType() != ExpressionDataType.ATOMIC || value.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (string == ExpressionBuilder.NULL || value == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		String repeatedString = StringUtils.repeat(string.getStringValue(), value.getNumberValue().intValue());

		return ExpressionBuilder.fromValue(repeatedString);
	}
}
