package nl.xillio.xill.plugins.mongodb.services;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionDeserializer;
import nl.xillio.xill.api.components.MetaExpressionSerializer;
import nl.xillio.xill.plugins.mongodb.data.MongoObjectId;
import org.bson.types.ObjectId;

/**
 * Provides a deserializer for a MongoObjectId.
 */
public class ObjectIdSerializer implements MetaExpressionSerializer, MetaExpressionDeserializer {

    @Override
    public MetaExpression parseObject(Object object) {
        if (!(object instanceof ObjectId)) {
            return null;
        }
        MetaExpression result = new AtomicExpression(object.toString());
        result.storeMeta(new MongoObjectId(object.toString()));
        return result;
    }

    @Override
    public Object extractValue(MetaExpression metaExpression) {
        MongoObjectId result = metaExpression.getMeta(MongoObjectId.class);
        if (result != null) {
            return result.getObjectId();
        }
        return null;
    }
}
