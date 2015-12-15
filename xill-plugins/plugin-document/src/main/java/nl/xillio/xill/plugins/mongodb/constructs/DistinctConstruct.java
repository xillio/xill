package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import org.bson.BsonValue;
import org.bson.Document;

/**
 * This construct represents the distinct method on MongoDB.
 *
 * @author Thomas Biesaart
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.distinct/#db.collection.distinct">db.collection.distinct</a>
 */
public class DistinctConstruct extends AbstractCollectionApiConstruct {

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("field", ATOMIC),
                new Argument("query", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection) {
        String field = arguments[0].getStringValue();
        Document query = getQuery(arguments[1]);

        DistinctIterable<BsonValue> mongoResult = collection.distinct(field, BsonValue.class).filter(query);

        return fromValueRaw(mongoResult, collection, arguments);
    }
}
