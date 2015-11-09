package nl.xillio.xill.plugins.rest.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.rest.data.MultipartBody;
import nl.xillio.xill.plugins.rest.services.RestService;

/**
 * Add text content to multipart REST body
 *
 * @author Zbynek Hochmann
 */
public class BodyAddTextConstruct extends Construct {

    @Inject
    private RestService restService;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                (body, name, text) -> process(body, name, text, restService),
                new Argument("body", ATOMIC),
                new Argument("name", ATOMIC),
                new Argument("text", ATOMIC)
        );
    }

    static MetaExpression process(final MetaExpression bodyVar, final MetaExpression nameVar, final MetaExpression textVar, final RestService service) {
        MultipartBody body = assertMeta(bodyVar, "body", MultipartBody.class, "REST Body");
        String name = nameVar.getStringValue();
        String text = textVar.getStringValue();
        service.bodyAddText(body, name, text);
        return NULL;
    }
}
