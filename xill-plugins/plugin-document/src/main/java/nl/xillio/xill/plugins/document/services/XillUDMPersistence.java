package nl.xillio.xill.plugins.document.services;

import com.google.inject.ImplementedBy;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;

import java.util.Map;

/**
 * This interface represents an object that can perform persistence operations on the UDM.
 *
 * @author Thomas Biesaart
 */
@ImplementedBy(XillUDMService.class)
public interface XillUDMPersistence {
    /**
     * Save a document to the UDM.
     *
     * @param document the document
     * @return the id of the document
     * @throws PersistException    if saving failed
     * @throws ValidationException if there was a constraint violation
     */
    String save(UDMDocument document) throws PersistException, ValidationException;

    /**
     * Get a map representation of the document with a given id.
     *
     * @param id the id
     * @return the map
     * @throws nl.xillio.udm.exceptions.DocumentNotFoundException if the document did not exist in the persistence
     */
    Map<String, Object> getMap(String id);

    /**
     * Remove a document from the persistence.
     *
     * @param id the id of the document
     * @throws PersistenceException
     */
    void delete(String id) throws PersistenceException;

    /**
     * Load decorators into the cache.
     *
     * @param json the json representation
     */
    void loadDecorators(String json);

    /**
     * Persist a content type and its decorators to the database.
     *
     * @param contentType the name of the content type
     * @param json        the json
     * @throws PersistenceException
     */
    void persistContentType(String contentType, String json) throws PersistenceException;
}
