package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.bson.Document;

/**
 * This construct represents the dropIndex method on MongoDB.
 *
 * @author Thomas Biesaart
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.dropIndex/#db.collection.dropIndex">db.collection.dropIndex</a>
 */
public class DropIndexConstruct extends AbstractCollectionApiConstruct {


    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("index", ATOMIC, OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection) {

        switch (arguments[0].getType()) {
            case ATOMIC:
                collection.dropIndex(arguments[0].getStringValue());
                break;
            case OBJECT:
                Document index = toDocument(arguments[0]);
                collection.dropIndex(index);
                break;
            default:
                // This should never happen but it is there just in case
                throw new RobotRuntimeException("Invalid Input! Please contact the development team.");
        }

        return NULL;
    }
}
