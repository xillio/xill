package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import org.bson.Document;

/**
 * This construct represents the getIndexes method on MongoDB
 *
 * @author Titus Nachbauer
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.getindexes/#db.collection.getIndexes">db.collection.getIndexes</a>
 */
public class ListIndexesConstruct extends AbstractCollectionApiConstruct {

    @Override
    protected Argument[] getApiArguments() {
        return null;
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection) {

        MongoIterable<Document> result = collection.listIndexes();

        return fromValue(result, collection);
    }
}
