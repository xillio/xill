package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.XillUDMPersistence;

/**
 * This construct will delete a document by id.
 *
 * @author Thomas Biesaart
 */
public class DeleteConstruct extends Construct {
    private final XillUDMPersistence persistence;

    @Inject
    public DeleteConstruct(XillUDMPersistence persistence) {
        this.persistence = persistence;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("documentId", ATOMIC)
        );
    }

    private MetaExpression process(MetaExpression documentId) {
        try {
            persistence.delete(documentId.getStringValue());
        } catch (PersistenceException e) {
            throw new RobotRuntimeException("Could not delete document [" + documentId.getStringValue() + "]", e);
        }

        return NULL;
    }
}
