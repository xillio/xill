package nl.xillio.xill.plugins.system.constructs;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.system.services.json.JsonParser;

/**
 * Forwards a JSON string to GSON
 */
public class ParseJSONConstruct extends Construct {

	@Inject
	JsonParser jsonParser;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			json -> process(json, jsonParser),
			new Argument("json", ATOMIC));
	}

	private static MetaExpression process(final MetaExpression json, final JsonParser jsonParser) {
		assertNotNull(json, "input");
		String jsonValue = json.getStringValue();

		try {
			Object result = jsonParser.fromJson(jsonValue, Object.class);
			return parseObject(result);
		} catch (JsonSyntaxException e) {
			Throwable exception = ExceptionUtils.getRootCause(e);
			if (exception == null) {
				exception = e;
			}

			throw new RobotRuntimeException("Invalid JSON input: " + exception.getMessage(), e);
		}
	}
}
