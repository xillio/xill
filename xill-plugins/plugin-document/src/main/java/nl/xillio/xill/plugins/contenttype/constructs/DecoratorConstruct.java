package nl.xillio.xill.plugins.contenttype.constructs;


import com.google.inject.Inject;
import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

import java.util.LinkedHashMap;

/**
 * This construct is used to define a decorator.
 *
 * @author Thomas Biesaart
 */
public class DecoratorConstruct extends Construct {

    private final DocumentDefinitionService documentDefinitionService;

    @Inject
    public DecoratorConstruct(DocumentDefinitionService documentDefinitionService) {
        this.documentDefinitionService = documentDefinitionService;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("decoratorName", ATOMIC),
                new Argument("definition", OBJECT)
        );
    }

    private MetaExpression process(MetaExpression name, MetaExpression definition) {
        String decoratorName = name.getStringValue();
        LinkedHashMap<String, MetaExpression> decoratorWrapper = new LinkedHashMap<>();
        decoratorWrapper.put(decoratorName, definition);

        String json = fromValue(decoratorWrapper).toString();
        documentDefinitionService.loadDecorators(json);

        return NULL;
    }
}
