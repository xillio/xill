package nl.xillio.xill.plugins.mongodb.constructs;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.mongodb.services.FindOneAndDeleteOptionsFactory;
import org.bson.Document;

/**
 * This construct represents the <code>FindOneAndDelete()</code> method on MongoDB. That method is specific to the Java
 * API of MongoDB and therefore not documented in the online javascript API documentation. It replaces the
 * <code>FindAndModify()</code> method.
 *
 * <code>FindOneAndDelete()</code> in MongoDB deletes the first document returned by <code>query</code>.
 *
 * @author Titus Nachbauer
 */
public class FindOneAndDeleteConstruct extends AbstractCollectionApiConstruct {
    private final FindOneAndDeleteOptionsFactory findOneAndDeleteOptionsFactory;

    @Inject
    public FindOneAndDeleteConstruct (FindOneAndDeleteOptionsFactory findOneAndDeleteOptionsFactory) {
        this.findOneAndDeleteOptionsFactory = findOneAndDeleteOptionsFactory;
    }

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[] {
                new Argument("query", emptyObject(), OBJECT),
                new Argument("options", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
        Document filter = toDocument(arguments[0]);

        FindOneAndDeleteOptions options = findOneAndDeleteOptionsFactory.build(arguments[1]);

        Document result = collection.findOneAndDelete(filter, options);
        return toExpression(result);
    }
}
