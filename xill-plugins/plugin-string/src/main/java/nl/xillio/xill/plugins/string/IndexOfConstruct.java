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
 * Returns the first index of the needle in the provided text. Optionally an alternative start position can be specified.
 * </br></br>
 * TODO:: not yet optional start position.
 * </br>
 * @author Sander
 *
 */
public class IndexOfConstruct implements Construct {

	@Override
	public String getName() {

		return "indexOf";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(IndexOfConstruct::process, new Argument("string1"), new Argument("string2"),
				new Argument("value"));
	}
	

	private static MetaExpression process(final MetaExpression string1, final MetaExpression string2,
			final MetaExpression value) {

		if (string1.getType() != ExpressionDataType.ATOMIC || string2.getType() != ExpressionDataType.ATOMIC
				|| value.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (string1 == ExpressionBuilder.NULL || string2 == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		return ExpressionBuilder.fromValue(
				string1.getStringValue().indexOf(string2.getStringValue(), value.getNumberValue().intValue()));
	}
}
