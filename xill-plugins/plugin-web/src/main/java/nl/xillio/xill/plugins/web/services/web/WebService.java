package nl.xillio.xill.plugins.web.services.web;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import nl.xillio.xill.api.components.MetaExpression;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.inject.ImplementedBy;

/**
 * Provides an interface for a webService.
 */
@ImplementedBy(WebServiceImpl.class)
public interface WebService {

	/**
	 * Tries to click a {@link WebElement}.
	 * 
	 * @param element
	 *        The {@link WebElement} we're trying to click on.
	 * @throws StaleElementReferenceException
	 *         Throws an exception when the element is stale.
	 */
	public void click(WebElement element) throws StaleElementReferenceException;

	/**
	 * Sets focus on an element on a given page.
	 * 
	 * @param page
	 *        The page as a {@link WebDriver}.
	 * @param element
	 *        The element as a {@link WebElement}.
	 */
	public void moveToElement(WebDriver page, WebElement element);

	/**
	 * Returns the tag name of a {@link WebElement}.
	 * 
	 * @param element
	 *        The element we want to know the tag name of.
	 * @return
	 *         The name of the tag of the element.
	 */
	public String getTagName(WebElement element);

	/**
	 * Returns a given attribute of a {@link WebElement}.
	 * 
	 * @param element
	 *        The element we want to know the attribute of.
	 * @param name
	 *        The name of the attribute we want to retrieve.
	 * @return
	 *         The attribute of the element.
	 */
	public String getAttribute(WebElement element, String name);

	/**
	 * Returns the text contained in a {@link WebElement}.
	 * 
	 * @param element
	 *        The element from which we want to extract text.
	 * @return
	 *         Returns the text in the element.
	 */
	public String getText(WebElement element);
	
	/**
	 * Returns the title of a {@link WebDriver}.
	 * @param driver
	 * 					The driver we want the title from.
	 * @return
	 * 				The title.
	 */
	public String getTitle(WebDriver driver);
	
	
	/**
	 * Finds all the elements in a node given a cssPath.
	 * @param node
	 * 					The node we're searching.
	 * @param cssPath
	 * 					The cssPath we're using.
	 * @return
	 * 				Returns a list of elements: {@link WebElement}.
	 */
	public List<WebElement> findElements(SearchContext node, String cssPath );
	
	/**
	 * Returns the current URL of a {@link WebDriver}.
	 * @param driver
	 * 					The driver we want the URL from.
	 * @return
	 * 				A string which is the current URL.
	 */
	public String getCurrentUrl(WebDriver driver);
	
	/**
	 * Recieves a {@link WebDriver} and returns a {@link WebElement}
	 * @param driver
	 * 					The driver we're casting.
	 * @return
	 * 				The driver as a webelement.
	 * @throws ClassCastException 
	 */
	public WebElement driverToElement(WebDriver driver) throws ClassCastException;
	
	/**
	 * Clears a {@link WebElement}.
	 * @param element
	 * 					The element we want to clear.
	 */
	public void clear(WebElement element);
	
	/**
	 * Simulates sending a key to an {@link WebElement}.
	 * May alter its value.
	 * @param element 
	 * 					the element we're sending the key to.
	 * @param key
	 * 					the key we want to send.
	 */
	public void sendKeys(WebElement element, String key);
	
	/**
	 * Gets the cookies from a {@link WebDriver}.
	 * @param driver
	 * 						the driver we're retrieving from.
	 * @return
	 * 				A set of Cookies.
	 */
	public Set<Cookie> getCookies(WebDriver driver);
	
		
		/**
		 * Gets the name of a {@link Cookie}
		 * @param cookie
		 * 					The cookie.
		 * @return
		 * 				The name of the cookie.
		 */
		public String getName(Cookie cookie);
		
		/**
		 * Gets the domain of a {@link Cookie}
		 * @param cookie
		 * 					The cookie.
		 * @return
		 * 				The domain of the cookie.
		 */
		public String getDomain(Cookie cookie);
		
		/**
		 * Gets the path of a {@link Cookie}
		 * @param cookie
		 * 					The cookie.
		 * @return	
		 * 				The path of the cookie.
		 */
		public String getPath(Cookie cookie);
		
		/**
		 * Gets the value of a {@link Cookie}.
		 * @param cookie
		 * 						The cookie.
		 * @return
		 * 				The value of the cookie.
		 */
		public String getValue(Cookie cookie);
		
		/**
		 * Deletes the cookie in the {@link WebDriver}  with the given name.
		 * @param driver
		 * 						The driver we're deleting on.
		 * @param name
		 * 						The name of the cookie we're deleting.
		 */
		public void deleteCookieNamed(WebDriver driver, String name);
		
		/**
		 * Deletes all cookies on a {@link WebDriver}.
		 * @param driver
		 * 					The driver we're deleting cookies on.
		 */
		public void deleteCookies(WebDriver driver);
		
		/**
		 * Makes a screenshot on a {@link WebDriver} and returns it as file.
		 * @param driver
		 * 						The driver we're screenshotting.
		 * @return
		 * 				A file containing the screenshot.
		 */
		public File getScreenshotAsFile(WebDriver driver);

}
