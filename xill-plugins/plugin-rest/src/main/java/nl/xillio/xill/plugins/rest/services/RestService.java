package nl.xillio.xill.plugins.rest.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.rest.RestXillPlugin;
import nl.xillio.xill.plugins.rest.data.Content;
import nl.xillio.xill.plugins.rest.data.Options;

/**
 * This interface represents some of the operations for the {@link RestXillPlugin}.
 *
 * @author Zbynek Hochmann
 */

@ImplementedBy(RestServiceImpl.class)
public interface RestService {

	/**
	 * Do GET Rest command
	 * 
	 * @param url 			URL address for Rest service
	 * @param options 	request options
	 * @param body			optional body content
	 * @return					response content
	 */
	Content get(final String url, final Options options, final Content body);

	/**
	 * Do PUT Rest command
	 * 
	 * @param url 			URL address for Rest service
	 * @param options 	request options
	 * @param body			optional body content
	 * @return					response content
	 */	
	Content put(final String url, final Options options, final Content body);

	/**
	 * Do POST Rest command
	 * 
	 * @param url 			URL address for Rest service
	 * @param options 	request options
	 * @param body			optional body content
	 * @return					response content
	 */	
	Content post(final String url, final Options options, final Content body);

	/**
	 * Do DELETE Rest command
	 * 
	 * @param url 			URL address for Rest service
	 * @param options 	request options
	 * @param body			optional body content
	 * @return					response content
	 */	
	Content delete(final String url, final Options options, final Content body);

	/**
	 * Do HEAD Rest command
	 * 
	 * @param url 			URL address for Rest service
	 * @param options 	request options
	 * @param body			optional body content
	 * @return					response content
	 */	
	Content head(final String url, final Options options, final Content body);
}
