package nl.xillio.xill.plugins.document.exceptions;

/**
 * This Exception is generally thrown when the {@link nl.xillio.xill.plugins.document.services.XillUDMService} fails to persist a document.
 *
 * @author Thomas Biesaart
 */
public class PersistException extends Exception {

    /**
     * Create a new PersistException
     *
     * @param message the message to describe the exception with
     */
    public PersistException(String message) {
        super(message);
    }

    /**
     * Create a new PersistException with a cause
     *
     * @param message the message to describe the exception with
     * @param cause   the cause
     */
    public PersistException(String message, Throwable cause) {
        super(message, cause);
    }
}
