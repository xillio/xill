package nl.xillio.xill.plugins.mongodb;

/**
 * Created by Thomas Biesaart on 14-12-2015.
 */
public class NoSuchConnectionException extends MongoDBPluginException {
    public NoSuchConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchConnectionException(String message) {
        super(message);
    }
}
