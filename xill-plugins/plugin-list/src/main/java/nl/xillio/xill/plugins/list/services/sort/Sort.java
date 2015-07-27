package nl.xillio.xill.plugins.list.services.sort;

import nl.xillio.xill.plugins.list.ListXillPlugin;
import nl.xillio.xill.services.XillService;

/**
 *
 * This interface represents the sort operation for the {@link ListXillPlugin}
 * 
 * @author Sander Visser
 *
 */
public interface Sort extends XillService {

	/**
	 * Sorts a LIST or an OBJECT.
	 * 
	 * @param input
	 *        the list
	 * @param recursive
	 *        whether it should sort lists inside the list
	 * @param onKeys
	 *        whether it should sort by key
	 * @return the sorted list
	 */
	public Object asSorted(Object input, boolean recursive, boolean onKeys);

}
