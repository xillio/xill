package nl.xillio.xill.plugins.document.services;

import java.util.Map;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.services.XillService;

/**
 * 
 * Service for doing operations on the UDM from Xill.
 * 
 * @author Geert Konijnendijk
 * @author Luca Scalzotto
 *
 */
public interface XillUDMService extends XillService {

	/**
	 * Get all decorators of a specific document version.
	 * 
	 * @param documentId
	 *        ID of the document
	 * @param versionId
	 *        ID of the version
	 * @param section
	 *        "target" or "source"
	 * @return A map with the keys being decorator names and the values being a map mapping from field names to field values
	 */
	public Map<String, Map<String, Object>> get(String documentId, String versionId, String section);
	
	/**
	 * Insert a document into the database.
	 * 
	 * @param contentType The content type of the document.
	 * @param body The body of the document.
	 * @return The ID of the inserted document.
	 * @throws PersistenceException 
	 */
	public DocumentID create(String contentType, Map<String, Map<String, Object>> body) throws PersistenceException;
}
