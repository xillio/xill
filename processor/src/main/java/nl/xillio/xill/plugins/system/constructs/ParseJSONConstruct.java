package nl.xillio.xill.plugins.system.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.services.json.JsonException;
import nl.xillio.xill.services.json.JsonParser;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Forwards a JSON string to GSON
 */
public class ParseJSONConstruct extends Construct {

    @Inject
    private JsonParser jsonParser;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                json -> process(json, jsonParser),
                new Argument("json", ATOMIC));
    }

    static MetaExpression process(final MetaExpression json, final JsonParser jsonParser) {
        assertNotNull(json, "input");
        String jsonValue = json.getStringValue();

        try {
            Object result = jsonParser.fromJson(jsonValue, Object.class);
            return parseObject(result);
        } catch (JsonException e) {
            Throwable exception = ExceptionUtils.getRootCause(e);
            if (exception == null) {
                exception = e;
            }

            throw new RobotRuntimeException("Invalid JSON input: " + exception.getMessage(), e);
        }
    }
}
