package nl.xillio.xill.plugins.mongodb.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.mongodb.data.MongoObjectId;

/**
 * Creates a Mongo ObjectId from a 24 character hexadecimal string
 */
public class ObjectIdConstruct extends Construct{
    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor((string) -> {
            return process(string);
        }, new Argument("string", ATOMIC));
    }

    private MetaExpression process(MetaExpression string) {
        MetaExpression result = fromValue(string.getStringValue());
        MongoObjectId objectId = new MongoObjectId(result.getStringValue());
        result.storeMeta(objectId);
        return result;
    }
}
