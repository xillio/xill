package nl.xillio.xill.api.construct;

import java.io.InputStream;

/**
 * This interface represents an object that holds a stream to a documentation file
 */
public interface HelpComponent {
	/**
	 * <p>
	 * Open a stream to the documentation file.
	 * </p>
	 * <b>NOTE: </b> this stream should be closed by an external class
	 * 
	 * @return The stream
	 */
	public InputStream openDocumentationStream();
}
