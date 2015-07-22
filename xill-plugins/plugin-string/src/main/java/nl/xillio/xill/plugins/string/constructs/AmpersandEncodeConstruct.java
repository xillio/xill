package nl.xillio.xill.plugins.string.constructs;

import org.apache.commons.lang3.StringEscapeUtils;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 * Encodes all special XML characters (<,>,&,\",') to their respective xml
 * entities.
 *
 * @author Sander
 *
 */
public class AmpersandEncodeConstruct extends Construct {

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

}
