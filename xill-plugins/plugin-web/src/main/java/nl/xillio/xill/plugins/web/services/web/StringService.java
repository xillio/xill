package nl.xillio.xill.plugins.web.services.web;

import com.google.inject.ImplementedBy;

/**
 * provides an interface for a stringservice.
 *
 */
@ImplementedBy(StringServiceImpl.class)
public interface StringService {
	
  /**
   * Checks if the first string ends with the second.
   *
   * @param haystack The string we're checking.
   * @param needle   The string we're searching.
   * @return A boolean whether the haystack string ends with the needle.
   */
  boolean endsWith(String haystack, String needle);
  
  /**
   * Returns a substring of a given string at the indices of start and end.
   *
   * @param text  The main string.
   * @param start The start index.
   * @param end   The end index.
   * @return The substring.
   * @throws IndexOutOfBoundsException 
   */
  String subString(String text, int start, int end) throws IndexOutOfBoundsException;
  
  /**
   * Tells whether the String matches a given regular expression.
   * @param text
   * 					The String.
   * @param regex
   * 					The regular expression.
   * @return
   * 					Whether the string matches the regex.
   */
  boolean matches(String text, String regex);
  
  /**
   * Returns the last index of a given character in a String.
   * If no could be found, returns -1.
   * @param text
   * 					The text we're searing through
   * @param ch
   * 					The character we're searching.
   * @return
   * 				Returns the last index of the char in the text.
   */
  int lastIndexOf(String text, int ch);

}
