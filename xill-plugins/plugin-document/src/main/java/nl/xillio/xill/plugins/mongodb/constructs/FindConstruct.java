package nl.xillio.xill.plugins.mongodb.constructs;

import com.google.inject.Inject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.mongodb.services.FindIterableBuilder;
import org.bson.Document;

/**
 * This construct represents the find method on MongoDB.
 *
 * @author Thomas Biesaart
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.find/#db.collection.find">db.collection.find</a>
 */
public class FindConstruct extends AbstractCollectionApiConstruct {

    private FindIterableBuilder findIterableBuilder;

    @Inject
    void setFindIterableBuilder(FindIterableBuilder findIterableBuilder) {
        this.findIterableBuilder = findIterableBuilder;
    }

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("query", emptyObject(), OBJECT),
                new Argument("projection", emptyObject(), OBJECT),
                new Argument("sort", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
        FindIterable<Document> mongoResult = findIterableBuilder.getIterable(collection, arguments);
        return fromValue(mongoResult, collection, arguments);
    }
}
