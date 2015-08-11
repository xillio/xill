package nl.xillio.xill.plugins.web.constructs;

import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * Removes cookie from a currently loaded page context
 */
public class RemoveCookieConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, cookie) -> process(page, cookie, webService),
			new Argument("page", ATOMIC),
			new Argument("cookie", ATOMIC, LIST));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param cookieVar
	 *        input variable - string (cookie name) or list of strings or boolean
	 * @param webService
	 *        the webservice we're using.
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression cookieVar, final WebService webService) {

		if (cookieVar.isNull()) {
			return NULL;
		}
		// else

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Node PAGE type expected!");
		}
		// else

		WebVariable driver = getPage(pageVar);
		String cookieName = "";
		try {
			if (cookieVar.getType() == LIST) {
				@SuppressWarnings("unchecked")
				List<MetaExpression> list = (List<MetaExpression>) cookieVar.getValue();
				for (MetaExpression cookie : list) {
					cookieName = cookie.getStringValue();
					webService.deleteCookieNamed(driver, cookieName);
				}
			} else {
				cookieName = cookieVar.getStringValue();
				webService.deleteCookieNamed(driver, cookieName);
			}
		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to delete cookie: " + cookieName, e);
		}
		return NULL;
	}
}
