package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Singleton;
import com.mongodb.Mongo;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.document.services.xill.DocumentQueryBuilder;
import nl.xillio.xill.plugins.mongodb.data.MongoObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Map;

/**
 * This class is responsible for the conversion of xill objects to Document objects.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
@Singleton
public class MongoConverter implements DocumentQueryBuilder {

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

    public MetaExpression parse(MongoObjectId objectId) {
        return MetaExpression.parseObject(objectId, new ObjectIdDeserializer());
    }

    @Override
    public Document parseQuery(MetaExpression expression) {
        return parse(expression);
    }
}
