package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 * Returns the substring of text between position start and position end. </br>
 * If the end position equals 0 it will take the full length of the
 * string. </br>
 * The start position is set to 0 if the end position is smaller than the start
 * position.
 *
 * @author Sander
 *
 */
public class SubstringConstruct extends Construct {

    @Override
    public String getName() {

	return "substring";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(SubstringConstruct::process, new Argument("string"), new Argument("start"), new Argument("end"));
    }

    private static MetaExpression process(final MetaExpression string, final MetaExpression startVar, final MetaExpression endVar) {

	assertType(string, "string", ATOMIC);
	assertType(startVar, "start", ATOMIC);
	assertType(endVar, "end", ATOMIC);
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
