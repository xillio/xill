package nl.xillio.xill.plugins.web.services.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.WebVariable;
import nl.xillio.xill.plugins.web.PhantomJSPool;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.google.inject.Singleton;

/**
 * The implementation of the {@link WebService} interface.
 */
@Singleton
public class WebServiceImpl implements WebService {

	@Override
	public void click(WebVariable node) throws StaleElementReferenceException {
		WebElement webElement = node.getElement();
		webElement.click();
	}

	@Override
	public void moveToElement(WebVariable var) {
		WebDriver page = var.getDriver();
		WebElement element = var.getElement();
		new Actions(page).moveToElement(element).perform();
	}

	@Override
	public String getTagName(WebVariable var) {
		WebElement element = var.getElement();
		return element.getTagName();
	}

	@Override
	public String getAttribute(WebVariable var, final String name) {
		WebElement element = var.getElement();
		return element.getAttribute(name);
	}

	@Override
	public String getText(WebVariable var) {
		WebElement element = var.getElement();
		return element.getText();
	}
	
	@Override
	public List<WebVariable> findElementsWithCssPath(WebVariable var, String cssPath) throws InvalidSelectorException {
		SearchContext node = var.getElement();
		List<WebElement> searchResults = node.findElements(By.cssSelector(cssPath));
		List<WebVariable> result = new ArrayList<>();
		for(WebElement element : searchResults){
			result.add(new NodeVariable(null, element));
		}
		return result;
	}
	
	@Override
	public List<WebVariable> findElementsWithXpath(WebVariable var, String xpath) throws InvalidSelectorException {
		SearchContext node = var.getElement();
		List<WebElement> searchResults = node.findElements(By.xpath(xpath));
		List<WebVariable> result = new ArrayList<>();
		for(WebElement element : searchResults){
			result.add(new NodeVariable(null, element));
		}
		return result;
	}

	@Override
	public String getCurrentUrl(WebVariable var) {
		WebDriver driver = var.getDriver();
		return driver.getCurrentUrl();
	}

	private WebElement driverToElement(WebDriver driver) throws ClassCastException {
		return (WebElement) driver;
	}

	@Override
	public void clear(WebVariable var) {
		WebElement element = var.getElement();
		element.clear();
	}

	@Override
	public void sendKeys(WebVariable var, String key) {
		WebElement element = var.getElement();
		element.sendKeys(key);	
	}

	@Override
	public String getTitle(WebVariable var) {
		WebDriver driver = var.getDriver();
		return driver.getTitle();
	}

	@Override
	public Set<Cookie> getCookies(WebVariable var) {
		WebDriver driver = var.getDriver();
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

		@Override
		public void deleteCookieNamed(WebVariable var, String name) {
			WebDriver driver = var.getDriver();
			driver.manage().deleteCookieNamed(name);	
		}

		@Override
		public void deleteCookies(WebVariable var) {
			WebDriver driver = var.getDriver();
			driver.manage().deleteAllCookies();
		}

		@Override
		public File getScreenshotAsFile(WebVariable var) {
			WebDriver driver = var.getDriver();
			PhantomJSDriver castedDriver = (PhantomJSDriver) driver;
			return castedDriver.getScreenshotAs(OutputType.FILE);
		}

		@Override
		public boolean isSelected(WebVariable var) {
			WebElement element = var.getElement();
			return element.isSelected();
		}

		@Override
		public void switchToFrame(WebVariable page, WebVariable elem) {
			WebElement element = elem.getElement();
			WebDriver driver = page.getDriver();
			driver.switchTo().frame(element);
			
		}

		@Override
		public void switchToFrame(WebVariable var, String element) {
			WebDriver driver = var.getDriver();
			driver.switchTo().frame(element);		
		}

		@Override
		public void switchToFrame(WebVariable var, int element) {
			WebDriver driver = var.getDriver();
			driver.switchTo().frame(element);			
		}

		@Override
		public void addCookie(WebVariable var, Cookie cookie) {
			WebDriver driver = var.getDriver();
			driver.manage().addCookie(cookie);
		}

		@Override
		public NodeVariable createNodeVariable(WebVariable page, WebVariable element) {
			WebDriver newDriver = page.getDriver();
			WebElement newElement = element.getElement();
			return new NodeVariable(newDriver, newElement);
		}
}
