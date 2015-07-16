package nl.xillio.xill.plugins.string.constructs;

import java.io.InputStream;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * Encodes all special XML characters (<,>,&,\",') to their respective xml
 * entities.
 *
 * @author Sander
 *
 */
public class AmpersandEncodeConstruct extends Construct implements HelpComponent {

	@Override
	public String getName() {

		return "ampersandEncode";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(AmpersandEncodeConstruct::process, new Argument("string"));
	}

	private static MetaExpression process(final MetaExpression stringVar) {

		assertType(stringVar, "string", ATOMIC);
		assertNotNull(stringVar, "string");

		String text = stringVar.getStringValue();

		return fromValue(StringEscapeUtils.escapeXml11(text));

	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/ampersandencode.xml");
	}

}
