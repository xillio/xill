package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.XillUDMPersistence;

import java.util.Map;

/**
 * This construct will fetch a document from the udm.
 *
 * @author Thomas Biesaart
 */
public class GetConstruct extends Construct {

    private final XillUDMPersistence persistence;

    @Inject
    public GetConstruct(XillUDMPersistence persistence) {
        this.persistence = persistence;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("documentId", ATOMIC)
        );
    }

    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar doesn't understand method references
    private MetaExpression process(MetaExpression documentId) {
        Map<String, Object> value = getDocument(documentId.getStringValue());

        return parseObject(value);
    }

    private Map<String, Object> getDocument(String id) {
        try {
            return persistence.getMap(id);
        } catch (DocumentNotFoundException e) {
            throw new RobotRuntimeException("Document [" + id + "] was not found", e);
        }
    }

}
