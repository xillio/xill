package nl.xillio.xill.plugins.rest.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.data.XmlNodeFactory;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.rest.data.Content;
import nl.xillio.xill.plugins.rest.data.Options;
import nl.xillio.xill.plugins.rest.services.RestService;
import nl.xillio.xill.services.json.JsonParser;

/**
 * This is an abstract implementation of the request constructs.
 *
 * @author Thomas Biesaart
 */
public abstract class AbstractRequestConstruct extends Construct {

    private final JsonParser jsonParser;
    private final XmlNodeFactory xmlNodeFactory;
    private final boolean hasBody;
    private final RestService restService;

    protected AbstractRequestConstruct(RestService restService, JsonParser jsonParser, XmlNodeFactory xmlNodeFactory, boolean hasBody) {
        this.restService = restService;
        this.jsonParser = jsonParser;
        this.xmlNodeFactory = xmlNodeFactory;
        this.hasBody = hasBody;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(this::processMeta, buildArguments());
    }

    protected RestService restService() {
        return restService;
    }

    protected MetaExpression processMeta(MetaExpression... input) {
        String url = input[0].getStringValue();
        if (url.isEmpty()) {
            throw new RobotRuntimeException("URL is empty!");
        }
        Options options = new Options(input[1]);
        Content body = null;
        if (hasBody) {
            body = new Content(input[2], input[3]);
        }
        return process(url, options, body).getMeta(jsonParser, xmlNodeFactory);
    }

    protected abstract Content process(String url, Options options, Content body);

    private Argument[] buildArguments() {
        if (hasBody) {
            return new Argument[]{
                    new Argument("url", ATOMIC),
                    new Argument("options", NULL, OBJECT),
                    new Argument("body", NULL, LIST, OBJECT, ATOMIC),
                    new Argument("contenttype", NULL, ATOMIC)
            };
        } else {
            return new Argument[]{
                    new Argument("url", ATOMIC),
                    new Argument("options", NULL, OBJECT)
            };
        }

    }
}
