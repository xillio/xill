package nl.xillio.xill.plugins.mongodb.services;


import com.mongodb.MongoClient;
import nl.xillio.xill.api.data.MetadataExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a connection with a {@link MongoClient}.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class Connection implements MetadataExpression {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MongoClient client;
    private boolean closed;

    /**
     * Create a connection.
     *
     * @param client the client to wrap
     */
    public Connection(MongoClient client) {
        this.client = client;
    }

    /**
     * Close the connection.
     */
    public void close() {
        if (closed) {
            return;
        }
        LOGGER.info("Closing connection");
        client.close();
        closed = true;
    }

    /**
     * Check whether connection is closed.
     *
     * @return true if and only if {@link Connection#close()} has been called
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Get the client for the connection.
     *
     * @return the client
     */
    public MongoClient getClient() {
        return client;
    }
}
