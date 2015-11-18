package nl.xillio.xill.plugins.document.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.document.data.UDMDocument;
import nl.xillio.xill.plugins.document.exceptions.PersistException;
import nl.xillio.xill.plugins.document.exceptions.ValidationException;

@ImplementedBy(XillUDMService.class)
public interface XillUDMPersistence {
    void save(UDMDocument document) throws PersistException, ValidationException;
}
