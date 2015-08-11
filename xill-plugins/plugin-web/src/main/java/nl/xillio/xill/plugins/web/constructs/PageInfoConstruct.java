package nl.xillio.xill.plugins.web.constructs;

import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.Cookie;

/**
 * Returns the info about currently loaded web page
 */
public class PageInfoConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			page -> process(page, webService),
			new Argument("page", ATOMIC));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param webService
	 *        the webService we're using.
	 * @return list of string variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final WebService webService) {
		
		if(pageVar.isNull()){
			return NULL;
		}

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Node PAGE type expected!");
		}
		try {

			WebVariable driver = getPage(pageVar);
			LinkedHashMap<String, MetaExpression> list = new LinkedHashMap<>();

			list.put("url", fromValue(webService.getCurrentUrl(driver)));
			list.put("title", fromValue(webService.getTitle(driver)));

			LinkedHashMap<String, MetaExpression> cookies = new LinkedHashMap<>();
			for (Cookie cookie : webService.getCookies(driver)) {
				cookies.put(webService.getName(cookie), makeCookie(cookie, webService));
			}
			list.put("cookies", fromValue(cookies));
			return fromValue(list);

		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to extract info", e);
		}
	}

}
