package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Singleton;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.services.xill.DocumentQueryBuilder;
import org.bson.Document;

import javax.inject.Inject;
import java.util.Map;

/**
 * This class is responsible for the conversion of xill objects to Document objects.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
@Singleton
public class MongoConverter implements DocumentQueryBuilder {

    private final ObjectIdSerializer objectIdSerializer;

    @Inject
    public MongoConverter(ObjectIdSerializer objectIdSerializer) {
        this.objectIdSerializer = objectIdSerializer;
    }


    public Document parse(MetaExpression expression) {

        if (expression.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Can only parse OBJECT to a query");
        }

        Map<String, Object> value = MetaExpression.extractValue(expression, objectIdSerializer);

        return new Document(value);
    }

    public MetaExpression parse(Document document) {
        return MetaExpression.parseObject(document, objectIdSerializer);
    }

    @Override
    public Document parseQuery(MetaExpression expression) {
        return parse(expression);
    }
}
