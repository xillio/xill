package nl.xillio.xill.plugins.document.services;

import com.google.inject.ImplementedBy;
import nl.xillio.udm.exceptions.PersistenceException;
import org.bson.Document;

import java.util.Iterator;

/**
 * This interface represents an object that can query the udm.
 */
@ImplementedBy(XillUDMService.class)
public interface XillUDMQueryService {
    Iterator<String> findJsonWhere(Document filter) throws PersistenceException;

    long delete(Document filterDoc) throws PersistenceException;
}
