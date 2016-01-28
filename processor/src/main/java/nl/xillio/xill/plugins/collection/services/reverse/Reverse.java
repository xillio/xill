package nl.xillio.xill.plugins.collection.services.reverse;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.collection.CollectionXillPlugin;
import nl.xillio.xill.services.XillService;

/**
 * This interface represents the reverse operation for the {@link CollectionXillPlugin}
 *
 * @author Sander
 */
@ImplementedBy(ReverseImpl.class)
public interface Reverse extends XillService {

    /**
     * Receives either a LIST or an OBJECT and returns the reversed version.
     *
     * @param input     the list
     * @param recursive whether lists inside the list should be reversed too.
     * @return the reversed list.
     */
    public Object asReversed(Object input, boolean recursive);

}
