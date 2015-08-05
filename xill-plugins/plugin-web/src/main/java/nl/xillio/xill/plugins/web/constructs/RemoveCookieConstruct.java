package nl.xillio.xill.plugins.web.constructs;

import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.WebDriver;

/**
 * Removes cookie from a currently loaded page context
 */
public class RemoveCookieConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, cookie) -> process(page, cookie, webService),
			new Argument("page"),
			new Argument("cookie"));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param cookieVar
	 *        input variable - string (cookie name) or list of strings or boolean
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression cookieVar, final WebService webService) {

		if (cookieVar.isNull()) {
			return NULL;
		}
		// else

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. PAGE type expected!");
		}
		// else

		WebDriver driver = getPageDriver(pageVar);

		try {

			if (cookieVar.getType() == LIST) {
				@SuppressWarnings("unchecked")
				List<MetaExpression> list = (List<MetaExpression>) cookieVar.getValue();
				for (MetaExpression cookie : list) {
					webService.deleteCookieNamed(driver, cookie.getStringValue());
				}
			} else {
				webService.deleteCookieNamed(driver, cookieVar.getStringValue());
			}
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
		return NULL;
	}
}
