package nl.xillio.xill.plugins.document.services;

import nl.xillio.xill.services.XillService;

import java.util.List;
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
	Map<String, Map<String, Object>> get(String documentId, String versionId, String section);

	/**
	 * Get all versions for a specific document.
	 * This lists all versions on the target revisions.
	 *
	 * @param documentID the id of the document
	 * @return a list of version ids
	 */
	List<String> getVersions(String documentID);


	/**
	 * Get all versions for a specific document.
	 *
	 * @param documentID the id of the document
	 * @param section    "target" or "source"
	 * @return a list of version ids
	 */
	List<String> getVersions(String documentID, String section);
}
