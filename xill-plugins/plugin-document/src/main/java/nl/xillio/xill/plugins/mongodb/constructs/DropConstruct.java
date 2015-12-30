package nl.xillio.xill.plugins.mongodb.constructs;

import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import org.bson.Document;

/**
 * This construct represents the drop method on MongoDB.
 *
 * @author Thomas Biesaart
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.drop/#db.collection.drop">db.collection.drop</a>
 */
public class DropConstruct extends AbstractCollectionApiConstruct {


    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {

        collection.drop();

        return NULL;
    }
}
