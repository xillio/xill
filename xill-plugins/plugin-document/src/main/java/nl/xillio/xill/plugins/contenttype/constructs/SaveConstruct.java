package nl.xillio.xill.plugins.contenttype.constructs;

import com.google.inject.Inject;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.LinkedHashMap;

/**
 * This construct is used to save a content type to the database.
 *
 * @author Thomas Biesaart
 */
public class SaveConstruct extends Construct {
    private final DocumentDefinitionService documentDefinitionService;

    @Inject
    public SaveConstruct(DocumentDefinitionService documentDefinitionService) {
        this.documentDefinitionService = documentDefinitionService;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("contentTypeName", ATOMIC),
                new Argument("decorators", LIST)
        );
    }

    private MetaExpression process(MetaExpression contentTypeName, MetaExpression decorators) {
        String contentType = contentTypeName.getStringValue();

        LinkedHashMap<String, MetaExpression> contentTypesWrapper = new LinkedHashMap<>();
        contentTypesWrapper.put(contentType, decorators);

        String json = contentTypesWrapper.toString();

        documentDefinitionService.loadContentTypes(json);
        try {
            documentDefinitionService.persist(contentType);
        } catch (PersistenceException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }

        return NULL;
    }
}
