package nl.xillio.xill.plugins.web.constructs;

import java.net.MalformedURLException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.Options;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.TimeoutException;

/**
 * @author Zbynek Hochmann
 *         Loads the new page via PhantomJS process and holds the context of a page
 */
public class LoadPageConstruct extends PhantomJSConstruct implements AutoCloseable {

	private static final PhantomJSPool pool = new PhantomJSPool(10);

	static {
		Options.cleanUnusedPJSExe();
		Options.extractNativeBinary();
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(url, options) -> process(url, options, webService),
			new Argument("url", ATOMIC),
			new Argument("options", NULL, OBJECT));
	}

	/**
	 * @param urlVar
	 *        string variable - page URL
	 * @param optionsVar
	 *        list variable - options for loading the page (see CT help for details)
	 * @return PAGE variable
	 */
	public static MetaExpression process(final MetaExpression urlVar, final MetaExpression optionsVar, final WebService webService) {

		String url = urlVar.getStringValue();

		Options options = new Options();
		try {
			// processing input options
			options.processOptions(optionsVar);
		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to convert LoadPage options '" + optionsVar.getValue(), e);
		}

		// getting properly configured webdriver
		WebVariable item = webService.getPageFromPool(pool, options);
		if (item == null) {
			throw new RobotRuntimeException("Loadpage error - PhantomJS pool is fully used and cannot provide another instance!");
		}

		try {
			webService.httpGet(item, url);
		} catch (TimeoutException e) {
			throw new RobotRuntimeException("Loadpage timeout", e);
		} catch (MalformedURLException e) {
			throw new RobotRuntimeException("Malformed url given: " + url);
		}
		return createPage(item, webService);
	}

	@Override
	public void close() throws Exception {
		// it will dispose entire PJS pool (all PJS processes will be terminated and temporary PJS files deleted)
		pool.dispose();
	}
}
