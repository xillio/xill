package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Construct for persisting a loaded content type and all its decorator definitions
 */
public class PersistContentTypeConstruct extends Construct{

    @Inject
    DocumentDefinitionService definitionService;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(contentType -> process(contentType, definitionService),
                new Argument("contentType", ATOMIC));
    }

    static MetaExpression process(MetaExpression contentType, DocumentDefinitionService definitionService){
        String contentTypeName = contentType.getStringValue();

        try {
            definitionService.persist(contentTypeName);
        } catch (PersistenceException e) {
            throw new RobotRuntimeException("Could not persist content type: " + e.getMessage(), e);
        }

        return NULL;
    }
}
