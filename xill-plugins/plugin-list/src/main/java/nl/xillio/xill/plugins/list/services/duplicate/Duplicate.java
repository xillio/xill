package nl.xillio.xill.plugins.list.services.duplicate;

import nl.xillio.xill.plugins.list.ListXillPlugin;
import nl.xillio.xill.services.XillService;

/**
 * This interface represents the duplicate operation for the {@link ListXillPlugin}
 *
 * @author Sander Visser
 *
 */
public interface Duplicate extends XillService {

	/**
	 * Receives either a LIST or an OBJECT and returns a deepcopy of it.
	 * 
	 * @param input
	 *        the list
	 * @return the copy of the list.
	 */
	public Object duplicate(Object input);

}
