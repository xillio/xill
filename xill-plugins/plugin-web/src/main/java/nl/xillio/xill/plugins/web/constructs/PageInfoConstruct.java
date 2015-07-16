package nl.xillio.xill.plugins.web.constructs;

import java.util.LinkedHashMap;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PageVariable;

public class PageInfoConstruct extends Construct {

	@Override
	public String getName() {
		return "pageInfo";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(PageInfoConstruct::process, new Argument("page"));
	}

	public static MetaExpression process(final MetaExpression pageVar) {

		if (!PageVariable.checkType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Node PAGE type expected!");
		}
		// else

		try {

			WebDriver page = PageVariable.getDriver(pageVar);
			LinkedHashMap<String, MetaExpression> list = new LinkedHashMap<>();

			list.put("url", fromValue(page.getCurrentUrl()));
			list.put("title", fromValue(page.getTitle()));

			LinkedHashMap<String, MetaExpression> cookies = new LinkedHashMap<>();
			for (Cookie cookie : page.manage().getCookies()) {
				cookies.put(cookie.getName(), PageVariable.makeCookie(cookie));
			}
			list.put("cookies", fromValue(cookies));
			return fromValue(list);

		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}

}
