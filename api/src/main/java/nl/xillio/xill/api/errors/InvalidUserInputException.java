package nl.xillio.xill.api.errors;

/**
 * The specific exception whenever the input from the user is not valid
 */
public class InvalidUserInputException extends RobotRuntimeException {

    protected final String reason;

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param reason the short reason for this exception
     * @param actualInput the representation of the input value that was not valid
     * @param expectedInput the explanation of what is correct input (noun with article)
     * @param example the example of correct usage in free form
     * @param cause the cause
     */
    public InvalidUserInputException(final String reason, final String actualInput, final String expectedInput, final String example, final Throwable cause) {
        super(reason + "\n\n== Actual Input ==\n" + actualInput + "\n\n== Expected Input ==\n" + expectedInput + (example == null ? "" : "\n\n== Example ==\n"+example), cause);
        this.reason = reason;
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param reason the short reason for this exception
     * @param actualInput the representation of the input value that was not valid
     * @param expectedInput the explanation of what is correct input (noun with article)
     * @param example the example of correct usage in free form
     */
    public InvalidUserInputException(final String reason, final String actualInput, final String expectedInput, final String example) {
        this(reason, actualInput, expectedInput, example, null);
    }

    /**
     * Constructs a new exception with the specified detail message and cause without example.
     *
     * @param reason the short reason for this exception
     * @param actualInput the representation of the input value that was not valid
     * @param expectedInput the explanation of what is correct input (noun with article)
     * @param cause the cause
     */
    public InvalidUserInputException(final String reason, final String actualInput, final String expectedInput, final Throwable cause) {
        this(reason, actualInput, expectedInput, null, cause);
    }
    /**
     * Constructs a new exception with the specified detail message without example.
     *
     * @param reason the short reason for this exception
     * @param actualInput the representation of the input value that was not valid
     * @param expectedInput the explanation of what is correct input (noun with article)
     */
    public InvalidUserInputException(final String reason, final String actualInput, final String expectedInput) {
        this(reason, actualInput, expectedInput, null, null);
    }

    @Override
    public String getShortMessage() {
        return reason;
    }
}