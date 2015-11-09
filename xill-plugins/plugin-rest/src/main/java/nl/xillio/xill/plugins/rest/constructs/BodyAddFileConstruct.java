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
 * Add file to multipart REST body
 *
 * @author Zbynek Hochmann
 */
public class BodyAddFileConstruct extends Construct {

    @Inject
    private RestService restService;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
            (body, name, fileName) -> process(body, name, fileName, restService),
                new Argument("body", ATOMIC),
                new Argument("name", ATOMIC),
                new Argument("fileName", ATOMIC)
        );
    }

    static MetaExpression process(final MetaExpression bodyVar, final MetaExpression nameVar, final MetaExpression fileNameVar, final RestService service) {
        MultipartBody body = assertMeta(bodyVar, "body", MultipartBody.class, "REST Body");
        String name = nameVar.getStringValue();
        String fileName = fileNameVar.getStringValue();
        service.bodyAddFile(body, name, fileName);
        return NULL;
    }
}
