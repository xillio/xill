package nl.xillio.xill.plugins.mongodb.services;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionDeserializer;
import nl.xillio.xill.plugins.mongodb.data.MongoObjectId;

/**
 * Deserializer for a MongoObjectId
 */
public class ObjectIdDeserializer implements MetaExpressionDeserializer {

    @Override
    public MetaExpression parseObject(Object object) {
        MetaExpression result = new AtomicExpression(object.toString());
        result.storeMeta(new MongoObjectId(object.toString()));
        return result;
    }
}
