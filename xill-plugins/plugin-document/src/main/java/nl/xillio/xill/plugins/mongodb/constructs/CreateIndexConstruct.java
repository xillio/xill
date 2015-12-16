package nl.xillio.xill.plugins.mongodb.constructs;


import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.plugins.mongodb.services.IndexOptionsFactory;
import org.bson.Document;

/**
 * This construct represents the createIndex method on MongoDB.
 *
 * @author Thomas Biesaart
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.createIndex/#db.collection.createIndex">db.collection.createIndex</a>
 */
public class CreateIndexConstruct extends AbstractCollectionApiConstruct {
    private final IndexOptionsFactory indexOptionsFactory;

    @Inject
    public CreateIndexConstruct(IndexOptionsFactory indexOptionsFactory) {
        this.indexOptionsFactory = indexOptionsFactory;
    }


    @Override
    protected Argument[] getApiArguments() {
        return new Argument[]{
                new Argument("keys", OBJECT),
                new Argument("options", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection) {
        Document keys = toDocument(arguments[0]);

        IndexOptions options = indexOptionsFactory.build(arguments[1]);
        String result = collection.createIndex(keys,options);
        return fromValue(result);
    }
}
