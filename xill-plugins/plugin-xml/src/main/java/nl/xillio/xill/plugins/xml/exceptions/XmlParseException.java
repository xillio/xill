package nl.xillio.xill.plugins.xml.exceptions;

/**
 * An exception to throw when XML parsing error occurs
 * 
 * @author Zbynek Hochmann
 */
public class XmlParseException extends Exception {
	private static final long serialVersionUID = 14589742606197714L;

	/**
	 * @param message	error message
	 */
	public XmlParseException(String message) {
    super(message);
	}
	/**
	 * @param message	error message
	 * @param cause the cause
	 */
	public XmlParseException(String message, Exception cause) {
		super(message, cause);
	}
}


