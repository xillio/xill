package nl.xillio.xill.plugins.document.data;

import nl.xillio.udm.builders.DecoratorBuilder;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for mapping an expression to a builder.
 *
 * @author Thomas Biesaart
 */
@SuppressWarnings("unchecked")
public class MetaExpressionUDMMapper implements UDMDocument {
    private final LinkedHashMap<String, MetaExpression> expression;

    public MetaExpressionUDMMapper(MetaExpression expression) {
        if (expression.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Can only map UDM documents from an object");
        }

        this.expression = (LinkedHashMap<String, MetaExpression>) expression.getValue();
    }

    @Override
    public void applyTo(DocumentBuilder builder) throws ValidationException {
        builder.contentType(getContentType());
        processHistory(builder.target(), getRequired(expression, "target"));
        processHistory(builder.source(), getRequired(expression, "source"));
    }

    private void processHistory(DocumentHistoryBuilder history, MetaExpression expression) throws ValidationException {
        Map<String, MetaExpression> convertedExpression = getAsObject(expression, "Document history");

        history.action(0);

        // Remove all revisions
        history.versions().forEach(history::removeRevision);

        // Parse current
        processRevision(history.current(), getRequired(convertedExpression, "current"));

        // Parse other revisions
        MetaExpression versions = getRequired(convertedExpression, "versions");
        if (versions.getType() != ExpressionDataType.LIST) {
            throw new ValidationException("Property `versions` on document revision must be a list");
        }
        for (MetaExpression version : (List<MetaExpression>) versions.getValue()) {
            Map<String, MetaExpression> versionMap = getAsObject(version, "Document revision");
            MetaExpression versionProperty = getRequired(versionMap, "version");

            processRevision(history.revision(versionProperty.getStringValue()), version);
        }
    }

    private void processRevision(DocumentRevisionBuilder revision, MetaExpression expression) throws ValidationException {
        Map<String, MetaExpression> convertedExpression = getAsObject(expression, "Document revision");

        revision.version("myVersion");

        for (Map.Entry<String, MetaExpression> entry : convertedExpression.entrySet()) {
            switch (entry.getKey()) {
                case "order":
                    break;
                case "version":
                    revision.version(entry.getValue().getStringValue());
                    break;
                default:
                    parseDecorator(revision.decorator(entry.getKey()), entry.getValue());
                    break;
            }
        }

        revision.commit();
    }

    private void parseDecorator(DecoratorBuilder decorator, MetaExpression expression) throws ValidationException {
        Map<String, MetaExpression> convertedExpression = getAsObject(expression, "Decorator");

        for (Map.Entry<String, MetaExpression> entry : convertedExpression.entrySet()) {
            decorator.field(entry.getKey(), parseFieldValue(entry.getValue()));
        }

        decorator.commit();
    }

    private Object parseFieldValue(MetaExpression value) {
        return MetaExpression.extractValue(value);
    }

    private Map<String, MetaExpression> getAsObject(MetaExpression expression, String type) throws ValidationException {
        if (expression == null) {
            throw new ValidationException("Could not parse null value to a " + type);
        }
        if (expression.getType() != ExpressionDataType.OBJECT) {
            throw new ValidationException(type + " must be an object. Could not parse [" + expression + "]");
        }
        return (Map<String, MetaExpression>) expression.getValue();
    }

    private MetaExpression getRequired(Map<String, MetaExpression> map, String value) throws ValidationException {
        MetaExpression result = map.get(value);

        if (result == null) {
            throw new ValidationException("Could not find requried property " + value + " in [" + map + "]");
        }

        return result;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    @Override
    public String getId() {
        MetaExpression idExpr = expression.get("_id");
        return idExpr == null ? null : idExpr.getStringValue();
    }

    public String getContentType() {
        MetaExpression value = expression.get("contenttype");
        return value == null ? null : value.getStringValue();
    }
}
