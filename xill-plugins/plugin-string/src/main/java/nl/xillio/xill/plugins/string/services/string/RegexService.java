package nl.xillio.xill.plugins.string.services.string;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.google.inject.ImplementedBy;

import nl.xillio.xill.plugins.string.constructs.RegexConstruct;

/**
 * 
 */
@ImplementedBy(RegexServiceImpl.class)
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

}
