package nl.xillio.xill.plugins.mongodb.services;

/**
 * This exception is generally thrown when a connection to a server fails.
 */
public class ConnectionFailedException extends MongoDBPluginException {

    private final Connection connection;

    public ConnectionFailedException(String message, Connection connection, Throwable cause) {
        super(message, cause);

        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
