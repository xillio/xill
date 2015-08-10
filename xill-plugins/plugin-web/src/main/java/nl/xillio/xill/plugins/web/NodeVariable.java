package nl.xillio.xill.plugins.web;

import nl.xillio.xill.api.components.MetadataExpression;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This class represents NODE "pseudo" variable, it encapsulates basic operations.
 *
 * @author Ivor van der Hoog.
 */
public class NodeVariable extends WebVariable implements MetadataExpression {
	private final WebDriver driver;
	private final WebElement element;

	/**
	 * <p>
	 * The constructor for the NodeVariable.
	 * </p>
	 * <p>
	 * This class represents the NODE "pseudo" variable.
	 * </p>
	 *
	 * @param driver
	 *        The {@link WebDriver} we're using.
	 * @param element
	 *        The {@link WebElement} we're representing the node of.
	 */
	public NodeVariable(final WebDriver driver, final WebElement element) {
		this.driver = driver;
		this.element = element;
	}

	@Override
	public WebDriver getDriver() {
		return driver;
	}

	@Override
	public WebElement getElement() {
		return element;
	}
}
