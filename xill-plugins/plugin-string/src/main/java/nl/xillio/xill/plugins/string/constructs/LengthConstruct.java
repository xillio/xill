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
 *
 * Returns the number of characters in the provided string.
 *
 * @author Sander
 *
 */
public class LengthConstruct extends Construct implements HelpComponent {

    @Override
    public String getName() {

	return "length";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(LengthConstruct::process, new Argument("value"));
    }

    private static MetaExpression process(final MetaExpression value) {

	assertType(value, "value", ATOMIC);

	int length = value.getStringValue().length();
	return fromValue(length);
    }


  	@Override
  	public InputStream openDocumentationStream() {
  		return getClass().getResourceAsStream("/helpfiles/length.xml");
  	}
}
