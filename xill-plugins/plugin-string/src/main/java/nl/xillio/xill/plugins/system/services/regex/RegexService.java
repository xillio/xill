package nl.xillio.xill.plugins.system.services.regex;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import nl.xillio.xill.plugins.string.constructs.RegexConstruct;

/**
 * @author Ivor
 *
 */
public interface RegexService {
	
	/**
	 * Tries to find subsequences in an input.
	 * @param regex 
	 * 					The regex with which we're matching.
	 * @param input
	 * 					The input. 
	 * @param timeout 
	 * 					The timeout in msec.
	 * @param regexConstruct 
	 * 					The regexConstruct we're using.
	 * @return
	 * 					Returns a list of strings with matches.
	 */
	public List<String> tryMatch(String regex, String input, int timeout, RegexConstruct regexConstruct);
	
	/**
	 * Escapes the XML.
	 * @param text
	 * 					The text that requires escaping.
	 * @return
	 * 				An escaped text.
	 */
	public String escapeXML(String text);
	
	/**
	 * Unescapes a text containing an XML entity.
	 * @param text
	 * 					The text that requires unescaping.
	 * @param passes 
	 * 					The number of passes the decoder runs.
	 * @return
	 * 				An unescaped text.
	 */
	public String unescapeXML(String text, int passes);
	
	/**
	 * Hashes the input with the MD5 hash.
	 * @param input
	 * 					The string to be hashed.
	 * @return
	 * 				A hashed string.
	 * @throws NoSuchAlgorithmException 
	 */
	public String createMD5Construct(String input) throws NoSuchAlgorithmException;
	
	/**
	 * Checks if the first string ends with the second.
	 * @param first
	 * 					The first string.
	 * @param second
	 * 					The second string.
	 * @return
	 */
	public boolean endsWith(String first, String second);
	
	/**
	 * Returns the index of the first occurrance of the needle in the haystack, starting from an index.
	 * @param haystack
	 * 					The haystack we're searching through.
	 * @param needle
	 * 					The needle we're searching.
	 * @param index
	 * 					The index from which we start searching.
	 * @return
	 */
	public int indexOf(String haystack, String needle, int index);
	
	/**
	 * Joins an array of strings by a delimiter.
	 * @param input
	 * 					The array of strings that need joining.
	 * @param delimiter
	 * 					The delimiter.
	 * @return
	 * 					A string which is the join of the input.
	 */
	public String join(String[] input, String delimiter);

}
