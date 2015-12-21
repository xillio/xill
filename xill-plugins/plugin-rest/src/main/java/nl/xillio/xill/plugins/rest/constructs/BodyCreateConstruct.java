package nl.xillio.xill.plugins.rest.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.rest.data.MultipartBody;
import nl.xillio.xill.plugins.rest.services.RestService;

/**
 * Create empty multipart REST body variable
 *
 * @author Zbynek Hochmann
 */
public class BodyCreateConstruct extends Construct {

    @Inject
    private RestService restService;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                () -> process(restService)
        );
    }

    static MetaExpression process(final RestService service) {
        MultipartBody body = service.bodyCreate();
        MetaExpression output = fromValue(body.toString());
        output.storeMeta(body);
        return output;
    }

}
