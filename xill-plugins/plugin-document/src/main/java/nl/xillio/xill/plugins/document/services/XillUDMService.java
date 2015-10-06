package nl.xillio.xill.plugins.document.services;

import java.util.Map;

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
	 * Update a specific version of a document by setting it to the given body.
	 * 
	 * @param documentId
	 *        The ID of the document to update
	 * @param body
	 *        The contents of the update
	 * @param versionId
	 *        The ID of the version to update
	 * @param section
	 *        The name of the section to update
	 */
	public void update(String documentId, Map<String, Map<String, Object>> body, String versionId, String section) throws PersistenceException;
}
