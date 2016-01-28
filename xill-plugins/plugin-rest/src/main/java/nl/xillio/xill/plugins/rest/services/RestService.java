package nl.xillio.xill.plugins.rest.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.rest.RESTXillPlugin;
import nl.xillio.xill.plugins.rest.data.Content;
import nl.xillio.xill.plugins.rest.data.MultipartBody;
import nl.xillio.xill.plugins.rest.data.Options;

/**
 * This interface represents some of the operations for the {@link RESTXillPlugin}.
 *
 * @author Zbynek Hochmann
 */

@ImplementedBy(RestServiceImpl.class)
public interface RestService {

    /**
     * Do GET Rest command
     *
     * @param url     URL address for Rest service
     * @param options request options
     * @return response content
     */
    Content get(final String url, final Options options);

    /**
     * Do PUT Rest command
     *
     * @param url     URL address for Rest service
     * @param options request options
     * @param body    optional body content
     * @return response content
     */
    Content put(final String url, final Options options, final Content body);

    /**
     * Do POST Rest command
     *
     * @param url     URL address for Rest service
     * @param options request options
     * @param body    optional body content
     * @return response content
     */
    Content post(final String url, final Options options, final Content body);

    /**
     * Do DELETE Rest command
     *
     * @param url     URL address for Rest service
     * @param options request options
     * @return response content
     */
    Content delete(final String url, final Options options);

    /**
     * Do HEAD Rest command
     *
     * @param url     URL address for Rest service
     * @param options request options
     * @return response content
     */
    Content head(final String url, final Options options);

    /**
     * Create empty multipart body object
     */
    MultipartBody bodyCreate();

    /**
     * Add file as a part of the multiple content
     *
     * @param body     MultipartBody object holding all content parts
     * @param name     name of the content part
     * @param fileName filenamepath
     */
    void bodyAddFile(final MultipartBody body, final String name, final String fileName);

    /**
     * Add text as a part of the multiple content
     *
     * @param body MultipartBody object holding all content parts
     * @param name name of the content part
     * @param text text content
     */
    void bodyAddText(final MultipartBody body, final String name, final String text);
}
