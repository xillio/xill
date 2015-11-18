package nl.xillio.xill.plugins.document.exceptions;

/**
 * This exception represents the base exception for the {@link nl.xillio.xill.plugins.document.DocumentXillPlugin}.
 *
 * @author Thomas Biesaart
 */
public abstract class DocumentException extends Exception {

    /**
     * Create a new DocumentException
     *
     * @param message the message to describe the exception with
     */
    public DocumentException(String message) {
        super(message);
    }

    /**
     * Create a new DocumentException with a cause
     *
     * @param message the message to describe the exception with
     * @param cause   the cause
     */
    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
