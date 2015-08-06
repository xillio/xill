package nl.xillio.xill.plugins.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageVariable extends WebVariable {
	private final WebDriver driver;
	private final WebElement element;

	/**
	 * <p>
	 * The constructor for the PageVariable
	 * </p>
	 * <p>
	 * This class represents the Page "pseudo" variable.
	 * </p>
	 * 
	 * @param driver
	 *        The {@link WebDriver} we're using.
	 * @param element
	 *        The {@link WebElement} we're representing the node of.
	 */
	public PageVariable(final WebDriver driver, final WebElement element) {
		this.driver = driver;
		this.element = element;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public WebElement getElement() {
		return element;
	}

}
