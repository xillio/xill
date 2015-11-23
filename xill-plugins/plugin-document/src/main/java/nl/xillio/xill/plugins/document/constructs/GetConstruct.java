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
import nl.xillio.xill.services.json.JsonException;
import nl.xillio.xill.services.json.JsonParser;

import java.util.Map;

/**
 * This construct will fetch a document from the udm.
 *
 * @author Thomas Biesaart
 */
public class GetConstruct extends Construct {

    private final XillUDMPersistence persistence;
    private final JsonParser jsonParser;

    @Inject
    public GetConstruct(XillUDMPersistence persistence, JsonParser jsonParser) {
        this.persistence = persistence;
        this.jsonParser = jsonParser;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("documentId", ATOMIC)
        );
    }

    private MetaExpression process(MetaExpression documentId) {
        String jsonDocument = getJson(documentId.getStringValue());
        Map<?, ?> value = parse(jsonDocument);

        return parseObject(value);
    }

    private Map<?, ?> parse(String jsonDocument) {
        try {
            return jsonParser.fromJson(jsonDocument, Map.class);
        } catch (JsonException e) {
            throw new RobotRuntimeException("Failed to parse json: " + e.getMessage(), e);
        }
    }

    private String getJson(String id) {
        try {
            return persistence.getJSON(id);
        } catch (DocumentNotFoundException e) {
            throw new RobotRuntimeException("Document [" + id + "] was not found", e);
        }
    }

}
