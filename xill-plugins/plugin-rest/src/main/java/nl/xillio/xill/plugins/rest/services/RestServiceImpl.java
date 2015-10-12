package nl.xillio.xill.plugins.rest.services;

import java.io.IOException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.rest.data.Content;
import nl.xillio.xill.plugins.rest.data.Options;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import com.google.inject.Singleton;

/**
 * This class is the main implementation of the {@link RestService}
 *
 * @author Zbynek Hochmann
 */

@Singleton
public class RestServiceImpl implements RestService {

	private Content processRequest(Request request, final Options options, final Content body) {
		try {
			// set-up request options
			if (options.getTimeout() != 0) {
				request.connectTimeout(options.getTimeout()).socketTimeout(options.getTimeout());
			}

			Executor executor = Executor.newInstance();
			options.doAuth(executor);

			// set body
			if ((body != null) && (!body.isEmpty())) {
				request.bodyString(body.getContent(), body.getType());
			}

			// do request
			try {
				return new Content(executor.execute(request).returnContent());
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
}