package nl.xillio.xill.plugins.selenium;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

public class PageInfoConstruct implements Construct {

	@Override
	public String getName() {
		return "pageinfo";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			PageInfoConstruct::process,
			new Argument("page"));
	}

	public static MetaExpression process(final MetaExpression pageVar) {

		if (!PageVariable.checkType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Node PAGE type expected!");
		}
		//else
		
		try {

			WebDriver page = PageVariable.get(pageVar).getDriver();
			HashMap<String, MetaExpression> list = new HashMap<String, MetaExpression>();
			
			list.put("url", ExpressionBuilder.fromValue(page.getCurrentUrl()));
			list.put("title", ExpressionBuilder.fromValue(page.getTitle()));

			HashMap<String, MetaExpression> cookies = new HashMap<String, MetaExpression>();
			for(Cookie cookie: page.manage().getCookies()) {
				cookies.put(cookie.getName(), PageVariable.makeCookie(cookie));
			}
			list.put("cookies", ExpressionBuilder.fromValue(cookies));
			return ExpressionBuilder.fromValue(list);
			
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}

}