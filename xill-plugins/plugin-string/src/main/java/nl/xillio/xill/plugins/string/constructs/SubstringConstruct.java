package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 * <p>
 * Returns the substring of text between position start and position end.
 * <p>
 * <p>
 * If the end position equals 0 it will take the full length of the string.
 * </p>
 * <p>
 * The start position is set to 0 if the end position is smaller than the start position.
 * </p>
 *
 * @author Sander
 *
 */
public class SubstringConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			SubstringConstruct::process,
			new Argument("string", ATOMIC),
			new Argument("start", ATOMIC),
			new Argument("end", ATOMIC));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression startVar, final MetaExpression endVar) {
		assertNotNull(string, "string");
		assertNotNull(startVar, "start");
		assertNotNull(endVar, "end");

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
			return fromValue(text.substring(start, end));
		} catch (StringIndexOutOfBoundsException e) {
			throw new RobotRuntimeException("Index out of bounds.");
		}

	}
}
