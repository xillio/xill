package nl.xillio.xill.plugins.system.constructs;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Forwards a JSON string to GSON
 */
public class ParseJSONConstruct extends Construct {

	private static final Gson gson = new GsonBuilder().create();

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ParseJSONConstruct::process, new Argument("json"));
	}

	private static MetaExpression process(final MetaExpression json) {
		String jsonValue = json.getStringValue();

		try {
			Object result = gson.fromJson(jsonValue, Object.class);
			return parseObject(result);
		}catch(JsonSyntaxException e) {
			Throwable exception = ExceptionUtils.getRootCause(e);
			if(exception == null) {
				exception = e;
			}
			
			throw new RobotRuntimeException("Invalid JSON input: " + exception.getMessage(), e);
		}
	}
}
