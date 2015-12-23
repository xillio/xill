package nl.xillio.xill.plugins.mongodb.constructs;

import com.google.inject.Inject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.mongodb.services.InsertManyOptionsFactory;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This construct represents the insert method on MongoDB.
 *
 * @author Titus Nachbauer
 * @see <a href="https://docs.mongodb.org/v3.0/reference/method/db.collection.insert/#db.collection.insert">db.collection.insert</a>
 */
public class InsertConstruct extends AbstractCollectionApiConstruct {

    private final InsertManyOptionsFactory insertManyOptionsFactory;

    @Inject
    public InsertConstruct(InsertManyOptionsFactory insertManyOptionsFactory) {
        this.insertManyOptionsFactory = insertManyOptionsFactory;
    }

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[] {
                new Argument("documents", emptyList(), LIST),
                new Argument("options", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
        List<MetaExpression> list = arguments[0].getValue();
        InsertManyOptions options = insertManyOptionsFactory.build(arguments[1]);

        
        List<Document>documents = list.stream().map(this::toDocument).collect(Collectors.toList());
        tryInsertMany(collection, documents, options);
        return NULL;
    }

    private void tryInsertMany(MongoCollection<Document> collection, List<Document> documents, InsertManyOptions options) {
        try {
            collection.insertMany(documents, options);
        } catch (MongoException e) {
            throw new RobotRuntimeException("Insert failed: " + e.getMessage(), e);
        }
    }
}
