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
 * Makes the provided text upper case.
 *
 * @author Sander
 *
 */
public class UpperCaseConstruct implements Construct {

	@Override
	public String getName() {

		return "toUpper";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(UpperCaseConstruct::process, new Argument("string"));
	}

	private static MetaExpression process(final MetaExpression string) {

		if (string.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}
		if (string == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null");
		}

		return ExpressionBuilder.fromValue(string.getStringValue().toUpperCase());
	}
}
