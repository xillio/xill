package nl.xillio.xill.api.errors;

/**
 * This {@link Exception} is generally thrown when something goes wrong during processing.
 */
public class RobotRuntimeException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     *
     * @param message the message
     */
    public RobotRuntimeException(final String message) {
        this(message, null);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public RobotRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
