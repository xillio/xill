package nl.xillio.xill.plugins.mongodb;

/**
 * This exception is generally thrown when a connection to a server fails.
 */
public class ConnectionFailedException extends MongoDBPluginException {

    public ConnectionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
