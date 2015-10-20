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
 * Construct for loading definition JSON representations
 *
 * @author Geert Konijnendijk
 */
public abstract class LoadDefinitionsConstruct extends Construct {

    @Inject
    DocumentDefinitionService definitionService;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor((json) -> process(json, context),
                new Argument("json", ATOMIC, OBJECT));
    }

    /**
     * Load a definition from a JSON file
     * @param json The file
     * @throws FileNotFoundException When the json file is not found
     */
    protected abstract void load(File json) throws FileNotFoundException;

    /**
     * Load a definition from a JSON string
     * @param json The JSON string
     */
    protected abstract void load(String json);

    /**
     * Load a number of content types from a JSON representation
     *
     * @param json              When ATOMIC, load from the file identified by the string value, when OBJECT, treat as JSON
     * @param context           The context used for file resolving
     * @return NULL
     */
    MetaExpression process(MetaExpression json, ConstructContext context){
        // Atomic, so should contain a json filename
        if (json.getType() == ATOMIC) {
            try {
                load(getFile(context, json.getStringValue()));
            } catch (FileNotFoundException e) {
                throw new RobotRuntimeException("Content types JSON file not found", e);
            }
        }
        // Object, treat it as JSON
        else if (json.getType() == OBJECT) {
            load(json.toString());
        }

        return NULL;
    }
}
