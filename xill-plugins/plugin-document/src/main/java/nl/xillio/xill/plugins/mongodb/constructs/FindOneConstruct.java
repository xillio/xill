package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import org.bson.Document;

/**
 * This construct represents the findOne method on MongoDB.
 *
 * @author Thomas Biesaart
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.findOne/#db.collection.findOne">db.collection.findOne</a>
 */
public class FindOneConstruct extends AbstractCollectionApiConstruct {


    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("query", emptyObject(), OBJECT),
                new Argument("projection", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection) {
        Document filter = toDocument(arguments[0]);
        Document projection = toDocument(arguments[1]);

        FindIterable<Document> mongoResult = collection.find(filter).projection(projection);
        return toExpression(mongoResult.first());
    }
}
