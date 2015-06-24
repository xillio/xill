package nl.xillio.xill.api.errors;

/**
 * This {@link Exception} is generally thrown when an unimplemented part of the software is called
 */
public class NotImplementedException extends RuntimeException{
	private static final long serialVersionUID = -452816988262437288L;

	/**
	 * Create a new {@link NotImplementedException}
	 * @param message
	 */
	public NotImplementedException(String message) {
		super(message);
	}
}
