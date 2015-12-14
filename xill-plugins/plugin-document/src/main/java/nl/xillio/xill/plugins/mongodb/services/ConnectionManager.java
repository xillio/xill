package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class is responsible for the management of MongoDB {@link Connection connections}.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
@Singleton
public class ConnectionManager {
    private final Map<RobotID, Connection> connectionCache = new ConcurrentHashMap<>();
    private final ConnectionFactory connectionFactory;

    /**
     * Create a new ConnectionManager.
     *
     * @param connectionFactory the factory that should be used to create connections
     */
    @Inject
    public ConnectionManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Get an open cached connection or create if it does not exist.
     *
     * @param context the context for the connection
     * @param info    the connection information
     * @return the connection
     * @see ConnectionManager#getConnection(ConstructContext)
     */
    public Connection getConnection(ConstructContext context, ConnectionInfo info) {
        synchronized (connectionCache) {

            Connection connection = getOpen(context);

            if (connection == null) {
                connection = connectionFactory.build(info);
                connectionCache.put(context.getRootRobot(), connection);
            }

            return connection;

        }
    }

    /**
     * Get a cached connection.
     *
     * @param context the context for which to fetch the connection
     * @return the connection
     * @throws NoSuchConnectionException if no connection is available
     */
    public Connection getConnection(ConstructContext context) throws NoSuchConnectionException {
        Connection connection = getOpen(context);

        if (connection == null) {
            throw new NoSuchConnectionException("No connection found for the given context");
        }

        return connection;
    }

    private Connection getOpen(ConstructContext context) {
        Connection connection = connectionCache.get(context.getRootRobot());
        if (connection == null || connection.isClosed()) {
            return null;
        }
        return connection;
    }
}
