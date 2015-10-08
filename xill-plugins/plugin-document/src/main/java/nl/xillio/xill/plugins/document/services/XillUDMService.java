package nl.xillio.xill.plugins.document.services;

import java.util.List;
import java.util.Map;

<<<<<<< HEAD
import nl.xillio.udm.DocumentID;
import org.bson.Document;

=======
>>>>>>> origin/develop
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.services.XillService;

import org.bson.Document;

/**
 * Service for doing operations on the UDM from Xill.
 *
 * @author Geert Konijnendijk
 * @author Luca Scalzotto
 */
public interface XillUDMService extends XillService {
	enum Section {
		TARGET, SOURCE;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}

		/**
		 * Get the section that belongs to a certain name.
		 *
		 * @param name
		 *        the name of the section
		 * @return the section
		 * @throws IllegalArgumentException
		 *         if no valid name was provided.
		 */
		public static Section of(final String name) {
			if (name.isEmpty() || "target".equalsIgnoreCase(name)) {
				return TARGET;
			} else if ("source".equalsIgnoreCase(name)) {
				return SOURCE;
			}
			throw new IllegalArgumentException("No valid section found for [" + name + "] choose either \"target\" or \"source\"");
		}
	}

	/**
	 * Get all versions for a specific document.
	 * This lists all versions on the target revisions.
	 *
	 * @param documentID
	 *        the id of the document
	 * @return a list of version ids
	 */
	List<String> getVersions(String documentID);

	/**
	 * Get all versions for a specific document.
	 *
	 * @param documentID
	 *        the id of the document
	 * @param section
	 *        "target" or "source"
	 * @return a list of version ids
	 */
	List<String> getVersions(String documentID, Section section);

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
	Map<String, Map<String, Object>> get(String documentId, String versionId, Section section);

	/**
	 * Remove all documents that match a certain filter.
	 *
	 * @param filter
	 * @return
	 */
	long removeWhere(Document filter) throws PersistenceException;

	/**
	 * Remove all versions with id in section of entry that matches a certain filter.
	 *
	 * @param filter
	 *        the filter
	 * @param version
	 *        the version
	 * @param section
	 *        the section
	 * @return the number of edited entries
	 * @throws PersistenceException
	 *         if the query fails
	 */
	long removeWhere(Document filter, String version, Section section) throws PersistenceException;

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
	public void update(String documentId, Map<String, Map<String, Object>> body, String versionId, Section section) throws PersistenceException;
	
	/**
	 * Insert a document into the database.
	 * 
	 * @param contentType The content type of the document.
	 * @param body The body of the document.
	 * @return The ID of the inserted document.
	 * @throws PersistenceException 
	 */
	public DocumentID create(String contentType, Map<String, Map<String, Object>> body) throws PersistenceException;

	/**
	 * Remove a document or a specific version of a document.
	 *
	 * @param documentId
	 *        ID of the document
	 * @param versionId
	 *        ID of the version
	 * @param section
	 *        "target" or "source"
	 * @throws PersistenceException
	 *         if the document could not be persisted
	 */
	public void remove(String documentId, String versionId, Section section) throws PersistenceException;
}
