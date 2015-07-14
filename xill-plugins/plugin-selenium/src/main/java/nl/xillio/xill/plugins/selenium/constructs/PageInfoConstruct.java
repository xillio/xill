package nl.xillio.xill.plugins.selenium.constructs;

import java.util.LinkedHashMap;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.selenium.PageVariable;

public class PageInfoConstruct implements Construct {

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
	    LinkedHashMap<String, MetaExpression> list = new LinkedHashMap<String, MetaExpression>();

	    list.put("url", ExpressionBuilder.fromValue(page.getCurrentUrl()));
	    list.put("title", ExpressionBuilder.fromValue(page.getTitle()));

	    LinkedHashMap<String, MetaExpression> cookies = new LinkedHashMap<String, MetaExpression>();
	    for (Cookie cookie : page.manage().getCookies()) {
		cookies.put(cookie.getName(), PageVariable.makeCookie(cookie));
	    }
	    list.put("cookies", ExpressionBuilder.fromValue(cookies));
	    return ExpressionBuilder.fromValue(list);

	} catch (Exception e) {
	    throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
	}
    }

}