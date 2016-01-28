package nl.xillio.xill.plugins.mongodb.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoTimeoutException;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.mongodb.ConnectionFailedException;
import nl.xillio.xill.plugins.mongodb.NoSuchConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * This class is responsible for the management of MongoDB {@link Connection connections}.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
@Singleton
public class ConnectionManager {
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * This cache keeps track of the last created connection in a running instance.
     */
    private final Map<UUID, Connection> connectionCache = new ConcurrentHashMap<>();
    /**
     * This map keeps track of the connection info used to build a connection.
     */
    private final Map<Connection, ConnectionInfo> connectionInfoMap = new ConcurrentHashMap<>();
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
    public Connection getConnection(ConstructContext context, ConnectionInfo info) throws ConnectionFailedException {
        synchronized (connectionCache) {
            Connection connection = getOpen(context);

            // Check if the connection info is different
            if (connection != null) {
                ConnectionInfo initInfo = connectionInfoMap.get(connection);
                if (!initInfo.equals(info)) {
                    // Pretend we don't have a connection yet because the connection info is different
                    connection = null;
                }
            }

            if (connection == null) {
                LOGGER.info("Creating connection for {}", info);
                // We have to create a new connection
                connection = connectionFactory.build(info);

                // Validate the connection
                validate(connection);

                connectionCache.put(context.getCompilerSerialId(), connection);
                connectionInfoMap.put(connection, info);

                // Add a listener to close the connection
                Connection connectionReference = connection;
                context.addRobotStoppedListener(action -> {
                    connectionReference.close();
                    clean();
                });
            }

            return connection;
        }
    }

    private void validate(Connection connection) throws ConnectionFailedException {
        try {
            connection.requireValid();
        } catch (MongoTimeoutException e) {
            throw new ConnectionFailedException("Could not connect to mongodb", e);
        }
    }

    /**
     * Clean the manager maps to make them ready for garbage collection.
     */
    private void clean() {
        LOGGER.info("Cleaning up connections");

        // Remove all closed elements from the cache
        List<UUID> closed = connectionCache.entrySet().stream()
                .filter(e -> e.getValue().isClosed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        closed.forEach(connectionCache::remove);

        // Remove all connection info mappings for connections that do not exist
        Collection<Connection> availableConnections = connectionCache.values();

        List<Connection> toBeRemoved = connectionInfoMap.keySet().stream()
                .filter(c -> !availableConnections.contains(c))
                .collect(Collectors.toList());

        toBeRemoved.forEach(connectionInfoMap::remove);

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
        Connection connection = connectionCache.get(context.getCompilerSerialId());
        if (connection == null || connection.isClosed()) {
            return null;
        }
        return connection;
    }
}
