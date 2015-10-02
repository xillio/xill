package nl.xillio.xill.plugins.document.exceptions;

/**
 * 
 * Thrown when a document version can not be found in the UDM persistence layer.
 * 
 * @author Geert Konijnendijk
 *
 */
public class VersionNotFoundException extends RuntimeException {

	/**
	 * {@inheritDoc}
	 */
	public VersionNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public VersionNotFoundException(String message) {
		super(message);
	}

}
