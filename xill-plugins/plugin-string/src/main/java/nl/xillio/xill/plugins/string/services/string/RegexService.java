package nl.xillio.xill.plugins.string.services.string;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;

import com.google.inject.ImplementedBy;

/**
 *
 */
@ImplementedBy(RegexServiceImpl.class)
public interface RegexService {

	/**
	 * Hashes the input with the MD5 hash.
	 *
	 * @param input
	 *        The string to be hashed.
	 * @return
	 *         A hashed string.
	 * @throws NoSuchAlgorithmException
	 */
	public String createMD5Construct(String input) throws NoSuchAlgorithmException;

	/**
	 * Escapes the XML.
	 *
	 * @param text
	 *        The text that requires escaping.
	 * @return
	 *         An escaped text.
	 */
	public String escapeXML(String text);

	/**
	 *
	 * @param regex
	 *        the pattern
	 * @param value
	 *        the haystack
	 * @param timeout
	 *        in seconds
	 * @return the matcher
	 * @throws FailedToGetMatcherException 
	 * @throws IllegalArgumentException
	 * @throws PatternSyntaxException
	 */
	public Matcher getMatcher(String regex, String value, int timeout) throws FailedToGetMatcherException, IllegalArgumentException, PatternSyntaxException;

	/**
	 * Attempts to match an entire region to a pattern.
	 *
	 * @param matcher
	 *        The Matcher we're matching with.
	 * @return
	 *         Returns wheter the matching succeeded.
	 */
	public boolean matches(Matcher matcher);

	/**
	 * Lets the matcher replace all subsequences of a given input with the replacement.
	 *
	 * @param matcher
	 *        The matcher we're using.
	 * @param replacement
	 *        The replacement string.
	 * @return
	 *         Returns a modified string.
	 */
	public String replaceAll(Matcher matcher, String replacement);

	/**
	 * Lets the matcher replace the first subsequence of a given input with the replacement.
	 *
	 * @param matcher
	 *        The matcher we're using.
	 * @param replacement
	 *        The replacement string.
	 * @return
	 *         Returns a modified string.
	 */
	public String replaceFirst(Matcher matcher, String replacement);

	/**
	 * Returns a list of matches form a matcher.
	 *
	 * @param matcher
	 *        The Matcher we're using.
	 * @return
	 *         Returns a list of strings with matches.
	 */
	public List<String> tryMatch(Matcher matcher);

	/**
	 * Returns a list of matches from a matcher
	 *
	 * @param matcher
	 *        The Matcher we're using.
	 * @return
	 *         Returns a list of strings with matches or null.
	 */
	public List<String> tryMatchElseNull(Matcher matcher);

	/**
	 * Unescapes a text containing an XML entity.
	 *
	 * @param text
	 *        The text that requires unescaping.
	 * @param passes
	 *        The number of passes the decoder runs.
	 * @return
	 *         An unescaped text.
	 */
	public String unescapeXML(String text, int passes);

}
