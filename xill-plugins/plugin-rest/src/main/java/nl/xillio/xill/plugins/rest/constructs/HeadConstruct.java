package nl.xillio.xill.plugins.rest.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.data.XmlNodeFactory;
import nl.xillio.xill.plugins.rest.data.Content;
import nl.xillio.xill.plugins.rest.data.Options;
import nl.xillio.xill.plugins.rest.services.RestService;
import nl.xillio.xill.services.json.JsonParser;

/**
 * Returns content of HEAD Rest command.
 *
 * @author Zbynek Hochmann
 */
public class HeadConstruct extends AbstractRequestConstruct {

    @Inject
    protected HeadConstruct(RestService restService, JsonParser jsonParser, XmlNodeFactory xmlNodeFactory) {
        super(restService, jsonParser, xmlNodeFactory, false);
    }

    @Override
    protected Content process(String url, Options options, Content body) {
        return restService().head(url, options);
    }
}
