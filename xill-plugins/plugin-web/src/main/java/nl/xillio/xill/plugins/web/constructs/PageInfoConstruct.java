package nl.xillio.xill.plugins.web.constructs;

import java.util.LinkedHashMap;

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
 * Returns the info about currently loaded web page
 */
public class PageInfoConstruct extends Construct {
	@Inject
	private PageVariableService pageVariableService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page) -> process(page, pageVariableService),
			new Argument("page"));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @return list of string variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final PageVariableService pageVariableService) {

		if (!pageVariableService.checkType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Node PAGE type expected!");
		}
		// else

		try {

			WebDriver page = pageVariableService.getDriver(pageVar);
			LinkedHashMap<String, MetaExpression> list = new LinkedHashMap<>();

			list.put("url", fromValue(page.getCurrentUrl()));
			list.put("title", fromValue(page.getTitle()));

			LinkedHashMap<String, MetaExpression> cookies = new LinkedHashMap<>();
			for (Cookie cookie : page.manage().getCookies()) {
				cookies.put(cookie.getName(), pageVariableService.makeCookie(cookie));
			}
			list.put("cookies", fromValue(cookies));
			return fromValue(list);

		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}

}
