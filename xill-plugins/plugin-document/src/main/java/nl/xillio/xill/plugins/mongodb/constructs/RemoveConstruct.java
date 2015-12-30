package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.bson.Document;

/**
 * This construct represents the update method on MongoDB.
 *
 * @author Titus Nachbauer
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.update/#db.collection.update">db.collection.update</a>
 */
public class RemoveConstruct extends AbstractCollectionApiConstruct {

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("query", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
        Document query = toDocument(arguments[0]);
        DeleteResult result = tryDeleteMany(collection, query);

        if (result.wasAcknowledged()) {
            return fromValue(result.getDeletedCount());
        } else {
            return fromValue(0);
        }
    }

    private DeleteResult tryDeleteMany(MongoCollection<Document> collection, Document query) {
        DeleteResult result;
        try {
            result = collection.deleteMany(query);
        } catch (MongoException e) {
            throw new RobotRuntimeException("Remove failed: " + e.getMessage(), e);
        }
        return result;
    }
}
