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
 * Decodes all ampersand-encoded characters in the provided text.
 *
 * @author Sander
 *
 */
public class AmpersandDecodeConstruct extends Construct implements HelpComponent {

	@Override
	public String getName() {

		return "ampersandDecode";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(AmpersandDecodeConstruct::process, new Argument("string"), new Argument("passes", fromValue(1)));
	}

	private static MetaExpression process(final MetaExpression stringVar, final MetaExpression passesVar) {
		assertType(stringVar, "string", ATOMIC);
		assertType(passesVar, "passes", ATOMIC);

		assertNotNull(stringVar, "string");
		assertNotNull(passesVar, "passes");

		String text = stringVar.getStringValue();

		int passes = passesVar.getNumberValue().intValue();

		for (int i = 0; i < passes; i++) {
			text = StringEscapeUtils.unescapeXml(text);
		}

		return fromValue(text);

	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/ampersanddecode.xml");
	}

}
