package nl.xillio.xill.docgen.exceptions;


/**
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class ParsingException extends Exception {
	public ParsingException(String message) {
		super(message);
	}
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
