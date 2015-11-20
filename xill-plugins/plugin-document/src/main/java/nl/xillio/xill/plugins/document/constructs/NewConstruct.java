package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.xill.UDMDocumentConstructor;

/**
 * This construct will build a udm document from input parameters.
 *
 * @author Thomas Biesaart
 */
public class NewConstruct extends Construct {

    private final UDMDocumentConstructor constructor;

    @Inject
    public NewConstruct(UDMDocumentConstructor constructor) {
        this.constructor = constructor;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("contentType", ATOMIC),
                new Argument("currentVersion", OBJECT),
                new Argument("history", emptyList(), LIST)
        );
    }

    public MetaExpression process(MetaExpression contentType, MetaExpression currentVersion, MetaExpression history) {
        String contentTypeName = contentType.getStringValue();

        try {
            return constructor.buildStructure(contentTypeName, currentVersion, history);
        } catch (IllegalArgumentException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
    }
}
