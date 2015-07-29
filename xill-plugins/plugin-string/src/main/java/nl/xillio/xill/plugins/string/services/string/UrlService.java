package nl.xillio.xill.plugins.string.services.string;

import nl.xillio.xill.plugins.string.StringXillPlugin;

import com.google.inject.ImplementedBy;

/**
 * This interface represents some of the operations for the {@link StringXillPlugin}.
 */
@ImplementedBy(UrlServiceImpl.class)
public interface UrlService {

	/**
	 * Recieves an URL and returns a cleaned version.
	 * 
	 * @param url
	 *        The URL to clean.
	 * @return
	 *         The cleaned version of the URL.
	 */
	public String cleanupUrl(final String url);

	/**
	 * Tries to convert the relativeUrl using the pageUrl
	 * 
	 * @param pageUrl
	 *        The pageUrl.
	 * @param relativeUrl
	 *        The relativeUrl.
	 * @return
	 *         A converted relativeUrl as a string.
	 */
	public String tryConvert(final String pageUrl, final String relativeUrl);

}
