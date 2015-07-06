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
 * Returns whether the first string starts with the second string. </br>
 *
 *
 * @author Sander
 *
 */
public class StartsWithConstruct implements Construct {

	@Override
	public String getName() {

		return "startsWith";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(StartsWithConstruct::process, new Argument("string1"), new Argument("string2"));
	}

	private static MetaExpression process(final MetaExpression string1, final MetaExpression string2) {

		if (string1.getType() != ExpressionDataType.ATOMIC || string2.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (string1 == ExpressionBuilder.NULL || string2 == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		return ExpressionBuilder.fromValue(string1.getStringValue().startsWith(string2.getStringValue()));
	}
}
