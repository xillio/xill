package nl.xillio.xill.plugins.mongodb.constructs;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.mongodb.services.UpdateOptionsFactory;
import org.bson.Document;

/**
 *
 */
public class UpdateConstruct extends AbstractCollectionApiConstruct{

    private final UpdateOptionsFactory updateOptionsFactory;

    @Inject
    public UpdateConstruct(UpdateOptionsFactory updateOptionsFactory) {
        this.updateOptionsFactory = updateOptionsFactory;
    }

    @Override
    protected Argument[] getApiArguments() {
        return new Argument[] {
                new Argument("query", emptyObject(), OBJECT),
                new Argument("update", emptyObject(), OBJECT),
                new Argument("options", emptyObject(), OBJECT)
        };
    }

    @Override
    MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context) {
        Document filter = toDocument(arguments[0]);
        Document update = toDocument(arguments[1]);
        UpdateOptions options = updateOptionsFactory.build(arguments[2]);

        UpdateResult result = tryUpdateMany(collection, filter, update, options);
        if (result.wasAcknowledged() && result.isModifiedCountAvailable()) {
            return fromValue(result.getModifiedCount());
        } else if (result.wasAcknowledged() && !result.isModifiedCountAvailable()) {
            context.getRootLogger().warn("Update succeeded, but update count is unavailable, due to server with Mongo version prior to 2.6");
            return fromValue(0);
        } else {
            return fromValue(0);
        }
    }

    private UpdateResult tryUpdateMany(MongoCollection<Document> collection, Document filter, Document update, UpdateOptions options) {
        UpdateResult result;
        try {
            result = collection.updateMany(filter, update, options);
        } catch (com.mongodb.MongoException e) {
            throw new RobotRuntimeException("Update failed: " + e.getMessage(), e);
        }
        return result;
    }
}
