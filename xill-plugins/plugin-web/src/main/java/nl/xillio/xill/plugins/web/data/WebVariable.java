package nl.xillio.xill.plugins.web.data;


import nl.xillio.xill.api.data.MetadataExpression;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Defines an interface for a WebVariable
 */
public interface WebVariable extends MetadataExpression {

	/**
	 * @return Returns a {@link WebElement}.
	 */
	WebElement getElement();

	/**
	 * @return Returns a {@link WebDriver}.
	 */
	WebDriver getDriver();

}
