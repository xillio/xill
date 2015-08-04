package nl.xillio.xill.plugins.web.constructs;

import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

/**
 * Returns the info about currently loaded web page
 */
public class PageInfoConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page) -> process(page),
			new Argument("page"));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @return list of string variable
	 */
	public static MetaExpression process(final MetaExpression pageVar) {

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Node PAGE type expected!");
		}
		// else

		try {

			WebDriver page = getPageDriver(pageVar);
			LinkedHashMap<String, MetaExpression> list = new LinkedHashMap<>();

			list.put("url", fromValue(page.getCurrentUrl()));
			list.put("title", fromValue(page.getTitle()));

			LinkedHashMap<String, MetaExpression> cookies = new LinkedHashMap<>();
			for (Cookie cookie : page.manage().getCookies()) {
				cookies.put(cookie.getName(), makeCookie(cookie));
			}
			list.put("cookies", fromValue(cookies));
			return fromValue(list);

		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}

}
