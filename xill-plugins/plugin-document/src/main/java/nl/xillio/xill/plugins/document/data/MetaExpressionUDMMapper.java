package nl.xillio.xill.plugins.document.data;

import nl.xillio.udm.builders.DecoratorBuilder;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is responsible for mapping an expression to a builder.
 *
 * @author Thomas Biesaart
 */
public class MetaExpressionUDMMapper implements UDMDocument {
    private final LinkedHashMap<String, MetaExpression> expression;

    @SuppressWarnings("unchecked")
    public MetaExpressionUDMMapper(MetaExpression expression) {
        if (expression.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Can only map UDM documents from an object");
        }

        this.expression = (LinkedHashMap<String, MetaExpression>) expression.getValue();
    }

    @Override
    public void applyTo(DocumentBuilder builder) throws ValidationException {
        builder.contentType(getContentType());
        processHistory(builder.target(), expression.get("target"));
        processHistory(builder.source(), expression.get("source"));
    }

    private void processHistory(DocumentHistoryBuilder history, MetaExpression expression) throws ValidationException {
        Map<String, MetaExpression> convertedExpression = getAsObject(expression, "Document history");

        history.action(0);

        // Remove all revisions
        history.versions().forEach(history::removeRevision);

        // Parse current
        processRevision(history.current(), convertedExpression.get("current"));
    }

    private void processRevision(DocumentRevisionBuilder revision, MetaExpression expression) throws ValidationException {
        Map<String, MetaExpression> convertedExpression = getAsObject(expression, "Document revision");

        revision.version("myVersion");

        for (Map.Entry<String, MetaExpression> entry : convertedExpression.entrySet()) {
            if ("version".equals(entry.getKey())) {
                revision.version(entry.getValue().getStringValue());
            } else {
                parseDecorator(revision.decorator(entry.getKey()), entry.getValue());
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

    @SuppressWarnings("unchecked")
    private Map<String, MetaExpression> getAsObject(MetaExpression expression, String type) throws ValidationException {
        if (expression.getType() != ExpressionDataType.OBJECT) {
            throw new ValidationException(type + " must be an object. Could not parse [" + expression + "]");
        }
        return (Map<String, MetaExpression>) expression.getValue();
    }

    @Override
    public boolean isNew() {
        MetaExpression idExpr = expression.get("_id");
        return idExpr == null || idExpr.isNull();
    }

    public String getContentType() {
        MetaExpression value = expression.get("contenttype");
        return value == null ? null : value.getStringValue();
    }
}
