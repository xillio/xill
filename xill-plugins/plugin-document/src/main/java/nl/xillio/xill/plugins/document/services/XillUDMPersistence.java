package nl.xillio.xill.plugins.document.services;

import com.google.inject.ImplementedBy;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;

import java.util.Map;

@ImplementedBy(XillUDMService.class)
public interface XillUDMPersistence {
    String save(UDMDocument document) throws PersistException, ValidationException;

    Map<?, ?> getMap(String id);

    void delete(String stringValue) throws PersistenceException;

    void loadDecorators(String json);

    void persistContentType(String contentType, String json) throws PersistenceException;
}
