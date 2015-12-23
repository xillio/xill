package nl.xillio.xill.plugins.collection.services.duplicate;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.collection.CollectionXillPlugin;
import nl.xillio.xill.services.XillService;

/**
 * This interface represents the duplicate operation for the {@link CollectionXillPlugin}
 *
 * @author Sander Visser
 */
@ImplementedBy(DuplicateImpl.class)
public interface Duplicate extends XillService {

    /**
     * Receives either a LIST or an OBJECT and returns a deepcopy of it.
     *
     * @param input the list
     * @return the copy of the list.
     */
    public Object duplicate(Object input);

}
