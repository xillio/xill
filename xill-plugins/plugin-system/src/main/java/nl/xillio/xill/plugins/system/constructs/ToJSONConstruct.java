package nl.xillio.xill.plugins.system.constructs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Returns a json string representation of the input
 */
public class ToJSONConstruct extends Construct {

	private static final Gson gson = new GsonBuilder()
		.enableComplexMapKeySerialization()
		.serializeNulls()
		.setPrettyPrinting()
		.disableHtmlEscaping()
		.disableInnerClassSerialization()
		.serializeSpecialFloatingPointValues()
		.serializeNulls().create();

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ToJSONConstruct::process, new Argument("expression"), new Argument("pretty",FALSE));
	}

	private static MetaExpression process(final MetaExpression expression, final MetaExpression pretty) {
		assertNotNull(expression, "input");
		if (pretty.getBooleanValue()) {
			return fromValue(expression.toString(gson));
		}

		return fromValue(expression.toString());
	}
}
