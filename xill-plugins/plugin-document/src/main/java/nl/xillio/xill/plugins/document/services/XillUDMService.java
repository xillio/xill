package nl.xillio.xill.plugins.document.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import nl.xillio.udm.DocumentID;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.interfaces.FindResult;
import nl.xillio.udm.services.UDMService;
import nl.xillio.udm.util.TransformationIterable;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;
import org.bson.Document;

import java.util.Iterator;
import java.util.Map;

/**
 * This class is responsible for communicating with the udm.
 *
 * @author Thomas Biesaart
 */
@Singleton
public class XillUDMService implements XillUDMPersistence, XillUDMQueryService {
    private static final String DEFAULT_IDENTITY = "xill";
    private final ConnectionPool connectionPool;

    @Inject
    public XillUDMService(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public String save(UDMDocument document) throws PersistException, ValidationException {
        if (document.isNew()) {
            return create(document);
        } else {
            return update(document);
        }
    }

    @Override
    public Map<?, ?> getMap(String id) {
        UDMService service = getUdmService(DEFAULT_IDENTITY);
        DocumentID docId = service.get(id);
        String json = service.toJSON(docId);
        DBObject obj = (DBObject) JSON.parse(json);
        service.release(docId);
        return obj.toMap();
    }

    @Override
    public void delete(String stringValue) throws PersistenceException {
        UDMService service = getUdmService(DEFAULT_IDENTITY);
        service.delete(new Document("_id", stringValue));
    }

    public String create(UDMDocument document) throws PersistException, ValidationException {
        DocumentBuilder builder = getUdmService(DEFAULT_IDENTITY).create();
        document.applyTo(builder);
        return persist(builder);
    }

    public String update(UDMDocument document) throws PersistException, ValidationException {
        UDMService service = getUdmService(DEFAULT_IDENTITY);
        DocumentID id = service.get(document.getId());
        DocumentBuilder builder = service.document(id);
        document.applyTo(builder);
        return persist(builder);
    }

    String persist(DocumentBuilder builder) throws PersistException, ValidationException {
        DocumentID id = builder.commit();
        UDMService service = getUdmService(DEFAULT_IDENTITY);

        try {
            service.persist(id);
        } catch (PersistenceException e) {
            throw new PersistException("Failed to persist document " + id.toString() + " to the database", e);
        } catch (ModelException e) {
            service.release(id);
            throw new ValidationException("Validation failed: " + e.getMessage(), e);
        }

        return id.get();
    }

    /**
     * This method is here for future changes.
     * At some point we might want to allow multiple databases (for multiple projects).
     *
     * @param identity the project identity
     * @return the connection
     */
    UDMService getUdmService(String identity) {
        return connectionPool.get(identity);
    }


    @Override
    public Iterator<Map<?, ?>> findMapWhere(Document filter) throws PersistenceException {
        UDMService service = getUdmService(DEFAULT_IDENTITY);
        try {
            FindResult result = service.find(filter);
            return new TransformationIterable<>(
                    result.iterator(),
                    id -> {
                        String json = service.toJSON(id);
                        DBObject obj = (DBObject) JSON.parse(json);
                        service.release(id);
                        return obj.toMap();
                    },
                    noCleanUp -> {

                    }
            );
        } catch (MongoException e) {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public long delete(Document filterDoc) throws PersistenceException {
        UDMService service = getUdmService(DEFAULT_IDENTITY);
        return service.delete(filterDoc);
    }
}
