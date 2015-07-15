package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 * Returns whether the first string ends with the second string. </br>
 *
 *
 * @author Sander
 *
 */
public class EndsWithConstruct extends Construct {

    @Override
    public String getName() {

	return "endsWith";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(EndsWithConstruct::process, new Argument("string1"), new Argument("string2"));
    }

    private static MetaExpression process(final MetaExpression string1, final MetaExpression string2) {
	assertType(string1, "string1", ATOMIC);
	assertType(string2, "string2", ATOMIC);
	assertNotNull(string1, "string1");
	assertNotNull(string2, "string2");

	return fromValue(string1.getStringValue().endsWith(string2.getStringValue()));
    }
}
