package nl.xillio.xill.api.errors;

/**
 * This {@link Exception} is generally thrown when an unimplemented part of the software is called.
 */
public class NotImplementedException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     *
     * @param message the message
     */
    public NotImplementedException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public NotImplementedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
