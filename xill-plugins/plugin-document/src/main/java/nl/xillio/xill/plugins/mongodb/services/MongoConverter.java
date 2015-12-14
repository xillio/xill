package nl.xillio.xill.plugins.mongodb.services;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.services.xill.DocumentQueryBuilder;
import org.bson.Document;

import java.util.Map;

/**
 * This class is responsible for the conversion of xill objects to Document objects.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class MongoConverter implements DocumentQueryBuilder {

    @SuppressWarnings("unchecked")
    public Document parse(MetaExpression expression) {

        if (expression.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Can only parse OBJECT to a query");
        }

        Map<String, Object> value = MetaExpression.extractValue(expression);

        return new Document(value);
    }

    public MetaExpression parse(Document document) {
        return MetaExpression.parseObject(document);
    }

    @Override
    public Document parseQuery(MetaExpression expression) {
        return parse(expression);
    }
}