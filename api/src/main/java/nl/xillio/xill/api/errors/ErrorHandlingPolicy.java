package nl.xillio.xill.api.errors;

/**
 * This interface represents a class that defines how to handle errors and exceptions in robots.
 */
public interface ErrorHandlingPolicy {
    /**
     * Be presented the an exception caused inside a robot
     *
     * @param e the throwable that should be handled
     * @throws RobotRuntimeException when the policy decides to stop processing
     */
    public void handle(final Throwable e) throws RobotRuntimeException;
}
