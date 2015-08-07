package nl.xillio.xill.plugins.web.constructs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.Cookie;

/**
 * Set cookie in a currently loaded page context
 */
public class SetCookieConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, cookies) -> process(page, cookies, webService),
			new Argument("page"),
			new Argument("cookies", LIST, OBJECT));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param cookiesVar
	 *        input string variable - associated array or list of associated arrays (see CT help for details)
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression cookiesVar, final WebService webService) {

		if (cookiesVar.isNull()) {
			return NULL;
		}

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Page NODE type expected!");
		}
		// else

		WebVariable driver = getPage(pageVar);

		if (cookiesVar.getType() == OBJECT) {
			processCookie(driver, cookiesVar, webService);
		} else if (cookiesVar.getType() == LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) cookiesVar.getValue();
			for (MetaExpression cookie : list) {
				processCookie(driver, cookie, webService);
			}
		}

		return NULL;
	}

	private static void processCookie(final WebVariable driver, final MetaExpression cookie, final WebService webService) {
		@SuppressWarnings("unchecked")
		Map<String, MetaExpression> cookieMap = (Map<String, MetaExpression>) cookie.getValue();
		setCookie(driver, cookieMap, webService);
	}

	private static void setCookie(final WebVariable driver, final Map<String, MetaExpression> cookie, final WebService webService) {
		String cookieName = cookie.get("name").getStringValue();
		String cookieDomain = cookie.get("domain").getStringValue();
		String cookiePath = cookie.get("path").getStringValue();
		String cookieValue = cookie.get("value").getStringValue();
		String cookieExpires = cookie.get("expires").getStringValue();

		if (cookieName.equals("null")) {
			throw new RobotRuntimeException("Invalid cookie. Attribute 'name' is empty.");
		}

		if (cookieValue.equals("null")) {
			cookieValue = "";
		}
		if (cookieDomain.equals("null")) {
			cookieDomain = null;
		}
		if (cookiePath.equals("null")) {
			cookiePath = null;
		}

		Date cookieExpiresDate = null;
		if (cookieExpires.equals("null")) {
			cookieExpires = null;
		} else {
			try {
				cookieExpiresDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(cookieExpires);
			} catch (Exception e) {
				throw new RobotRuntimeException("Invalid cookie. Atribute 'expires' does not have the format yyyy-MM-ddThh:mm:ss");
			}
		}

		Cookie c = new Cookie(cookieName, cookieValue, cookieDomain, cookiePath, cookieExpiresDate, false);
		try {
			webService.addCookie(driver, c);
		} catch (Exception e) {
			throw new RobotRuntimeException("Invalid cookie", e);
		}
	}
}
