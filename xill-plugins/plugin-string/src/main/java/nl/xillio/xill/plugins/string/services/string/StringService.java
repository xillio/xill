package nl.xillio.xill.plugins.string.services.string;

import nl.xillio.xill.plugins.string.StringXillPlugin;

import com.google.inject.ImplementedBy;

/**
 * This interface represents some of the operations for the {@link StringXillPlugin}.
 */
@ImplementedBy(StringServiceImpl.class)
public interface StringService {

	/**
	 * Checks wheter the child string is contained in the parent string.
	 * 
	 * @param parent
	 *        The parent string.
	 * @param child
	 *        The child string.
	 * @return
	 *         Returns true if the child is contained in the parent.
	 */
	public boolean contains(String parent, String child);

	/**
	 * Checks if the first string ends with the second.
	 * 
	 * @param first
	 *        The first string.
	 * @param second
	 *        The second string.
	 * @return
	 */
	public boolean endsWith(String first, String second);

	/**
	 * Returns the index of the first occurrance of the needle in the haystack, starting from an index.
	 * 
	 * @param haystack
	 *        The haystack we're searching through.
	 * @param needle
	 *        The needle we're searching.
	 * @param index
	 *        The index from which we start searching.
	 * @return
	 */
	public int indexOf(String haystack, String needle, int index);

	/**
	 * Joins an array of strings by a delimiter.
	 * 
	 * @param input
	 *        The array of strings that need joining.
	 * @param delimiter
	 *        The delimiter.
	 * @return
	 *         A string which is the join of the input.
	 */
	public String join(String[] input, String delimiter);

}
