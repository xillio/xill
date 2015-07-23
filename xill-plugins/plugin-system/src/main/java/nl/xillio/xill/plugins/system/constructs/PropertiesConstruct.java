package nl.xillio.xill.plugins.system.constructs;

import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * Gets a list or a single system property
 */
public class PropertiesConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(PropertiesConstruct::process, new Argument("property", NULL, ATOMIC));
	}

	private static MetaExpression process(final MetaExpression property) {

		if (property.isNull()) {
			return fromValue(getProperties());
		}

		String system = System.getProperty(property.getStringValue());
		String env = System.getenv(property.getStringValue());

		// Parse the results
		String result = "";
		if (system != null) {
			result += system;
		}
		if (env != null) {
			if (!result.isEmpty()) {
				result += ";";
			}
			result += env;
		}

		if (result.isEmpty()) {
			return NULL;
		}

		return fromValue(result);
	}

	private static LinkedHashMap<String, MetaExpression> getProperties() {
		LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();

		System.getProperties().forEach((key, value) -> {
			result.put(key.toString(), fromValue(value.toString()));
		});

		System.getenv().forEach((key, value) -> {
			result.put(key, fromValue(value));
		});

		return result;
	}
}
