package nl.xillio.xill.plugins.document.constructs;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Construct for loading a JSON representation of content types
 *
 * @author Geert Konijnendijk
 */
public class LoadContentTypesConstruct extends LoadDefinitionsConstruct {


    @Override
    protected void load(File json) throws FileNotFoundException {
        definitionService.loadContentTypes(json);
    }

    @Override
    protected void load(String json) {
        definitionService.loadContentTypes(json);
    }
}
