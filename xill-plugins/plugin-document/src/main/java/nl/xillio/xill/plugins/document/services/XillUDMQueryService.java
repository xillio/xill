package nl.xillio.xill.plugins.document.services;

import com.google.inject.ImplementedBy;
import nl.xillio.udm.exceptions.PersistenceException;
import org.bson.Document;

import java.util.Iterator;
import java.util.Map;

/**
 * This interface represents an object that can query the udm.
 *
 * @author Thomas Biesaart
 */
@ImplementedBy(XillUDMService.class)
public interface XillUDMQueryService {
    /**
     * Find all document that match a filter.
     *
     * @param filter the filter
     * @param sort   the sort query
     * @return a map iterator
     * @throws PersistenceException
     */
    Iterator<Map<String, Object>> findMapWhere(Document filter, Document sort) throws PersistenceException;
}
