package nl.xillio.xill.plugins.document.services;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.data.UDMConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a pool that will manage connections to the udm.
 */
@Singleton
public class ConnectionPool {
    private final List<UDMConnection> connections = new ArrayList<>();
    private final ConnectionFactory connectionFactory;

    @Inject
    public ConnectionPool(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Close all connections.
     */
    public void clear() {
        connections.forEach(UDMConnection::close);
        connections.clear();
    }

    /**
     * Get an existing connection if it is available, otherwise create a new one.
     *
     * @param identity the identity of the connection
     * @return the connection
     */
    public UDMService get(String identity) {
        Optional<UDMConnection> connection = connections
                .stream()
                .filter(c -> c.getIdentity().equals(identity))
                .findAny();

        if (connection.isPresent()) {
            return connection.get().getUdm();
        }

        return createConnection(identity);
    }

    /**
     * Get the number of connections in this pool.
     *
     * @return the number of connections
     */
    public int size() {
        return connections.size();
    }

    private UDMService createConnection(String identity) {
        UDMConnection connection = connectionFactory.build(identity);
        connections.add(connection);
        return connection.getUdm();
    }
}
