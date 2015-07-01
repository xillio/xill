package nl.xillio.xill.plugins.selenium;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class NodeVariable
{
	private WebDriver driver;
	private WebElement element;
	
	private NodeVariable(WebDriver driver, WebElement element) {
		this.driver = driver;
		this.element = element;
	}
	
	private static String name = "Selenium:node";
	
	public static MetaExpression create(final WebDriver driver, final WebElement element) {
		MetaExpression var = new AtomicExpression(element.getAttribute("outerHTML"));
		var.storeMeta(name);
		var.storeMeta(new NodeVariable(driver, element));
		return var;
	}
	
	public static WebElement get(final MetaExpression var) {
		return var.getMeta(NodeVariable.class).element;
	}
	
	public static boolean checkType(final MetaExpression var) {
		String metaName = var.getMeta(String.class);
		return ( (metaName != null) && (metaName.equals(NodeVariable.name)) && (var.getMeta(NodeVariable.class) != null) );
	}
	
	public static WebDriver getDriver(final MetaExpression var) {
		return var.getMeta(NodeVariable.class).driver;
	}
}