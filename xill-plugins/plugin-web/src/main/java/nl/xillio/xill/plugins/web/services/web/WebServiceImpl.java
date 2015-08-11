package nl.xillio.xill.plugins.web.services.web;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import nl.xillio.xill.plugins.web.data.CookieVariable;
import nl.xillio.xill.plugins.web.data.NodeVariable;
import nl.xillio.xill.plugins.web.data.Options;
import nl.xillio.xill.plugins.web.data.PageVariable;
import nl.xillio.xill.plugins.web.data.PhantomJSPool;
import nl.xillio.xill.plugins.web.data.WebVariable;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
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
	public void click(final WebVariable node) throws StaleElementReferenceException {
		WebElement webElement = node.getElement();
		webElement.click();
	}

	@Override
	public void moveToElement(final WebVariable var) {
		WebDriver page = var.getDriver();
		WebElement element = var.getElement();
		new Actions(page).moveToElement(element).perform();
	}

	@Override
	public String getTagName(final WebVariable var) {
		WebElement element = var.getElement();
		return element.getTagName();
	}

	@Override
	public String getAttribute(final WebVariable var, final String name) {
		WebElement element = var.getElement();
		return element.getAttribute(name);
	}

	@Override
	public String getText(final WebVariable var) {
		WebElement element = var.getElement();
		return element.getText();
	}

	@Override
	public List<WebVariable> findElementsWithCssPath(final WebVariable var, final String cssPath) throws InvalidSelectorException {
		SearchContext node;
		if (var instanceof PageVariable) {
			node = var.getDriver();
		} else {
			node = var.getElement();
		}
		List<WebElement> searchResults = node.findElements(By.cssSelector(cssPath));
		List<WebVariable> result = new ArrayList<>();
		for (WebElement element : searchResults) {
			result.add(new NodeVariable(null, element));
		}
		return result;
	}

	@Override
	public List<WebVariable> findElementsWithXpath(final WebVariable var, final String xpath) throws InvalidSelectorException {
		SearchContext node;
		if (var instanceof PageVariable) {
			node = var.getDriver();
		} else {
			node = var.getElement();
		}
		List<WebElement> searchResults = node.findElements(By.xpath(xpath));
		List<WebVariable> result = new ArrayList<>();
		for (WebElement element : searchResults) {
			result.add(new NodeVariable(null, element));
		}
		return result;
	}

	@Override
	public String getCurrentUrl(final WebVariable var) {
		WebDriver driver = var.getDriver();
		return driver.getCurrentUrl();
	}

	@Override
	public void clear(final WebVariable var) {
		WebElement element = var.getElement();
		element.clear();
	}

	@Override
	public void sendKeys(final WebVariable var, final String key) throws Exception {
		WebElement element = var.getElement();
		element.sendKeys(key);
	}

	@Override
	public String getTitle(final WebVariable var) {
		WebDriver driver = var.getDriver();
		return driver.getTitle();
	}

	@Override
	public Set<Cookie> getCookies(final WebVariable var) {
		WebDriver driver = var.getDriver();
		return driver.manage().getCookies();
	}

	@Override
	public String getName(final Cookie cookie) {
		return cookie.getName();
	}

	@Override
	public String getDomain(final Cookie cookie) {
		return cookie.getDomain();
	}

	@Override
	public String getPath(final Cookie cookie) {
		return cookie.getPath();
	}

	@Override
	public String getValue(final Cookie cookie) {
		return cookie.getValue();
	}

	@Override
	public void deleteCookieNamed(final WebVariable var, final String name) {
		WebDriver driver = var.getDriver();
		driver.manage().deleteCookieNamed(name);
	}

	@Override
	public void deleteCookies(final WebVariable var) throws Exception {
		WebDriver driver = var.getDriver();
		driver.manage().deleteAllCookies();
	}

	@Override
	public File getScreenshotAsFile(final WebVariable var) {
		WebDriver driver = var.getDriver();
		PhantomJSDriver castedDriver = (PhantomJSDriver) driver;
		return castedDriver.getScreenshotAs(OutputType.FILE);
	}

	@Override
	public boolean isSelected(final WebVariable var) {
		WebElement element = var.getElement();
		return element.isSelected();
	}

	@Override
	public void switchToFrame(final WebVariable page, final WebVariable elem) {
		WebElement element = elem.getElement();
		WebDriver driver = page.getDriver();
		driver.switchTo().frame(element);

	}

	@Override
	public void switchToFrame(final WebVariable var, final String element) {
		WebDriver driver = var.getDriver();
		driver.switchTo().frame(element);
	}

	@Override
	public void switchToFrame(final WebVariable var, final int element) {
		WebDriver driver = var.getDriver();
		driver.switchTo().frame(element);
	}

	@Override
	public void addCookie(final WebVariable var, final CookieVariable cookieVar) {
		WebDriver driver = var.getDriver();
		Cookie cookie = new Cookie(cookieVar.getName(), cookieVar.getValue(), cookieVar.getDomain(), cookieVar.getPath(), cookieVar.getExpireDate(), false);
		driver.manage().addCookie(cookie);
	}

	@Override
	public NodeVariable createNodeVariable(final WebVariable page, final WebVariable element) {
		WebDriver newDriver = page.getDriver();
		WebElement newElement = element.getElement();
		return new NodeVariable(newDriver, newElement);
	}

	@Override
	public void httpGet(final WebVariable var, final String url) throws ClassCastException, MalformedURLException {
		PhantomJSDriver driver = (PhantomJSDriver) var.getDriver();
		if (getRef(url) != null) {
			driver.get("about:blank");
		}
		driver.get(url);
	}

	/**
	 * Creates an URL and gets the anchor (also known as the "reference") of this URL.
	 *
	 * @param url
	 *        The url.
	 * @return
	 *         the reference of this url.
	 * @throws MalformedURLException
	 */
	private String getRef(final String url) throws MalformedURLException {
		URL newURL = new URL(url);
		if(newURL == null || !checkSupportedURL(newURL)){
			throw new MalformedURLException();
		}
		return newURL.getRef();
	}
	
	private boolean checkSupportedURL(URL url){
		return url.getProtocol().toLowerCase().equals("http") ||url.getProtocol().toLowerCase().equals("https");
	}

	@Override
	public PageVariable createPage(final Options options) {
		if (options == null) {
			throw new NullPointerException("Options cannot be null.");
		}
		WebDriver driver = options.createDriver();
		return new PageVariable(driver, null);
	}

	@Override
	public void setDriverOptions(final WebVariable var, final int timeOut) {
		WebDriver driver = var.getDriver();
		// setting up bigger size of viewport (default is 400x300)
		driver.manage().window().setSize(new Dimension(1920, 1080));

		// page load timeout
		if (timeOut != 0) {
			driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.MILLISECONDS);
		} else {
			// set infinite timeout
			driver.manage().timeouts().pageLoadTimeout(-1, TimeUnit.MILLISECONDS);
		}

	}

	@Override
	public void quit(final WebVariable var) {
		WebDriver driver = var.getDriver();
		driver.quit();
	}

	@Override
	public WebVariable getPageFromPool(final PhantomJSPool pool, final Options options) {
		return pool.get(pool.createIdentifier(options), this).getPage();
	}
}
