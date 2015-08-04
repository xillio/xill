package nl.xillio.xill.plugins.web.constructs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PageVariableService;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.google.inject.Inject;

/**
 * Set cookie in a currently loaded page context
 */
public class SetCookieConstruct extends Construct {
	@Inject
	private PageVariableService pageVariableService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, cookies) -> process(page, cookies, pageVariableService),
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
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression cookiesVar, final PageVariableService pageVariableService) {

		if (cookiesVar.isNull()) {
			return NULL;
		}

		if (!pageVariableService.checkType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Page NODE type expected!");
		}
		// else

		WebDriver driver = pageVariableService.getDriver(pageVar);

		if (cookiesVar.getType() == OBJECT) {
			processCookie(driver, cookiesVar);
		} else if (cookiesVar.getType() == LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) cookiesVar.getValue();
			for (MetaExpression cookie : list) {
				processCookie(driver, cookie);
			}
		}

		return NULL;
	}

	private static void processCookie(final WebDriver driver, final MetaExpression cookie) {
		@SuppressWarnings("unchecked")
		Map<String, MetaExpression> cookieMap = (Map<String, MetaExpression>) cookie.getValue();
		setCookie(driver, cookieMap);
	}

	private static void setCookie(final WebDriver driver, final Map<String, MetaExpression> cookie) {
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
			driver.manage().addCookie(c);
		} catch (Exception e) {
			throw new RobotRuntimeException("Invalid cookie", e);
		}
	}
}
