package nl.xillio.xill.plugins.string.services.string;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * This interface represents a collection of operations using a shared regex Matcher
 */
@ImplementedBy(RegexServiceImpl.class)
public interface RegexService {

    /**
     * @param regex   the pattern
     * @param value   the haystack
     * @param timeout in seconds
     * @return the matcher
     * @throws FailedToGetMatcherException
     * @throws IllegalArgumentException
     * @throws PatternSyntaxException
     */
    Matcher getMatcher(String regex, String value, int timeout) throws FailedToGetMatcherException;

    /**
     * Attempts to match an entire region to a pattern.
     *
     * @param matcher The Matcher we're matching with.
     * @return Returns wheter the matching succeeded.
     */
    boolean matches(Matcher matcher);

    /**
     * Lets the matcher replace all subsequences of a given input with the replacement.
     *
     * @param matcher     The matcher we're using.
     * @param replacement The replacement string.
     * @return Returns a modified string.
     */
    String replaceAll(Matcher matcher, String replacement);

    /**
     * Lets the matcher replace the first subsequence of a given input with the replacement.
     *
     * @param matcher     The matcher we're using.
     * @param replacement The replacement string.
     * @return Returns a modified string.
     */
    String replaceFirst(Matcher matcher, String replacement);

    /**
     * Returns a list of matches form a matcher.
     *
     * @param matcher The Matcher we're using.
     * @return Returns a list of strings with matches.
     */
    List<String> tryMatch(Matcher matcher);

    /**
     * Returns a list of matches from a matcher
     *
     * @param matcher The Matcher we're using.
     * @return Returns a list of strings with matches or null.
     */
    List<String> tryMatchElseNull(Matcher matcher);


}
