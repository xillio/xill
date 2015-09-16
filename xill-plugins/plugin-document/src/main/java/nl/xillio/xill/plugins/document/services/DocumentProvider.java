package nl.xillio.xill.plugins.document.services;

import org.bson.Document;

/**
 * This interface represents an object that can be serialized to a BSON document
 */
public interface DocumentProvider {
	/**
	 * Serialize this object into a BSON document.
	 *
	 * @return the document, not null
	 */
	Document buildDocument();
}
