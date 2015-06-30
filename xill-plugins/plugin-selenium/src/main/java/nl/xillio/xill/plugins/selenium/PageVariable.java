package nl.xillio.xill.plugins.selenium;

import java.util.HashMap;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class PageVariable
{
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
	
	public static boolean checkType(final MetaExpression var) {
		String metaName = var.getMeta(String.class);
		return ( (metaName != null) && (metaName.equals(PageVariable.name)) && (var.getMeta(PhantomJSPool.Entity.class) != null) );
	}
	
	public static MetaExpression makeCookie(final Cookie cookie) {
		HashMap<String, MetaExpression> map = new HashMap<String, MetaExpression>(); 
		map.put("name", ExpressionBuilder.fromValue(cookie.getName()));
		
		map.put("domain", ExpressionBuilder.fromValue(cookie.getName()));
		map.put("name", ExpressionBuilder.fromValue(cookie.getDomain()));
		map.put("path", ExpressionBuilder.fromValue(cookie.getPath()));
		map.put("value", ExpressionBuilder.fromValue(cookie.getValue()));
/*!!	
		if(cookie.getExpiry() != null) {
			map.put("expires", ExpressionBuilder.fromValue(new DateVariable(cookie.getExpiry())));
			l.addVariable("expires", new DateVariable(cookie.getExpiry()));
		}
*/		
		return ExpressionBuilder.fromValue(map);	
	}

	
}