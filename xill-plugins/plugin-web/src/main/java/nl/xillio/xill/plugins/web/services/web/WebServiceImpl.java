package nl.xillio.xill.plugins.web.services.web;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * The implementation of the {@link WebService} interface.
 *
 */
public class WebServiceImpl implements WebService {

	@Override
	public void click(final WebElement element) throws StaleElementReferenceException {
		element.click();
	}

	@Override
	public void moveToElement(final WebDriver page, final WebElement element) {
		new Actions(page).moveToElement(element).perform();
	}

	@Override
	public String getTagName(final WebElement element) {
		return element.getTagName();
	}

	@Override
	public String getAttribute(final WebElement element, final String name) {
		return element.getAttribute(name);
	}

	@Override
	public String getText(final WebElement element) {
		return element.getText();
	}
}
