package nl.xillio.xill.plugins.document.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.udm.DocumentID;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;

/**
 * This class is responsible for communicating with the udm.
 *
 * @author Thomas Biesaart
 */
@Singleton
public class XillUDMService {

    private final UDMService udmService;

    @Inject
    public XillUDMService(UDMService udmService) {
        this.udmService = udmService;
    }

    public void create(UDMDocument document) throws PersistException, ValidationException {
        DocumentBuilder builder = udmService.create();
        document.applyTo(builder);
        persist(builder);
    }

    private void persist(DocumentBuilder builder) throws PersistException, ValidationException {
        DocumentID id = builder.commit();

        try {
            udmService.persist(id);
        } catch (PersistenceException e) {
            throw new PersistException("Failed to persist document " + id.toString() + " to the database", e);
        } catch (ModelException e) {
            throw new ValidationException("Validation failed: " + e.getMessage(), e);
        }
    }
}
