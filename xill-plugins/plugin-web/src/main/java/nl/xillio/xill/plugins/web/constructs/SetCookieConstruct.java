package nl.xillio.xill.plugins.web.constructs;

import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.CookieFactory;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import com.google.inject.Inject;

/**
 * Set cookie in a currently loaded page context
 */
public class SetCookieConstruct extends PhantomJSConstruct {
	@Inject
	private CookieFactory cookieFactory;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, cookies) -> process(page, cookies, cookieFactory, webService),
			new Argument("page"),
			new Argument("cookies", LIST, OBJECT));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param cookiesVar
	 *        input string variable - associated array or list of associated arrays (see CT help for details)
	 * @param cookieFactory
	 *        The factory which builds cookies.
	 * @param webService
	 *        The service we're using for accesing the web.
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression cookiesVar, final CookieFactory cookieFactory, final WebService webService) {

		if (cookiesVar.isNull()) {
			return NULL;
		}

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Page NODE type expected!");
		}
		// else

		WebVariable driver = getPage(pageVar);

		if (cookiesVar.getType() == LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) cookiesVar.getValue();
			for (MetaExpression cookie : list) {
				processCookie(driver, cookie, cookieFactory, webService);
			}
		}
		else {
			processCookie(driver, cookiesVar, cookieFactory, webService);
		}

		return NULL;
	}

	private static void processCookie(final WebVariable driver, final MetaExpression cookie, final CookieFactory cookieFactory, final WebService webService) {
		if (cookie.getType() == OBJECT)
		{
			@SuppressWarnings("unchecked")
			Map<String, MetaExpression> cookieMap = (Map<String, MetaExpression>) cookie.getValue();
			cookieFactory.setCookie(driver, cookieMap, webService);
		}
		else {
			throw new RobotRuntimeException("A value which was not an OBJECT found.");
		}
	}
}
