package nl.xillio.xill.plugins.mongodb;

/**
 * Created by Thomas Biesaart on 14-12-2015.
 */
public class MongoDBPluginException extends Exception {
    public MongoDBPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoDBPluginException(String message) {
        super(message);
    }
}
