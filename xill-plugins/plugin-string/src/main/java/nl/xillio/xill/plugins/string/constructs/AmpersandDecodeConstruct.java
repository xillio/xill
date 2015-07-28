package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.system.services.regex.RegexService;

import com.google.inject.Inject;

/**
 *
 * <p>
 * Decodes all ampersand-encoded characters in the provided text.
 * </p>
 *
 * @author Sander
 *
 */
public class AmpersandDecodeConstruct extends Construct {

	@Inject
	private RegexService regexService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(string, passes) -> process(string, passes, regexService),
			new Argument("string", ATOMIC),
			new Argument("passes", fromValue(1), ATOMIC));
	}

	private static MetaExpression process(final MetaExpression stringVar, final MetaExpression passesVar, final RegexService regexService) {

		assertNotNull(stringVar, "string");
		assertNotNull(passesVar, "passes");

		String text = stringVar.getStringValue();

		int passes = passesVar.getNumberValue().intValue();

		return fromValue(regexService.unescapeXML(text, passes));
	}
}
