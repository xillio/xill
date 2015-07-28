package nl.xillio.xill.plugins.system.services.regex;

import java.util.List;

import nl.xillio.xill.plugins.string.StringXillPlugin;

import com.google.inject.ImplementedBy;

/**
 * This interface represents some of the operations for the {@link StringXillPlugin}.
 */
@ImplementedBy(MatchServiceImpl.class)
public interface MatchService {
	
	/**
	 * Checks wheter a list contains an object.
	 * @param list
	 * 					The list we're searching though.
	 * @param needle
	 * 					The item we're searching.
	 * @return
	 * 				A bool wheter the needle is contained in the list.
	 */
	public boolean contains(List<Object> list, Object needle );
	
	/**
	 * Checks wheter the child string is contained in the parent string.
	 * @param parent
	 * 					The parent string.
	 * @param child
	 * 					The child string.
	 * @return
	 * 					Returns true if the child is contained in the parent.
	 */
	public boolean contains(String parent, String child);

}
