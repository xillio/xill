package nl.xillio.xill.plugins.mongodb.constructs;


import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import org.bson.Document;

/**
 * This construct represents the count method on MongoDB.
 *
 * @author Thomas Biesaart
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.count/#db.collection.count">db.collection.count</a>
 */
public class CountConstruct extends AbstractCollectionApiConstruct {
    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("query", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection) {
        Document query = toDocument(arguments[0]);

        long count = collection.count(query);

        return fromValue(count);
    }
}
