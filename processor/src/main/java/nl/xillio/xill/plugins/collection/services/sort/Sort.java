package nl.xillio.xill.plugins.collection.services.sort;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.collection.CollectionXillPlugin;
import nl.xillio.xill.services.XillService;

/**
 *
 * This interface represents the sort operation for the {@link CollectionXillPlugin}
 * 
 * @author Sander Visser
 *
 */
@ImplementedBy(SortImpl.class)
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
	 * @param reverse
	 * 				if true it sorts lowest first    
	 *    
	 * @return the sorted list
	 */
	public Object asSorted(Object input, boolean recursive, boolean onKeys, boolean reverse);

}
