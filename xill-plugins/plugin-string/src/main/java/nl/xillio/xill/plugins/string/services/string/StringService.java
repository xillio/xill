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
	 * @param haystack
	 *        The string we're checking.
	 * @param needle
	 *        The string we're searching.
	 * @return
	 *         A boolean wheter the haystack string ends with the needle.
	 */
	public boolean endsWith(String haystack, String needle);

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
	 *         The index of the first occurrance of the needle higher than the given index.
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

	/**
	 * Recieves a string and returns repeated a few times.
	 * 
	 * @param value
	 *        The string we're repeating.
	 * @param repeat
	 *        An int indicating how many times we're repeating.
	 * @return
	 *         A string with the value repeated.
	 */
	public String repeat(String value, int repeat);

	/**
	 * Replaces each substring of the needle in the haystack with the replacement string.
	 * 
	 * @param haystack
	 *        The string we're altering.
	 * @param needle
	 *        The substrings we're replacing.
	 * @param replacement
	 *        The replacement string.
	 * @return
	 *         The haystack string with all the needles replaced.
	 */
	public String replaceAll(String haystack, String needle, String replacement);

	/**
	 * Replaces the first substring of the needle in the haystack with the replacement string.
	 * 
	 * @param haystack
	 *        The string we're altering.
	 * @param needle
	 *        The substrings we're replacing.
	 * @param replacement
	 *        The replacement string.
	 * @return
	 *         The haystack string with the first needle replaced.
	 */
	public String replaceFirst(String haystack, String needle, String replacement);

	/**
	 * Recieves a haystack string and splits it in the needle string.
	 * 
	 * @param haystack
	 *        The haystack string.
	 * @param needle
	 *        The needle string.
	 * @return
	 *         An array of substrings.
	 */
	public String[] split(String haystack, String needle);

	/**
	 * Recieves a haystack string and returns wheter it starts with the given needle string.
	 * 
	 * @param haystack
	 *        The string we're checking.
	 * @param needle
	 *        The string we're searching.
	 * @return
	 *         Returns wheter the haystack string starts with the needle.
	 */
	public boolean startsWith(String haystack, String needle);

	/**
	 * Recieves a string and returns in lowercased.
	 * 
	 * @param toLower
	 *        The string to lower.
	 * @return
	 *         The provided string lowercased.
	 */
	public String toLowerCase(String toLower);

	/**
	 * Recieves a string and returns it uppercased.
	 * 
	 * @param toUpper
	 *        The string to upper.
	 * @return
	 *         The provided string uppercased.
	 */
	public String toUpperCase(String toUpper);

	/**
	 * Recieves a string and returns a trimmed version.
	 * 
	 * @param toTrim
	 *        the string to trim.
	 * @return
	 *         the trimmed version of the string.
	 */
	public String trim(String toTrim);

	/**
	 * wraps a single line of text, identifying words by ' '
	 * 
	 * @param text
	 *        The text we're wrapping, may be null.
	 * @param width
	 *        The column to wrap the words at, less than 1 is treated as 1.
	 * @param wrapLongWords
	 *        True if long words (such as URLs) should be wrapped.
	 * @return
	 *         Returns the wrapped text.
	 */
	public String wrap(String text, int width, boolean wrapLongWords);
	
	/**
	 * Returns a substring of a given string at the indices of start and end.
	 * @param text
	 * 					The main string.
	 * @param start
	 * 					The start index.
	 * @param end
	 * 					The end index.
	 * @return
	 * 					The substring.
	 */
	public String subString(String text, int start, int end);
	
	/**
	 * Converts the string argument into an array of bytes.
	 * @param text
	 * 					The string we're convering.
	 * @return
	 * 					An array of bytes.
	 */
	public byte[] parseBase64Binary(String text);
	
	/**
	 * Returns a string which represents the printed form of the data.
	 * @param data
	 * 					The data we want to convert.
	 * @return
	 * 					A string which can be printed.
	 */
	public String printBase64Binary(byte[] data);

}
