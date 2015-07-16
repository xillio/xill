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
 * Makes the provided text lower case.
 *
 * @author Sander
 *
 */
public class LowerCaseConstruct extends Construct implements HelpComponent {

	@Override
	public String getName() {

		return "toLower";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(LowerCaseConstruct::process, new Argument("string"));
	}

	private static MetaExpression process(final MetaExpression string) {
		assertType(string, "string", ATOMIC);

		return fromValue(string.getStringValue().toLowerCase());
	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/lowercase.xml");
	}
}
