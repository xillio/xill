package nl.xillio.xill.plugins.document.services;

import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.services.XillService;
import org.bson.Document;

import java.util.Map;

/**
 * Service for doing operations on the UDM from Xill.
 *
 * @author Geert Konijnendijk
 * @author Luca Scalzotto
 */
public interface XillUDMService extends XillService {

	/**
	 * Get all decorators of a specific document version.
	 *
	 * @param documentId ID of the document
	 * @param versionId  ID of the version
	 * @param section    "target" or "source"
	 * @return A map with the keys being decorator names and the values being a map mapping from field names to field values
	 */
	public Map<String, Map<String, Object>> get(String documentId, String versionId, String section);

	/**
	 * Get all documents matching a certain filter.
	 *
	 * @param filter  the filter
	 * @param version the version of the document to grab
	 * @param section the section to check for the version
	 * @return the requested revision
	 */
	Iterable<Map<String, Map<String, Object>>> findWhere(Document filter, String version, String section) throws PersistenceException;
}
