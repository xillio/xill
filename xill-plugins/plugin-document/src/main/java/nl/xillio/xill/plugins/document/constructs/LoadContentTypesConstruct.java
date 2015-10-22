package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;
import nl.xillio.udm.services.DocumentDefinitionService;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Construct for loading a JSON representation of content types
 *
 * @author Geert Konijnendijk
 */
public class LoadContentTypesConstruct extends LoadDefinitionsConstruct {


    /**
     * Create a new {@link LoadDefinitionsConstruct}
     *
     * @param definitionService
     */
    @Inject
    public LoadContentTypesConstruct(DocumentDefinitionService definitionService) {
        super(definitionService);
    }

    @Override
    protected void load(File json) throws FileNotFoundException {
        definitionService.loadContentTypes(json);
    }

    @Override
    protected void load(String json) {
        definitionService.loadContentTypes(json);
    }
}
