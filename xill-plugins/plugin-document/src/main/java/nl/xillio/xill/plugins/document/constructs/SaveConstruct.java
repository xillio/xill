package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;
import nl.xillio.xill.plugins.document.services.XillUDMPersistence;
import nl.xillio.xill.plugins.document.services.xill.UDMDocumentBuilder;

/**
 * This construct will save a document to the database.
 *
 * @author Thomas Biesaart
 */
public class SaveConstruct extends Construct {


    private final XillUDMPersistence persistence;
    private final UDMDocumentBuilder documentBuilder;

    @Inject
    public SaveConstruct(XillUDMPersistence persistence, UDMDocumentBuilder documentBuilder) {
        this.persistence = persistence;
        this.documentBuilder = documentBuilder;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("document", OBJECT)
        );
    }

    public MetaExpression process(MetaExpression document) {

        UDMDocument udmDocument = documentBuilder.build(document);

        try {
            persistence.save(udmDocument);
        } catch (PersistException | ValidationException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }

        return NULL;
    }
}
