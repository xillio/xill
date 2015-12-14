package nl.xillio.xill.plugins.mongodb.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.mongodb.services.Connection;
import nl.xillio.xill.plugins.mongodb.services.ConnectionInfo;
import nl.xillio.xill.plugins.mongodb.services.ConnectionManager;


public class ConnectConstruct extends Construct {
    private final ConnectionManager connectionManager;

    @Inject
    public ConnectConstruct(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                (host, port, database, username, password) ->
                        process(host, port, database, username, password, context),
                new Argument("host", ATOMIC),
                new Argument("port", ATOMIC),
                new Argument("database", ATOMIC),
                new Argument("username", NULL, ATOMIC),
                new Argument("password", NULL, ATOMIC)
        );
    }

    MetaExpression process(MetaExpression hostExpression, MetaExpression portExpression, MetaExpression databaseExpression, MetaExpression usernameExpression, MetaExpression passwordExpression, ConstructContext context) {
        String host = hostExpression.getStringValue();
        int port = portExpression.getNumberValue().intValue();
        String database = databaseExpression.getStringValue();
        String username = usernameExpression.getStringValue();
        String password = passwordExpression.getStringValue();
        ConnectionInfo info = new ConnectionInfo(host, port, database, username, password);

        MetaExpression result = fromValue(getConnectionString(username, host, port, database));
        Connection connection = connectionManager.getConnection(context, info);
        result.storeMeta(Connection.class, connection);

        return result;
    }

    private String getConnectionString(String username, String host, int port, String database) {
        if (username == null) {
            return String.format("Mongo[%s:%d/%s]", host, port, database);
        }
        return String.format("Mongo[%s@%s:%d/%s]", username, host, port, database);
    }
}
