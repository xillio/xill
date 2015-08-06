package nl.xillio.xill.plugins.web.services.web;

import com.google.inject.Singleton;

/**
 * the implementation of the {@link StringService}.
 *
 */
@Singleton
public class StringServiceImpl implements StringService {
	
  @Override
  public boolean endsWith(final String first, final String second) {
      return first.endsWith(second);
  }
  
  @Override
  public String subString(final String text, final int start, final int end) throws IndexOutOfBoundsException {
      return text.substring(start, end);
  }

	@Override
	public boolean matches(String text, String regex) {
		return text.matches(regex);
	}

	@Override
	public int lastIndexOf(String text, int ch) {
		return text.lastIndexOf(ch);
	}

}
