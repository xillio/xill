package nl.xillio.xill.plugins.mongodb.constructs;

import com.google.inject.Inject;
import com.mongodb.MongoQueryException;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.mongodb.NoSuchConnectionException;
import nl.xillio.xill.plugins.mongodb.services.BsonValueConverter;
import nl.xillio.xill.plugins.mongodb.services.Connection;
import nl.xillio.xill.plugins.mongodb.services.ConnectionManager;
import nl.xillio.xill.plugins.mongodb.services.MongoConverter;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonValue;
import org.bson.Document;

abstract class AbstractCollectionApiConstruct extends Construct {
    private ConnectionManager connectionManager;
    private MongoConverter mongoConverter;
    private BsonValueConverter bsonValueConverter;

    @Inject
    void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Inject
    void setMongoConverter(MongoConverter mongoConverter) {
        this.mongoConverter = mongoConverter;
    }

    @Inject
    void setBsonValueConverter(BsonValueConverter bsonValueConverter) {
        this.bsonValueConverter = bsonValueConverter;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        Argument[] apiArguments = getApiArguments();
        Argument[] arguments = new Argument[apiArguments.length + 2];

        arguments[0] = new Argument("collectionName", ATOMIC);
        arguments[arguments.length - 1] = new Argument("database", NULL, ATOMIC);

        System.arraycopy(apiArguments, 0, arguments, 1, arguments.length - 2);

        return new ConstructProcessor(
                args -> process(args, context),
                arguments
        );
    }

    MetaExpression process(MetaExpression[] arguments, ConstructContext context) {
        // Parse arguments
        MetaExpression[] customArguments = new MetaExpression[arguments.length - 2];
        System.arraycopy(arguments, 1, customArguments, 0, arguments.length - 2);

        // Fetch Connection
        Connection connection = getConnection(context, arguments[arguments.length - 1]);

        // Fetch Collection
        String collectionName = arguments[0].getStringValue();
        MongoCollection<Document> collection = connection.getDatabase().getCollection(collectionName);

        try {
            return process(customArguments, collection, context);
        } catch (MongoQueryException e) {
            throw new RobotRuntimeException("Could not parse query: " + e.getErrorMessage(), e);
        } catch (MongoSocketOpenException e) {
            throw new RobotRuntimeException("Could not connect to the database at " + e.getServerAddress() + "\nError: " + e.getMessage(), e);
        }
    }

    protected abstract Argument[] getApiArguments();

    abstract MetaExpression process(MetaExpression[] arguments, MongoCollection<Document> collection, ConstructContext context);

    private Connection getConnection(ConstructContext context, MetaExpression connectionExpression) {
        if (connectionExpression.isNull()) {
            return getConnection(context);
        }

        Connection connection = connectionExpression.getMeta(Connection.class);
        if (connection == null) {
            throw new RobotRuntimeException("The passed database parameter is not a valid MongoDB database. Please connect using the Mongo.connect construct");
        }
        return connection;
    }

    private Connection getConnection(ConstructContext context) {
        try {
            return connectionManager.getConnection(context);
        } catch (NoSuchConnectionException e) {
            throw new RobotRuntimeException("No active connection found for this robot. Please use Mongo.connect to create a connection", e);
        }
    }

    protected Document toDocument(MetaExpression query) {
        return mongoConverter.parse(query);
    }

    protected MetaExpression toExpression(Document document) {
        return mongoConverter.parse(document);
    }

    /**
     * Create a result expression from a MongoIterable.
     *
     * @param source    the iterable
     * @param arguments the arguments that should be included in the string representation
     * @return the expression
     */
    protected MetaExpression fromValue(MongoIterable<Document> source, MongoCollection<Document> collection, MetaExpression... arguments) {

        MetaExpression result = fromValue(String.format("db.%s.%s(%s)", collection.getNamespace().getCollectionName(), getName(), StringUtils.join(arguments, ",")));
        result.storeMeta(new MetaExpressionIterator<>(
                source.iterator(),
                this::toExpression
        ));

        return result;
    }

    /**
     * Create a result expression from a MongoIterable.
     *
     * @param source    the iterable
     * @param arguments the arguments that should be included in the string representation
     * @return the expression
     */
    protected MetaExpression fromValueRaw(MongoIterable<BsonValue> source, MongoCollection<Document> collection, MetaExpression... arguments) {

        MetaExpression result = fromValue(String.format("db.%s.%s(%s)", collection.getNamespace().getCollectionName(), getName(), StringUtils.join(arguments, ",")));
        result.storeMeta(new MetaExpressionIterator<>(
                source.iterator(),
                bsonValueConverter::convert
        ));

        return result;
    }

}
