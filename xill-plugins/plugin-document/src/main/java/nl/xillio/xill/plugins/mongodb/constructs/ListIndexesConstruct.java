package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import org.bson.Document;

/**
 * This construct represents the getIndexes method on MongoDB (implemented in the Java API as <code>listIndexes()</code>)
 *
 * @author Titus Nachbauer
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.getindexes/#db.collection.getIndexes">db.collection.getIndexes</a>
 */
public class ListIndexesConstruct extends AbstractCollectionApiConstruct {

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {

        MongoIterable<Document> result = collection.listIndexes();

        return fromValue(result, collection);
    }
}