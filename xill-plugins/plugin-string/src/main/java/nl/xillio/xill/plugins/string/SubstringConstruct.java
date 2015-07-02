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
 * Returns the substring of text between position start and position end.
 *
 * @author Sander
 *
 */
public class SubstringConstruct implements Construct {

	@Override
	public String getName() {

		return "substring";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(SubstringConstruct::process, new Argument("string"), new Argument("start"),
				new Argument("end"));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression startVar,
			final MetaExpression endVar) {

		// The start and end value have to be atomic.
		if (startVar.getType() != ExpressionDataType.ATOMIC || endVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		// All inputs cannot be null.
		if (string == ExpressionBuilder.NULL || startVar == ExpressionBuilder.NULL
				|| endVar == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		String text = string.getStringValue();
		int start = startVar.getNumberValue().intValue();
		int end = endVar.getNumberValue().intValue();

		// Special case; If end equals 0, then take the full length of the
		// string.
		if (end == 0) {
			end = text.length();
		}

		// If end is smaller than start, then start is basically invalid. Assume
		// start = 0
		if (end < start) {
			start = 0;
		}

		try {
			return ExpressionBuilder.fromValue(text.substring(start, end));
		} catch (StringIndexOutOfBoundsException e) {
			throw new RobotRuntimeException("Index out of bounds.");
		}

	}
}
