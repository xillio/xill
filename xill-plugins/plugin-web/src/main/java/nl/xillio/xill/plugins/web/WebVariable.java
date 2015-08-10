package nl.xillio.xill.plugins.web;

import nl.xillio.xill.api.components.MetadataExpression;
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
