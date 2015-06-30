package nl.xillio.xill.plugins.selenium;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NodeVariable
{
	private static String name = "Selenium:node";
	
	public static MetaExpression create(final WebDriver driver, final WebElement element) {
		MetaExpression var = new AtomicExpression(element.getAttribute("outerHTML"));
		var.storeMeta(name);
		var.storeMeta(element);
		var.storeMeta(driver);
		return var;
	}
	
	public static WebElement get(final MetaExpression var) {
		return var.getMeta(WebElement.class);
	}
	
	public static boolean checkType(final MetaExpression var) {
		String metaName = var.getMeta(String.class);
		return ( (metaName != null) && (metaName.equals(NodeVariable.name)) && (var.getMeta(WebElement.class) != null) && (var.getMeta(WebDriver.class) != null) );
	}
	
	public static WebDriver getDriver(final MetaExpression var) {
		return var.getMeta(WebDriver.class);
	}
}