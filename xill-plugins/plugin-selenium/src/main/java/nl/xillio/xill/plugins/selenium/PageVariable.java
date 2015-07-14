package nl.xillio.xill.plugins.selenium;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;

/**
 * @author Zbynek Hochmann This class represents PAGE "pseudo" variable, it
 *         encapsulates basic operations.
 */
public class PageVariable {
    private static String name = "Selenium:page";

    public static MetaExpression create(final PhantomJSPool.Entity item) {
	MetaExpression var = new AtomicExpression(item.getDriver().getCurrentUrl());
	var.storeMeta(name);
	var.storeMeta(item);
	return var;
    }

    public static PhantomJSPool.Entity get(final MetaExpression var) {
	return var.getMeta(PhantomJSPool.Entity.class);
    }

    public static WebDriver getDriver(final MetaExpression var) {
	return var.getMeta(PhantomJSPool.Entity.class).getDriver();
    }

    /**
     * @param var
     *            MetaExpression (any)
     * @return true if it's pseudovariable of PAGE type
     */
    public static boolean checkType(final MetaExpression var) {
	String metaName = var.getMeta(String.class);
	return metaName != null && metaName.equals(PageVariable.name) && var.getMeta(PhantomJSPool.Entity.class) != null;
    }

    public static MetaExpression makeCookie(final Cookie cookie) {
	LinkedHashMap<String, MetaExpression> map = new LinkedHashMap<String, MetaExpression>();
	map.put("name", ExpressionBuilder.fromValue(cookie.getName()));
	map.put("domain", ExpressionBuilder.fromValue(cookie.getDomain()));
	map.put("path", ExpressionBuilder.fromValue(cookie.getPath()));
	map.put("value", ExpressionBuilder.fromValue(cookie.getValue()));

	if (cookie.getExpiry() != null) {
	    map.put("expires", ExpressionBuilder.fromValue(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S").format(cookie.getExpiry())));
	}

	return ExpressionBuilder.fromValue(map);
    }

}