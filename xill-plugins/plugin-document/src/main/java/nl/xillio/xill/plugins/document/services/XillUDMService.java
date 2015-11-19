package nl.xillio.xill.plugins.document.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.udm.DocumentID;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;
import org.bson.Document;

/**
 * This class is responsible for communicating with the udm.
 *
 * @author Thomas Biesaart
 */
@Singleton
public class XillUDMService implements XillUDMPersistence {
    private static final String DEFAULT_IDENTITY = "xill";
    private final ConnectionPool connectionPool;

    @Inject
    public XillUDMService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void save(UDMDocument document) throws PersistException, ValidationException {
        if (document.isNew()) {
            create(document);
        } else {
            throw new NotImplementedException("I have not implemented this yet");
        }
    }

    @Override
    public String getJSON(String id) {
        UDMService service = getUdmService(DEFAULT_IDENTITY);
        DocumentID docId = service.get(id);
        String json = service.toJSON(docId);
        service.release(docId);
        return json;
    }

    @Override
    public void delete(String stringValue) throws PersistenceException {
        UDMService service = getUdmService(DEFAULT_IDENTITY);
        service.delete(new Document("_id", stringValue));
    }

    public void create(UDMDocument document) throws PersistException, ValidationException {
        DocumentBuilder builder = getUdmService(DEFAULT_IDENTITY).create();
        document.applyTo(builder);
        persist(builder);
    }

    private void persist(DocumentBuilder builder) throws PersistException, ValidationException {
        DocumentID id = builder.commit();

        try {
            getUdmService(DEFAULT_IDENTITY).persist(id);
        } catch (PersistenceException e) {
            throw new PersistException("Failed to persist document " + id.toString() + " to the database", e);
        } catch (ModelException e) {
            throw new ValidationException("Validation failed: " + e.getMessage(), e);
        }
    }

    /**
     * This method is here for future changes.
     * At some point we might want to allow multiple databases (for multiple projects).
     *
     * @param identity the project identity
     * @return the connection
     */
    private UDMService getUdmService(String identity) {
        return connectionPool.get(identity);
    }
}
