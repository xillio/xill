package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import nl.xillio.udm.services.DocumentDefinitionService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Construct for loading a JSON representation of decorators
 *
 * @author Geert Konijnendijk
 */
public class LoadDecoratorDefinitionsConstruct extends LoadDefinitionsConstruct {


    /**
     * Create a new {@link LoadDefinitionsConstruct}
     *
     * @param definitionService
     */
    @Inject
    public LoadDecoratorDefinitionsConstruct(DocumentDefinitionService definitionService) {
        super(definitionService);
    }

    @Override
    protected void load(File json) throws FileNotFoundException {
        definitionService.loadDecorators(json);
    }

    @Override
    protected void load(String json) {
        definitionService.loadDecorators(json);
    }
}
