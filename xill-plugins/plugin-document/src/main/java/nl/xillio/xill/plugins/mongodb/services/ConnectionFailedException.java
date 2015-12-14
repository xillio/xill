package nl.xillio.xill.plugins.mongodb.services;

/**
 * This exception is generally thrown when a connection to a server fails.
 */
public class ConnectionFailedException extends MongoDBPluginException {

    public ConnectionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionFailedException(String message) {
        super(message);
    }
}
