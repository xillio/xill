package nl.xillio.xill.plugins.mongodb.constructs;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.mongodb.services.FindOneAndUpdateOptionsFactory;
import org.bson.Document;

/**
 * This construct represents the <code>FindOneAndUpdate()</code> method on MongoDB. That method is specific to the Java
 * API of MongoDB and therefore not documented in the online javascript API documentation. It replaces the
 * <code>FindAndModify()</code> method.
 * <p>
 * <code>FindOneAndUpdate()</code> in MongoDB updates the first document returned by a <code>query</code> with an <code>update</code> document.
 *
 * @author Titus Nachbauer
 */
public class FindOneAndUpdateConstruct extends AbstractCollectionApiConstruct {
    private final FindOneAndUpdateOptionsFactory findOneAndUpdateOptionsFactory;

    @Inject
    public FindOneAndUpdateConstruct(FindOneAndUpdateOptionsFactory findOneAndUpdateOptionsFactory) {
        this.findOneAndUpdateOptionsFactory = findOneAndUpdateOptionsFactory;
    }

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("query", emptyObject(), OBJECT),
                new Argument("update", emptyObject(), OBJECT),
                new Argument("options", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
        Document filter = toDocument(arguments[0]);
        Document update = toDocument(arguments[1]);
        FindOneAndUpdateOptions options = findOneAndUpdateOptionsFactory.build(arguments[2]);

        Document result = collection.findOneAndUpdate(filter, update, options);
        return toExpression(result);
    }
}
