package nl.xillio.xill.api.errors;

/**
 * This {@link Exception} is generally thrown when something goes wrong during processing
 */
public class RobotRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -2417475642600377589L;
	
	/**
	 * Create a new {@link RobotRuntimeException}
	 * @param message
	 * @param cause
	 */
	public RobotRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Create a new {@link RobotRuntimeException}
	 * @param message
	 */
	public RobotRuntimeException(String message) {
		this(message, null);
	}
	
}
