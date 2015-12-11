package nl.xillio.xill.plugins.document.exceptions;

/**
 * This Exception is generally thrown when a constraint in the document model has been violated.
 *
 * @author Thomas Biesaart
 */
public class ValidationException extends DocumentException {

    /**
     * Create a new ValidationException
     *
     * @param message the message to describe the exception with
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Create a new ValidationException with a cause
     *
     * @param message the message to describe the exception with
     * @param cause   the cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
