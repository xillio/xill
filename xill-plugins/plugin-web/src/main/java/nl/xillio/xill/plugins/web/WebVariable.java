package nl.xillio.xill.plugins.web;

import nl.xillio.xill.api.components.MetadataExpression;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Defines an interface for a WebVariable
 *
 */
public abstract class WebVariable implements MetadataExpression {

	/**
	 * @return Returns a {@link WebElement}.
	 */
	public abstract WebElement getElement();

	/**
	 * @return Returns a {@link WebDriver}.
	 */
	public abstract WebDriver getDriver();

}
