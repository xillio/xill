package nl.xillio.xill.plugins.mongodb.constructs;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.mongodb.services.FindOneAndReplaceOptionsFactory;
import org.bson.Document;

/**
 * This construct represents the <code>FindOneAndReplace()</code> method on MongoDB. That method is specific to the Java
 * API of MongoDB and therefore not documented in the online javascript API documentation. It replaces the
 * <code>FindAndModify()</code> method.
 *
 * <code>FindOneAndReplace()</code> in MongoDB replaces the first document returned by a <code>query</code> with a <code>replacement</code> document.
 *
 * @author Titus Nachbauer
 */
public class FindOneAndReplaceConstruct extends AbstractCollectionApiConstruct {
    private final FindOneAndReplaceOptionsFactory findOneAndReplaceOptionsFactory;

    @Inject
    public FindOneAndReplaceConstruct(FindOneAndReplaceOptionsFactory findOneAndReplaceOptionsFactory) {
        this.findOneAndReplaceOptionsFactory = findOneAndReplaceOptionsFactory;
    }

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("query", emptyObject(), OBJECT),
                new Argument("replacement", emptyObject(), OBJECT),
                new Argument("options", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
        Document filter = toDocument(arguments[0]);
        Document replacement = toDocument(arguments[1]);
        FindOneAndReplaceOptions options = findOneAndReplaceOptionsFactory.build(arguments[2]);

        Document result = collection.findOneAndReplace(filter, replacement, options);
        return toExpression(result);
    }
}
