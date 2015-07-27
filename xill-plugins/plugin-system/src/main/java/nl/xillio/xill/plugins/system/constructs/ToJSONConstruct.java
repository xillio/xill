package nl.xillio.xill.plugins.system.constructs;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.services.json.JsonParser;
import nl.xillio.xill.services.json.PrettyJsonParser;

/**
 * Returns a json string representation of the input
 */
public class ToJSONConstruct extends Construct {

	@Inject
	private JsonParser jsonParser;
	@Inject
	private PrettyJsonParser prettyJsonParser;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((expression, pretty) -> process(expression, pretty, jsonParser, prettyJsonParser), new Argument("expression"), new Argument("pretty", FALSE, ATOMIC));
	}

	static MetaExpression process(final MetaExpression expression, final MetaExpression pretty, final JsonParser parser, final JsonParser prettyParser) {
		assertNotNull(expression, "input");
		if (pretty.getBooleanValue()) {
			return fromValue(prettyParser.toJson(expression));
		}

		return fromValue(parser.toJson(expression));
	}
}
