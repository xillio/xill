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
 *
 *
 * Returns the number of characters in the provided string.
 *
 * @author Sander
 *
 */
public class LengthConstruct implements Construct {

	@Override
	public String getName() {

		return "length";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(LengthConstruct::process, new Argument("value"));
	}

	private static MetaExpression process(final MetaExpression value) {

		if (value.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}
		if (value == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		int length = value.getStringValue().length();
		return ExpressionBuilder.fromValue(length);
	}
}
