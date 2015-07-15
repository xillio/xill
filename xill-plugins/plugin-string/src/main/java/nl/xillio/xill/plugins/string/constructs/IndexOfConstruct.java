package nl.xillio.xill.plugins.string.constructs;

import java.io.InputStream;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 *
 * Returns the first index of the needle in the provided text. Optionally an
 * alternative start position can be specified.
 *
 *
 * </br>
 *
 * @author Sander
 *
 */
public class IndexOfConstruct extends Construct implements HelpComponent {

    @Override
    public String getName() {

	return "indexOf";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(IndexOfConstruct::process, new Argument("string1"), new Argument("string2"), new Argument("value", fromValue(0)));
    }

    private static MetaExpression process(final MetaExpression string1, final MetaExpression string2, final MetaExpression value) {
	assertType(string1, "string1", ATOMIC);
	assertType(string2, "string2", ATOMIC);
	assertType(value, "value", ATOMIC);
	assertNotNull(string1, "string1");
	assertNotNull(string2, "string2");

	return fromValue(string1.getStringValue().indexOf(string2.getStringValue(), value.getNumberValue().intValue()));
    }


  	@Override
  	public InputStream openDocumentationStream() {
  		return getClass().getResourceAsStream("/helpfiles/indexof.xml");
  	}
}
