package nl.xillio.xill.plugins.rest.services;

import com.google.inject.Singleton;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.rest.data.Content;
import nl.xillio.xill.plugins.rest.data.MultipartBody;
import nl.xillio.xill.plugins.rest.data.Options;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * This class is the main implementation of the {@link RestService}
 *
 * @author Zbynek Hochmann
 */

@Singleton
public class RestServiceImpl implements RestService {

    Executor createExecutor(boolean isInsecure) {
        CloseableHttpClient httpClient = null;
        if (isInsecure) {
            //create custom httpClient that basically turns hostname verification off.
            httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        }
        return Executor.newInstance(httpClient);
    }

    Content processRequest(final Request request, final Options options, final Content body) {

        try {
            // set-up request options
            if (options.getTimeout() != 0) {
                request.connectTimeout(options.getTimeout()).socketTimeout(options.getTimeout());
            }

            // set HTTP header items
            options.setHeaders(request);

            // create executor
            Executor executor = createExecutor(options.isInsecure());

            // set authentication
            options.setAuth(executor);

            // set body
            if ((body != null) && (!body.isEmpty())) {
                body.setToRequest(request);
            }

            // do request
            try {
                Response responseContent = executor.execute(request);
                HttpResponse httpResponse = responseContent.returnResponse();


                return new Content(httpResponse);
            } catch (IOException e) {
                throw new RobotRuntimeException("Request error: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Content get(final String url, final Options options) {
        Request request = Request.Get(url);
        return this.processRequest(request, options, null);
    }

    @Override
    public Content put(final String url, final Options options, final Content body) {
        Request request = Request.Put(url);
        return this.processRequest(request, options, body);
    }

    @Override
    public Content post(final String url, final Options options, final Content body) {
        Request request = Request.Post(url);
        return this.processRequest(request, options, body);
    }

    @Override
    public Content delete(final String url, final Options options) {
        Request request = Request.Delete(url);
        return this.processRequest(request, options, null);
    }

    @Override
    public Content head(final String url, final Options options) {
        Request request = Request.Head(url);
        return this.processRequest(request, options, null);
    }

    @Override
    public MultipartBody bodyCreate() {
        return new MultipartBody();
    }

    @Override
    public void bodyAddFile(final MultipartBody body, final String name, final String fileName) {
        body.addFile(name, fileName);
    }

    @Override
    public void bodyAddText(final MultipartBody body, final String name, final String text) {
        body.addText(name, text);
    }
}
