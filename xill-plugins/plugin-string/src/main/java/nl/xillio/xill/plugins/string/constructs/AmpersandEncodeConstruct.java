package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import com.google.inject.Inject;

/**
 *
 * <p>
 * Encodes all special XML characters (<,>,&,\",') to their respective xml entities.
 * </p>
 *
 * @author Sander
 *
 */
public class AmpersandEncodeConstruct extends Construct {
	@Inject
	private RegexService regexService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			string -> process(string, regexService),
			new Argument("String", ATOMIC));
	}

	@SuppressWarnings("javadoc")
	public static MetaExpression process(final MetaExpression stringVar, final RegexService regexService) {

		assertNotNull(stringVar, "string");

		String text = stringVar.getStringValue();

		return fromValue(regexService.escapeXML(text));

	}

}
