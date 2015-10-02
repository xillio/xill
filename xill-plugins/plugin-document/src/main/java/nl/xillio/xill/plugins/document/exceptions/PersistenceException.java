package nl.xillio.xill.plugins.document.exceptions;

/**
 * This Exception is generally thrown when something goes wrong inside a persistence
 *
 * @author Thomas Biesaart
 */
public class PersistenceException extends Exception {

	/**
	 * Create a new PersistenceException
	 *
	 * @param message the message to describe the exception with
	 */
	public PersistenceException(String message) {
		super(message);
	}

	/**
	 * Create a new PersistenceException with a cause
	 *
	 * @param message the message to describe the exception with
	 * @param cause   the cause
	 */
	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}
}
