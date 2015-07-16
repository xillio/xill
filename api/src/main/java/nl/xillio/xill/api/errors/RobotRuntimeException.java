package nl.xillio.xill.api.errors;

/**
 * This {@link Exception} is generally thrown when something goes wrong during processing
 */
public class RobotRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -2417475642600377589L;
	
	/**
	 * Create a new {@link RobotRuntimeException}
	 * @param message the message to display
	 * @param cause the exception that caused this
	 */
	public RobotRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Create a new {@link RobotRuntimeException} without a Throwable cause
	 * @param message the message to display
	 */
	public RobotRuntimeException(String message) {
		this(message, null);
	}
	
}
