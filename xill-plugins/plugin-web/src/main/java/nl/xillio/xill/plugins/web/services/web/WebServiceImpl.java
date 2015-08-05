package nl.xillio.xill.plugins.web.services.web;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.SearchContext;
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
	@Override
	public List<WebElement> findElements(SearchContext node, String cssPath) {
		return node.findElements(By.cssSelector(cssPath));
	}

	@Override
	public String getCurrentUrl(WebDriver driver) {
		return driver.getCurrentUrl();
	}

	@Override
	public WebElement driverToElement(WebDriver driver) throws ClassCastException {
		return (WebElement) driver;
	}

	@Override
	public void clear(WebElement element) {
		element.clear();
	}

	@Override
	public void sendKeys(WebElement element, String key) {
		element.sendKeys(key);	
	}

	@Override
	public String getTitle(WebDriver driver) {
		return driver.getTitle();
	}

	@Override
	public Set<Cookie> getCookies(WebDriver driver) {
		return driver.manage().getCookies();
	}

		@Override
		public String getName(Cookie cookie) {
			return cookie.getName();
		}

		@Override
		public String getDomain(Cookie cookie) {
			return cookie.getDomain();
		}

		@Override
		public String getPath(Cookie cookie) {
			return cookie.getPath();
		}

		@Override
		public String getValue(Cookie cookie) {
			return cookie.getValue();
		}
}
