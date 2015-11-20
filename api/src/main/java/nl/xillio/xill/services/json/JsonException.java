package nl.xillio.xill.services.json;

/**
 * This exception is generally thrown when something goes wrong in the json parsers.
 */
public class JsonException extends Exception {
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
