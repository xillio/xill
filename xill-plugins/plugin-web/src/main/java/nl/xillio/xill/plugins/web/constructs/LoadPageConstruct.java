package nl.xillio.xill.plugins.web.constructs;

import java.net.MalformedURLException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.Options;
import nl.xillio.xill.plugins.web.OptionsFactory;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import com.google.inject.Inject;

/**
 * @author Zbynek Hochmann
 *         Loads the new page via PhantomJS process and holds the context of a page
 */
public class LoadPageConstruct extends PhantomJSConstruct implements AutoCloseable {
	@Inject
	private OptionsFactory optionsFactory;

	private static final PhantomJSPool pool = new PhantomJSPool(10);

	static {
		Options.cleanUnusedPJSExe();
		Options.extractNativeBinary();
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(url, options) -> process(url, options, optionsFactory, webService),
			new Argument("url", ATOMIC),
			new Argument("options", NULL, OBJECT));
	}

	/**
	 * @param urlVar
	 *        string variable - page URL
	 * @param optionsVar
	 *        list variable - options for loading the page (see CT help for details)
	 * @param optionsFactory
	 *        The factory which will create the options.
	 * @param webService
	 *        The service we're using.
	 * @return PAGE variable
	 */
	public static MetaExpression process(final MetaExpression urlVar, final MetaExpression optionsVar, final OptionsFactory optionsFactory, final WebService webService) {

		String url = urlVar.getStringValue();

		Options options;
		try {
			// processing input options
			options = optionsFactory.processOptions(optionsVar);
			WebVariable item = webService.getPageFromPool(pool, options);
			if (item == null) {
				throw new RobotRuntimeException("Loadpage error - PhantomJS pool is fully used and cannot provide another instance!");
			}
			webService.httpGet(item, url);
			return createPage(item, webService);
		} catch (RobotRuntimeException e) {
			throw e;
		} catch (ClassCastException e) {
			throw new RobotRuntimeException("Failed to execute httpGet.");
		} catch (MalformedURLException e) {
			throw new RobotRuntimeException("Malformed URL during httpGet.");
		}
	}

	@Override
	public void close() throws Exception {
		// it will dispose entire PJS pool (all PJS processes will be terminated and temporary PJS files deleted)
		pool.dispose();
	}
}
